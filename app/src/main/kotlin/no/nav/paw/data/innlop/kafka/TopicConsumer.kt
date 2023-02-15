package no.nav.paw.data.innlop.kafka

import no.nav.common.kafka.consumer.util.KafkaConsumerClientBuilder
import no.nav.common.kafka.consumer.util.deserializer.Deserializers.stringDeserializer
import no.nav.paw.data.innlop.utils.logger
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration
import java.util.*
import java.util.function.Consumer

class TopicConsumer(private val kafkaProperties: Properties, private val topic: String) {
    fun consume(callback: (record: ConsumerRecord<String, String>) -> Unit) {
        KafkaConsumer<String, String>(kafkaProperties)
            .also { consumer ->
                Runtime.getRuntime().addShutdownHook(
                    Thread {
                        consumer.close()
                    }
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
                        }
                    )
                }
            }
    }

    fun consume2(callback: Consumer<ConsumerRecord<String?, String?>>) {
        val topicConfigs = listOf<KafkaConsumerClientBuilder.TopicConfig<*, *>>(
            KafkaConsumerClientBuilder.TopicConfig<String?, String?>()
                .withConsumerConfig(
                    topic,
                    stringDeserializer(),
                    stringDeserializer(),
                    callback
                )
        )

        val consumerClient = KafkaConsumerClientBuilder.builder()
            .withProperties(kafkaProperties)
            .withTopicConfigs(topicConfigs)
            .build()

        consumerClient.start()
    }
}
