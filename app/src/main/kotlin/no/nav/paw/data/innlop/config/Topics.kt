package no.nav.paw.data.innlop.config

object Topics {
    val innlopReaktivering = requireNotNull(System.getenv("AUTOMATISK_REAKTIVERING_TOPIC")) { "Expected AUTOMATISK_REAKTIVERING_TOPIC" }
    val utlopReaktivering = requireNotNull(System.getenv("DATA_REAKTIVERING_TOPIC")) { "Expected DATA_REAKTIVERING_TOPIC" }
    val utlopReaktiveringSvar = requireNotNull(System.getenv("DATA_REAKTIVERING_SVAR_TOPIC")) { "Expected DATA_REAKTIVERING_SVAR_TOPIC" }
}
