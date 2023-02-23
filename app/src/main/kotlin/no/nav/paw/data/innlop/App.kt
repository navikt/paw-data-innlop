package no.nav.paw.data.innlop

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson
import no.nav.paw.data.innlop.auth.TokenService
import no.nav.paw.data.innlop.config.Config
import no.nav.paw.data.innlop.config.Topics
import no.nav.paw.data.innlop.tjenester.automatiskreaktivering.AutomatiskReaktiveringEvent
import no.nav.paw.data.innlop.tjenester.automatiskreaktivering.automatiskReaktiveringDataStream
import no.nav.paw.data.innlop.utils.logger
import no.nav.paw.pdl.PdlClient
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
    val tokenService = TokenService()
    val pdlClient = PdlClient(config.pdlUrl, "OPP", httpClient) { tokenService.createMachineToMachineToken() }

    val automatiskReaktiveringInnlop =
        innlopStream<AutomatiskReaktiveringEvent>(Topics.innlopReaktivering, builder, objectMapper)

    automatiskReaktiveringDataStream(automatiskReaktiveringInnlop, config, pdlClient)

    val streams = KafkaStreams(builder.build(), config.kafka)
    logger.info("Starter streams")
    streams.start()

    Runtime.getRuntime().addShutdownHook(Thread(streams::close))
}
