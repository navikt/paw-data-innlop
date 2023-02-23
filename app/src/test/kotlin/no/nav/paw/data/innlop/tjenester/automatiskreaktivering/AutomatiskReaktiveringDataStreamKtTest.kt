package no.nav.paw.data.innlop.tjenester.automatiskreaktivering

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.confluent.kafka.schemaregistry.testutil.MockSchemaRegistry
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import junit.framework.TestCase.assertEquals
import no.nav.paw.data.innlop.AutomatiskReaktivering
import no.nav.paw.data.innlop.innlopStream
import no.nav.paw.data.innlop.utils.asTimestamp
import no.nav.paw.pdl.PdlClient
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.Serdes.StringSerde
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.TestOutputTopic
import org.apache.kafka.streams.TopologyTestDriver
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.util.Properties
import kotlin.collections.set

internal class AutomatiskReaktiveringDataStreamKtTest {

    private var testDriver: TopologyTestDriver? = null
    private var inputTopic: TestInputTopic<String, String>? = null
    private var outputTopic: TestOutputTopic<String, AutomatiskReaktivering>? = null

    private val SCHEMA_REGISTRY_SCOPE = "automatisk-reaktivering"
    private val MOCK_SCHEMA_REGISTRY_URL = "mock://$SCHEMA_REGISTRY_SCOPE"
    private val AUTOMATISK_REAKTIVERING_TOPIC = "paw.arbeidssoker-reaktivering-v1"
    private val DATA_REAKTIVERING_TOPIC = "paw.data-innlop-reaktivering-v1"
    private val DATA_REAKTIVERING_SVAR_TOPIC = "paw.data-innlop-reaktivering-svar-v1"
    private val objectMapper = jacksonObjectMapper().findAndRegisterModules()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    private val aktorId = "2649500819544"

    @Before
    fun setup() {
        val mockEngine = MockEngine {
            respond(
                content = readResource("hentIdenter.json"),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        val httpClient = HttpClient(mockEngine)
        fun getAccessToken() = "mock-access-token"
        val pdlClient = PdlClient("http://mock.no", "OPP", httpClient) { getAccessToken() }
        val builder = StreamsBuilder()

        val innlopStream =
            innlopStream<AutomatiskReaktiveringEvent>(AUTOMATISK_REAKTIVERING_TOPIC, builder, objectMapper)

        val automatiskReaktiveringStream = innlopStream.filter { _, automatiskReaktiveringEvent ->
            automatiskReaktiveringEvent.type == "AutomatiskReaktivering"
        }

        // Create Serdes used for test record keys and values
        val stringSerde = Serdes.String()
        val avroAutomatiskReaktivering = SpecificAvroSerde<AutomatiskReaktivering>()
        val config = mapOf(
            KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG to MOCK_SCHEMA_REGISTRY_URL
        )
        avroAutomatiskReaktivering.configure(config, false)

        setupAutomatiskReaktivering(automatiskReaktiveringStream, avroAutomatiskReaktivering, DATA_REAKTIVERING_TOPIC, pdlClient)

        val topology = builder.build()

        // setup test driver
        val props = Properties()
        props[StreamsConfig.APPLICATION_ID_CONFIG] = "test"
        props[StreamsConfig.BOOTSTRAP_SERVERS_CONFIG] = "dummy:1234"
        props[StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG] = StringSerde::class.java
        props[StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG] = StringSerde::class.java
        props[KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG] = MOCK_SCHEMA_REGISTRY_URL

        testDriver = TopologyTestDriver(topology, props)

        inputTopic = testDriver!!.createInputTopic(
            AUTOMATISK_REAKTIVERING_TOPIC,
            stringSerde.serializer(),
            stringSerde.serializer()
        )

        outputTopic = testDriver!!.createOutputTopic(
            DATA_REAKTIVERING_TOPIC,
            stringSerde.deserializer(),
            avroAutomatiskReaktivering.deserializer()
        )
    }

    @After
    fun teardown() {
        testDriver?.close()
        MockSchemaRegistry.dropScope(SCHEMA_REGISTRY_SCOPE)
    }

    @Test
    fun automatiskReaktivering() {
        val automatiskReaktiveringEvent = AutomatiskReaktiveringEvent("12345678910", LocalDateTime.now(), "AutomatiskReaktivering")
        val automatiskReaktivering = AutomatiskReaktivering.newBuilder().apply {
            brukerId = aktorId
            created = automatiskReaktiveringEvent.created_at.asTimestamp()
        }.build()

        inputTopic!!.pipeInput(
            "",
            objectMapper.writeValueAsString(automatiskReaktiveringEvent)
        )
        assertEquals(automatiskReaktivering, outputTopic!!.readValue())
    }
}

private fun readResource(filename: String) =
    ClassLoader.getSystemResource(filename).readText()
