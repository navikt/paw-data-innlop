package no.nav.paw.data.innlop.eventer

import java.time.LocalDateTime

data class AutomatiskReaktiveringEvent(
    val bruker_id: String,
    val created_at: LocalDateTime,
    val type: String,
    val svar: String? = null
)
