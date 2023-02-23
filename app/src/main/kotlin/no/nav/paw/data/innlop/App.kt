package no.nav.paw.data.innlop

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson
import no.nav.paw.data.innlop.auth.TokenExchange
import no.nav.paw.data.innlop.config.Config
import no.nav.paw.data.innlop.config.Topics
import no.nav.paw.data.innlop.pdl.createPdlClient
import no.nav.paw.data.innlop.tjenester.automatiskreaktivering.AutomatiskReaktiveringEvent
import no.nav.paw.data.innlop.tjenester.automatiskreaktivering.automatiskReaktiveringDataStream
import no.nav.paw.data.innlop.utils.logger
import org.apache.kafka.streams.KafkaStreams
import org.apache.kafka.streams.StreamsBuilder

fun main() {
    val config = Config()
    val builder = StreamsBuilder()
    val objectMapper = jacksonObjectMapper().findAndRegisterModules()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            jackson()
        }
    }
    val tokenExchange = TokenExchange()
    val pdlClient = createPdlClient(config.pdlUrl, httpClient) { tokenExchange.createMachineToMachineToken() }

    val automatiskReaktiveringInnlop =
        innlopStream<AutomatiskReaktiveringEvent>(Topics.innlopReaktivering, builder, objectMapper)

    automatiskReaktiveringDataStream(automatiskReaktiveringInnlop, config, pdlClient)

    val streams = KafkaStreams(builder.build(), config.kafka)
    logger.info("Starter streams")
    streams.start()

    Runtime.getRuntime().addShutdownHook(Thread(streams::close))
}
