package no.nav.paw.data.innlop.tjenester.automatiskreaktivering

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import kotlinx.coroutines.runBlocking
import no.nav.paw.data.innlop.AutomatiskReaktivering
import no.nav.paw.data.innlop.AutomatiskReaktiveringSvar
import no.nav.paw.data.innlop.config.Config
import no.nav.paw.data.innlop.config.Topics
import no.nav.paw.data.innlop.pdl.hentAktorId
import no.nav.paw.data.innlop.utils.asTimestamp
import no.nav.paw.data.innlop.utils.logger
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Produced

fun automatiskReaktiveringDataStream(
    stream: KStream<String, AutomatiskReaktiveringEvent>,
    config: Config,
    getToken: () -> String
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

    automatiskReaktiveringStream
        .mapValues { _, melding ->
            runBlocking {
                val aktorId = hentAktorId(melding.bruker_id, getToken())

                AutomatiskReaktivering.newBuilder().apply {
                    brukerId = aktorId ?: melding.bruker_id
                    created = melding.created_at.asTimestamp()
                }.build()
            }
        }
        .peek { _, _ ->
            logger.info("Sending message to topic: ${Topics.utlopReaktivering}")
        }
        .to(
            Topics.utlopReaktivering,
            Produced.with(
                Serdes.String(),
                Serdes.serdeFrom(
                    automatiskReaktiveringSerde.serializer(),
                    automatiskReaktiveringSerde.deserializer()
                )
            )
        )

    automatiskReaktiveringSvarStream
        .mapValues { _, melding ->
            AutomatiskReaktiveringSvar.newBuilder().apply {
                brukerId = melding.bruker_id
                svar = melding.svar
                created = melding.created_at.asTimestamp()
            }.build()
        }
        .peek { _, _ ->
            logger.info("Sending message to topic: ${Topics.utlopReaktiveringSvar}")
        }
        .to(
            Topics.utlopReaktiveringSvar,
            Produced.with(
                Serdes.String(),
                Serdes.serdeFrom(
                    automatiskReaktiveringSvarSerde.serializer(),
                    automatiskReaktiveringSvarSerde.deserializer()
                )
            )
        )
}
