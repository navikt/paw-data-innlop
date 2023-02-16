package no.nav.paw.data.innlop.streams

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import no.nav.paw.data.innlop.AutomatiskReaktivering
import no.nav.paw.data.innlop.AutomatiskReaktiveringSvar
import no.nav.paw.data.innlop.avro.asTimestamp
import no.nav.paw.data.innlop.config.Config
import no.nav.paw.data.innlop.config.Topics
import no.nav.paw.data.innlop.eventer.AutomatiskReaktiveringEvent
import no.nav.paw.data.innlop.utils.logger
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Produced

fun automatiskReaktiveringDataStream(
    stream: KStream<String, AutomatiskReaktiveringEvent>,
    config: Config
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
            AutomatiskReaktivering.newBuilder().apply {
                brukerId = melding.bruker_id
                created = melding.created_at.asTimestamp()
            }.build()
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