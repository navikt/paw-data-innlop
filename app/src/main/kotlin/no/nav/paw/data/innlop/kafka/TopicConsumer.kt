package no.nav.paw.data.innlop.kafka

import no.nav.paw.data.innlop.utils.logger
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration
import java.util.Properties
import java.util.function.Consumer

class TopicConsumer(private val kafkaProperties: Properties, private val topic: String) {
    fun consume(callback: (record: ConsumerRecord<String, String>) -> Unit) {
        KafkaConsumer<String, String>(kafkaProperties)
            .also { consumer ->
                Runtime.getRuntime().addShutdownHook(
                    Thread {
                        consumer.close()
                    },
                )
            }
            .use { consumer ->
                {
                    consumer.subscribe(listOf(topic))
                    val consumerRecords = consumer.poll(Duration.ofMinutes(2))

                    consumerRecords.forEach(
                        Consumer { record: ConsumerRecord<String, String> ->
                            try {
                                callback(record)
                            } catch (e: Exception) {
                                logger.error("Feil ved konsumering av topic=$topic", e)
                            }
                        },
                    )
                }
            }
    }
}
