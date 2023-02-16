package no.nav.paw.data.innlop.utils

import org.slf4j.LoggerFactory

inline val <reified T : Any> T.logger get() = LoggerFactory.getLogger(T::class.java.name)
inline val logger get() = LoggerFactory.getLogger("main")
inline val secureLogger get() = LoggerFactory.getLogger("Tjenestekall")
