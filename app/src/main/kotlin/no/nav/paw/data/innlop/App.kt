package no.nav.paw.data.innlop

import io.confluent.kafka.serializers.KafkaAvroSerializer
import io.confluent.kafka.serializers.KafkaAvroSerializerConfig
import no.nav.common.kafka.producer.util.KafkaProducerClientBuilder
import no.nav.common.kafka.util.KafkaPropertiesPreset
import org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import java.util.Properties

fun main() {
    val avroSerializer = Properties().apply {
        put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java)
        put(VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer::class.java)
        put(
            KafkaAvroSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
            System.getenv("KAFKA_SCHEMA_REGISTRY")
        )
        put(
            KafkaAvroSerializerConfig.USER_INFO_CONFIG,
            "${System.getenv("KAFKA_SCHEMA_REGISTRY_USER")}:${System.getenv("KAFKA_SCHEMA_REGISTRY_PASSWORD")}"
        )
        put(KafkaAvroSerializerConfig.BASIC_AUTH_CREDENTIALS_SOURCE, "USER_INFO")
    }
    val melding = TestSkjema.newBuilder().apply {
        this.name = "Jonas"
    }.build()

    val producerClient = KafkaProducerClientBuilder.builder<String, TestSkjema>()
        .withProperties(KafkaPropertiesPreset.aivenDefaultProducerProperties("paw-data-innlop"))
        .withAdditionalProperties(avroSerializer)
        .build()

    // producerClient.sendSync(ProducerRecord("paw.paw-data-innlop", melding))
    Thread.sleep(Long.MAX_VALUE)
}
