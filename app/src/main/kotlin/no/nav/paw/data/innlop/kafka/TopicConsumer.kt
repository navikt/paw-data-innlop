package no.nav.paw.data.innlop.kafka

import no.nav.common.kafka.consumer.util.KafkaConsumerClientBuilder
import no.nav.common.kafka.consumer.util.deserializer.Deserializers.stringDeserializer
import org.apache.kafka.clients.consumer.ConsumerRecord
import java.util.*
import java.util.function.Consumer

class TopicConsumer(private val kafkaProperties: Properties, private val topic: String) {
    fun consume(callback: Consumer<ConsumerRecord<String?, String?>>) {
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
