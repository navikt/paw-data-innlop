package no.nav.paw.data.innlop.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

private val oslo: ZoneId = ZoneId.of("Europe/Oslo")

fun LocalDateTime.asTimestamp(): Instant? =
    this.atZone(oslo).toInstant()
