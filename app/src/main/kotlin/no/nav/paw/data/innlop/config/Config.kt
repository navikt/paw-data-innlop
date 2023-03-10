package no.nav.paw.data.innlop.config

import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import no.nav.common.kafka.util.KafkaPropertiesPreset
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.errors.LogAndFailExceptionHandler
import java.util.Properties

data class Config(
    val kafka: Properties = KafkaPropertiesPreset
        .aivenDefaultProducerProperties(System.getenv("KAFKA_CONSUMER_GROUP_ID")).apply {
            put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, System.getenv("KAFKA_BROKERS"))
            put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().javaClass)
            put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().javaClass)
            put(StreamsConfig.APPLICATION_ID_CONFIG, System.getenv("KAFKA_CONSUMER_GROUP_ID"))
            put(StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG, LogAndFailExceptionHandler::class.java)
        },
    val schemaRegistry: Map<String, String> = mapOf(
        KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG to System.getenv("KAFKA_SCHEMA_REGISTRY"),
        KafkaAvroSerializerConfig.USER_INFO_CONFIG to "${System.getenv("KAFKA_SCHEMA_REGISTRY_USER")}:${System.getenv("KAFKA_SCHEMA_REGISTRY_PASSWORD")}",
        KafkaAvroSerializerConfig.BASIC_AUTH_CREDENTIALS_SOURCE to "USER_INFO"
    ),
    val pdlCluster: String = System.getenv("PDL_CLUSTER"),
    val pdlUrl: String = System.getenv("PDL_URL")
)
