package no.nav.paw.data.innlop.kafka

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration
import java.util.Properties
import java.util.function.Consumer

class TopicConsumer(val kafkaProperties: Properties, val topic: String) {
    fun consume(callback: (record: ConsumerRecord<String, String>) -> Unit) {
        KafkaConsumer<String, String>(kafkaProperties).use { consumer ->
            {
                consumer.subscribe(listOf(topic))
                val consumerRecords = consumer.poll(Duration.ofMinutes(2))

                consumerRecords.forEach(
                    Consumer { record: ConsumerRecord<String, String> ->
                        try {
                            callback(record)
                        } catch (e: Exception) {
                        }
                    }
                )
            }
        }
    }
}
