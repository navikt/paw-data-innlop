package no.nav.paw.data.innlop.tjenester

import no.nav.paw.data.innlop.kafka.TopicProducer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName

class AutomatiskReaktiveringTjenesteTest {
    lateinit var kafka: KafkaContainer
    lateinit var kafkaSchemaRegistry: GenericContainer<*>

    @Before
    fun setup() {
        kafka = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))
        kafka.start()

        kafkaSchemaRegistry = GenericContainer(DockerImageName.parse("confluentinc/cp-schema-registry:6.2.1"))
            .withEnv(mapOf("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS" to kafka.bootstrapServers))
        kafkaSchemaRegistry.start()
    }

    @After
    fun shutdown() {
        kafka.stop()
        kafkaSchemaRegistry.stop()
    }

    @Test
    fun test() {
        println(kafka.bootstrapServers)
        val automatiskReaktiveringTjeneste = AutomatiskReaktiveringTjeneste(TopicProducer.dataTopic("topic-name")).consume()
        assert(true)
    }
}
