package no.nav.paw.data.innlop.kafka

import no.nav.common.kafka.util.KafkaPropertiesBuilder
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName

class TopicConsumerTest {
    lateinit var kafka: KafkaContainer

    @Before
    fun setup() {
        kafka = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))
        kafka.start()
    }

    @After
    fun shutdown() {
        kafka.stop()
    }

    @Test
    fun test() {
        val kafkaProperties = KafkaPropertiesBuilder
            .consumerBuilder()
            .withBaseProperties()
            .withConsumerGroupId("test")
            .withBrokerUrl(kafka.bootstrapServers)
            .withDeserializers(ByteArrayDeserializer::class.java, ByteArrayDeserializer::class.java)
            .build()

        val topicConsumer = TopicConsumer(kafkaProperties, "test")
        topicConsumer.consume {
            assert(false)
        }
    }
}
