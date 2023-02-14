package no.nav.paw.data.innlop.tjenester

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.kafka.util.KafkaPropertiesPreset
import no.nav.paw.data.innlop.AutomatiskReaktivering
import no.nav.paw.data.innlop.AutomatiskReaktiveringSvar
import no.nav.paw.data.innlop.avro.asTimestamp
import no.nav.paw.data.innlop.eventer.AutomatiskReaktiveringEvent
import no.nav.paw.data.innlop.kafka.TopicConsumer
import no.nav.paw.data.innlop.kafka.TopicProducer
import no.nav.paw.data.innlop.utils.logger

internal class AutomatiskReaktiveringTjeneste(
    private val automatiskReaktiveringProducer: TopicProducer<AutomatiskReaktivering>,
    private val automatiskReaktiveringSvarProducer: TopicProducer<AutomatiskReaktiveringSvar>
) {
    val json = jacksonObjectMapper().findAndRegisterModules()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun start() {
        logger.info("Starter AutomatiskReaktiveringTjeneste")
        val kafkaProperties = KafkaPropertiesPreset.aivenDefaultConsumerProperties("consumerGroupId")

        val topic = System.getenv("AUTOMATISK_REAKTIVERING_TOPIC")

        TopicConsumer(kafkaProperties, topic).consume {
            logger.info("Konsumerer AutomatiskReaktiveringEvent")
            val event = json.readValue<AutomatiskReaktiveringEvent>(it.value())
            consume(event)
        }
    }

    internal fun consume(event: AutomatiskReaktiveringEvent) {
        if (event.type == "AutomatiskReaktivering") {
            logger.info("Fant AutomatiskReaktivering")

            AutomatiskReaktivering.newBuilder().apply {
                brukerId = event.brukerId
                created = event.created.asTimestamp()
            }.build().also { data ->
                logger.info("Publiserer AutomatiskReaktivering $data")
                automatiskReaktiveringProducer.publiser(data)
            }
        } else if (event.type == "AutomatiskReaktiveringSvar") {
            logger.info("Fant AutomatiskReaktiveringSvar")
            AutomatiskReaktiveringSvar.newBuilder().apply {
                brukerId = event.brukerId
                svar = event.svar
                created = event.created.asTimestamp()
            }.build().also { data ->
                logger.info("Publiserer AutomatiskReaktiveringSvar $data")
                automatiskReaktiveringSvarProducer.publiser(data)
            }
        }
    }
}
