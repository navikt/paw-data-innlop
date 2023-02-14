package no.nav.paw.data.innlop.tjenester

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.kafka.util.KafkaPropertiesPreset
import no.nav.paw.data.innlop.AutomatiskReaktivering
import no.nav.paw.data.innlop.avro.asTimestamp
import no.nav.paw.data.innlop.eventer.AutomatiskReaktiveringEvent
import no.nav.paw.data.innlop.kafka.TopicConsumer
import no.nav.paw.data.innlop.kafka.TopicProducer

internal class AutomatiskReaktiveringTjeneste(private val topicProducer: TopicProducer<AutomatiskReaktivering>) {
    val json = jacksonObjectMapper().findAndRegisterModules()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun consume() {
        val kafkaProperties = KafkaPropertiesPreset.aivenDefaultConsumerProperties("consumerGroupId")
        TopicConsumer(kafkaProperties, "topic").consume {
            val event = json.readValue<AutomatiskReaktiveringEvent>(it.value())

            try {
                if (event.type == "AutomatiskReaktivering") {
                    AutomatiskReaktivering.newBuilder().apply {
                        brukerId = event.brukerId
                        created = event.created.asTimestamp()
                    }.build().also { data ->
                        topicProducer.publiser(data)
                    }
                } else if (event.type == "AutomatiskReaktiveringSvar") {
                    // TODO
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }
}
