package no.nav.paw.data.innlop.tjenester.automatiskreaktivering

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import kotlinx.coroutines.runBlocking
import no.nav.paw.data.innlop.AutomatiskReaktivering
import no.nav.paw.data.innlop.AutomatiskReaktiveringSvar
import no.nav.paw.data.innlop.config.Config
import no.nav.paw.data.innlop.config.Topics
import no.nav.paw.data.innlop.utils.asTimestamp
import no.nav.paw.data.innlop.utils.logger
import no.nav.paw.pdl.PdlClient
import no.nav.paw.pdl.PdlException
import no.nav.paw.pdl.hentAktorId
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Produced

fun automatiskReaktiveringDataStream(
    stream: KStream<String, AutomatiskReaktiveringEvent>,
    config: Config,
    pdlClient: PdlClient
) {
    val automatiskReaktiveringStream = stream.filter { _, automatiskReaktiveringEvent ->
        automatiskReaktiveringEvent.type == "AutomatiskReaktivering"
    }

    val automatiskReaktiveringSvarStream = stream.filter { _, automatiskReaktiveringEvent ->
        automatiskReaktiveringEvent.type == "AutomatiskReaktiveringSvar"
    }

    val automatiskReaktiveringSerde = SpecificAvroSerde<AutomatiskReaktivering>()
    automatiskReaktiveringSerde.configure(config.schemaRegistry, false)

    val automatiskReaktiveringSvarSerde = SpecificAvroSerde<AutomatiskReaktiveringSvar>()
    automatiskReaktiveringSvarSerde.configure(config.schemaRegistry, false)

    setupAutomatiskReaktivering(automatiskReaktiveringStream, automatiskReaktiveringSerde, Topics.utlopReaktivering, pdlClient)
    setupAutomatiskReaktiveringSvar(automatiskReaktiveringSvarStream, automatiskReaktiveringSvarSerde, Topics.utlopReaktiveringSvar, pdlClient)
}

fun setupAutomatiskReaktiveringSvar(
    automatiskReaktiveringSvarStream: KStream<String, AutomatiskReaktiveringEvent>,
    automatiskReaktiveringSvarSerde: SpecificAvroSerde<AutomatiskReaktiveringSvar>,
    utlopsTopic: String,
    pdlClient: PdlClient
) {
    automatiskReaktiveringSvarStream
        .mapValues { _, melding ->
            val aktorId = runBlocking { hentAktorId(pdlClient, melding.bruker_id) }
            AutomatiskReaktiveringSvar.newBuilder().apply {
                brukerId = aktorId
                svar = melding.svar
                created = melding.created_at.asTimestamp()
            }.build()
        }
        .peek { _, _ ->
            logger.info("Sender melding til topic: ${Topics.utlopReaktiveringSvar}")
        }
        .to(
            utlopsTopic,
            Produced.with(
                Serdes.String(),
                Serdes.serdeFrom(
                    automatiskReaktiveringSvarSerde.serializer(),
                    automatiskReaktiveringSvarSerde.deserializer()
                )
            )
        )
}

fun setupAutomatiskReaktivering(
    automatiskReaktiveringStream: KStream<String, AutomatiskReaktiveringEvent>,
    automatiskReaktiveringSerde: SpecificAvroSerde<AutomatiskReaktivering>,
    utlopsTopic: String,
    pdlClient: PdlClient
) {
    automatiskReaktiveringStream
        .mapValues { _, melding ->
            val aktorId = runBlocking { hentAktorId(pdlClient, melding.bruker_id) }
            AutomatiskReaktivering.newBuilder().apply {
                brukerId = aktorId
                created = melding.created_at.asTimestamp()
            }.build()
        }
        .peek { _, _ ->
            logger.info("Sender melding til topic: ${Topics.utlopReaktivering}")
        }
        .to(
            utlopsTopic,
            Produced.with(
                Serdes.String(),
                Serdes.serdeFrom(
                    automatiskReaktiveringSerde.serializer(),
                    automatiskReaktiveringSerde.deserializer()
                )
            )
        )
}

private suspend fun hentAktorId(pdlClient: PdlClient, fnr: String): String? {
    var aktorId: String? = null

    try {
        aktorId = runBlocking { pdlClient.hentAktorId(fnr) }
    } catch (ex: PdlException) {
        logger.warn("Kall til PDL feilet. Setter aktørId til 'null'")
    }

    return aktorId
}
