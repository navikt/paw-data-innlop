package no.nav.paw.data.innlop.tjenester.automatiskreaktivering

import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.Serdes.LongSerde
import org.apache.kafka.common.serialization.Serdes.StringSerde
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.TestInputTopic
import org.apache.kafka.streams.TestOutputTopic
import org.apache.kafka.streams.TopologyTestDriver
import org.junit.After
import org.junit.Before
import java.util.*

internal class AutomatiskReaktiveringDataStreamKtTest {
    private var testDriver: TopologyTestDriver? = null
    private var inputTopic: TestInputTopic<String, Long>? = null
    private var outputTopic: TestOutputTopic<String, Long>? = null

    private val stringSerde: Serde<String> = StringSerde()
    private val longSerde: Serde<Long> = LongSerde()

    @Before
    fun setup() {
        val builder = StreamsBuilder()
        builder.stream<String, String>("input-topic")
//            .filter(...)
            .to("output-topic")
        val topology = builder.build()

        // setup test driver

        // setup test driver
        val props = Properties()
        props.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "maxAggregation")
        props.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234")
        props.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().javaClass.name)
        props.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().javaClass.name)

        testDriver = TopologyTestDriver(topology, props)
        // setup test topics
        inputTopic = testDriver!!.createInputTopic("input-topic", stringSerde.serializer(), longSerde.serializer())
        outputTopic = testDriver!!.createOutputTopic("result-topic", stringSerde.deserializer(), longSerde.deserializer())

        // pre-populate store

        // pre-populate store
//        store = testDriver!!.getKeyValueStore<Any, Any>("aggStore")
//        store!!.put("a", 21L)
    }

    @After
    fun teardown() {
        testDriver?.close()
    }

//    @Test
//    fun shouldFlushStoreForFirstInput() {
//        inputTopic!!.pipeInput("a", 1L)
//        assertEquals(outputTopic!!.readKeyValue(), KeyValue("a", 21L))
//        assertEquals(outputTopic!!.isEmpty, true)
//    }
}
