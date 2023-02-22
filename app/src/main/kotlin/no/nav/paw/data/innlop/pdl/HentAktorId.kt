package no.nav.paw.data.innlop.pdl

import no.nav.paw.data.innlop.config.Config
import no.nav.paw.pdl.PdlClient
import no.nav.paw.pdl.hentAktorId

suspend fun hentAktorId(fnr: String, token: String): String? {
    val config = Config()
    val pdlClient = PdlClient(config.pdlUrl, "OPP", { token })
    return pdlClient.hentAktorId(fnr)
}
