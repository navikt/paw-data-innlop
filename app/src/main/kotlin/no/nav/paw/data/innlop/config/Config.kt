package no.nav.paw.data.innlop.config

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import no.nav.common.kafka.util.KafkaPropertiesPreset
import org.apache.kafka.streams.StreamsConfig
import java.util.*

data class Config(
    val kafka: Properties = KafkaPropertiesPreset
        .aivenDefaultProducerProperties(System.getenv("KAFKA_CONSUMER_GROUP_ID")).apply {
            put(
                KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
                System.getenv("KAFKA_SCHEMA_REGISTRY")
            )
            put(
                KafkaAvroSerializerConfig.USER_INFO_CONFIG,
                "${System.getenv("KAFKA_SCHEMA_REGISTRY_USER")}:${System.getenv("KAFKA_SCHEMA_REGISTRY_PASSWORD")}"
            )
            put(KafkaAvroSerializerConfig.BASIC_AUTH_CREDENTIALS_SOURCE, "USER_INFO")
            put(StreamsConfig.APPLICATION_ID_CONFIG, System.getenv("KAFKA_CONSUMER_GROUP_ID"))
        },
    val schemaRegistry: Map<String, String> = mapOf(
        AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG to System.getenv("KAFKA_SCHEMA_REGISTRY"),
        KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG to System.getenv("KAFKA_SCHEMA_REGISTRY"),
        KafkaAvroSerializerConfig.USER_INFO_CONFIG to "${System.getenv("KAFKA_SCHEMA_REGISTRY_USER")}:${System.getenv("KAFKA_SCHEMA_REGISTRY_PASSWORD")}",
        KafkaAvroSerializerConfig.BASIC_AUTH_CREDENTIALS_SOURCE to "USER_INFO"
    )
)
