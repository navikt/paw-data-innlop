package no.nav.paw.data.innlop.pdl

import io.ktor.client.HttpClient
import no.nav.paw.pdl.PdlClient

fun createPdlClient(pdlUrl: String, httpClient: HttpClient, getAccessToken: () -> String): PdlClient =
    PdlClient(pdlUrl, "OPP", httpClient) { getAccessToken() }
