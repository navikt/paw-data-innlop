package no.nav.paw.data.innlop.tjenester

import no.nav.common.kafka.util.KafkaPropertiesPreset
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration
import java.util.function.Consumer

class TopicConsumer(val consumerGroupId: String, val topic: String) {
    fun consume(callback: (record: ConsumerRecord<String, String>) -> Unit ) {
        KafkaConsumer<String, String>(KafkaPropertiesPreset.aivenDefaultConsumerProperties(consumerGroupId)).use { consumer ->
            {
                consumer.subscribe(listOf(topic))
                val consumerRecords = consumer.poll(Duration.ofMinutes(2))

                consumerRecords.forEach(Consumer { record: ConsumerRecord<String, String> ->
                    try {
                        callback(record);
                    } catch (e: Exception) {

                    }
                })
            }
        }
    }
}
