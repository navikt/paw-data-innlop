package no.nav.paw.data.innlop

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.paw.data.innlop.config.Config
import no.nav.paw.data.innlop.config.Topics
import no.nav.paw.data.innlop.eventer.AutomatiskReaktiveringEvent
import no.nav.paw.data.innlop.kafka.innlopStream
import no.nav.paw.data.innlop.streams.automatiskReaktiveringDataStream
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder

fun main() {
    val config = Config()
    val builder = StreamsBuilder()
    val objectMapper = jacksonObjectMapper().findAndRegisterModules()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    val automatiskReaktiveringInnlop =
        innlopStream<AutomatiskReaktiveringEvent>(Topics.innlopReaktivering, builder, objectMapper)
    automatiskReaktiveringDataStream(automatiskReaktiveringInnlop, config)

    val streams = KafkaStreams(builder.build(), config.kafka)
    streams.start()

    Runtime.getRuntime().addShutdownHook(Thread(streams::close))
}
