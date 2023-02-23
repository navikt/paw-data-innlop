package no.nav.paw.data.innlop.pdl

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson
import no.nav.paw.data.innlop.config.Config
import no.nav.paw.pdl.PdlClient
import no.nav.paw.pdl.hentAktorId

suspend fun hentAktorId(fnr: String, token: String): String? {
    val config = Config()
    val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            jackson()
        }
    }
    val pdlClient = PdlClient(config.pdlUrl, "OPP", httpClient) { token }
    return pdlClient.hentAktorId(fnr)
}
