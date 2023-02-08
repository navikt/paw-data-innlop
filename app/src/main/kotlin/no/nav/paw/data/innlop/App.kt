package no.nav.paw.data.innlop

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.nav.common.kafka.util.KafkaPropertiesPreset
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration
import java.util.function.Consumer

fun main() {
    val json = jacksonObjectMapper().findAndRegisterModules()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

//    val automatiskReaktiveringConsumer = KafkaConsumer<String, String>(KafkaPropertiesPreset.aivenDefaultConsumerProperties("paw-data-innlop-v1")).use { consumer -> {
//        consumer.subscribe(listOf("automatisk-reaktivering-topic"))
//
//        val consumerRecords = consumer.poll(Duration.ofMinutes(2))
//
//        consumerRecords.forEach(Consumer { record: ConsumerRecord<String, String>  ->
//            try {
//                // automatisk reaktivering event =>  send avro
//                val event = json.readValue<AutomatiskReaktiveringEvent>(record.value())
//                val avroEvent = AutomatiskReaktivering.newBuilder().apply {
//                    brukerId = event.brukerId
//                    created = event.created
//                }
//
//            } catch (e: Exception) {
//
//            }
//        })
//    }}

    Thread.sleep(Long.MAX_VALUE)
}
