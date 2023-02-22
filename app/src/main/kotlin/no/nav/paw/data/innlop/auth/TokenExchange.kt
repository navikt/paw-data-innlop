package no.nav.paw.data.innlop.auth

import no.nav.common.token_client.builder.AzureAdTokenClientBuilder
import no.nav.paw.data.innlop.config.Config
import no.nav.paw.data.innlop.utils.logger

class TokenExchange {
    val config = Config()
    val namespace = "pdl"
    val appName = "pdl-api"

    fun createMachineToMachineToken(): String {
        logger.info("Lager nytt Azure AD M2M-token mot $appName")
        return aadMachineToMachineTokenClient.createMachineToMachineToken(
            "api://${config.pdlCluster}.$namespace.$appName/.default"
        )
    }

    private val aadMachineToMachineTokenClient = AzureAdTokenClientBuilder.builder()
        .withNaisDefaults()
        .buildMachineToMachineTokenClient()
}
