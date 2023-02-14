package no.nav.paw.data.innlop.eventer

import java.time.LocalDateTime

data class AutomatiskReaktiveringEvent(
    val brukerId: String,
    val created: LocalDateTime,
    val type: String,
    val svar: String? = null
)
