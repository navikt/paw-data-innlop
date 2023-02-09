package no.nav.paw.data.innlop.tjenester

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.kafka.util.KafkaPropertiesPreset
import no.nav.paw.data.innlop.AutomatiskReaktivering
import no.nav.paw.data.innlop.eventer.AutomatiskReaktiveringEvent
import no.nav.paw.data.innlop.avro.asTimestamp
import no.nav.paw.data.innlop.kafka.DataTopic
import java.util.function.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration

internal class AutomatiskReaktiveringTjeneste(private val dataTopic: DataTopic<AutomatiskReaktivering>) {
    val json = jacksonObjectMapper().findAndRegisterModules()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun consume() {
        KafkaConsumer<String, String>(KafkaPropertiesPreset.aivenDefaultConsumerProperties("paw-data-innlop-v1")).use { consumer ->
            {
                consumer.subscribe(listOf("automatisk-reaktivering-topic"))
                val consumerRecords = consumer.poll(Duration.ofMinutes(2))

                consumerRecords.forEach(Consumer { record: ConsumerRecord<String, String> ->
                    try {
                        val event = json.readValue<AutomatiskReaktiveringEvent>(record.value())
                        onPacket(event)
                    } catch (e: Exception) {

                    }
                })
            }
        }
    }

    fun onPacket(event: AutomatiskReaktiveringEvent) {
        try {
            AutomatiskReaktivering.newBuilder().apply {
                brukerId = event.brukerId
                created = event.created.asTimestamp()
            }.build().also { data ->
                dataTopic.publiser(data)
            }
        } catch (e: Exception) {
            throw e
        }
    }
}
