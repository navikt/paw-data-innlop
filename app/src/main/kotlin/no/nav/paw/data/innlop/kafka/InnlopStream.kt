package no.nav.paw.data.innlop.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import no.nav.paw.data.innlop.utils.logger
import org.apache.kafka.streams.StreamsBuilder

inline fun <reified T> innlopStream(
    innlopTopic: String,
    builder: StreamsBuilder,
    objectMapper: ObjectMapper
) = builder.stream<String, String>(innlopTopic)
    .mapValues { melding -> objectMapper.readValue(melding, T::class.java) }
    .peek { _, _ -> logger.info("Konsumerte melding fra $innlopTopic") }
