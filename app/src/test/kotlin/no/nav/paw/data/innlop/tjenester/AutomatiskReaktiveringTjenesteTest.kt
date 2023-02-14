package no.nav.paw.data.innlop.tjenester

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.paw.data.innlop.AutomatiskReaktivering
import no.nav.paw.data.innlop.eventer.AutomatiskReaktiveringEvent
import no.nav.paw.data.innlop.kafka.TopicProducer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName
import java.time.LocalDateTime
import java.time.ZoneId

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
        val automatiskReaktiveringTjeneste = AutomatiskReaktiveringTjeneste(TopicProducer.dataTopic("topic-name")).start()
        assert(true)
    }

    @Test
    fun mockingTest() {
        val producerMock = mockk<TopicProducer<AutomatiskReaktivering>>()
        every { producerMock.publiser(any())} returns Unit

        val tjeneste = AutomatiskReaktiveringTjeneste(producerMock)

        val createdDate = LocalDateTime.now()

        tjeneste.consume(AutomatiskReaktiveringEvent("test", createdDate,"AutomatiskReaktivering"))

        val avroData = AutomatiskReaktivering.newBuilder().apply {
            brukerId = "test"
            created = createdDate.atZone(ZoneId.of("Europe/Oslo")).toInstant()
        }.build()

        verify { producerMock.publiser(avroData) }
    }
}
