package no.nav.paw.data.innlop.kafka

import org.apache.avro.specific.SpecificRecord
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.errors.AuthorizationException
import org.apache.kafka.common.errors.InvalidConfigurationException
import org.apache.kafka.common.errors.InvalidTopicException
import org.apache.kafka.common.errors.RecordBatchTooLargeException
import org.apache.kafka.common.errors.RecordTooLargeException
import org.apache.kafka.common.errors.UnknownServerException
import java.util.Properties

internal class TopicProducer<T : SpecificRecord>(
    private val producer: KafkaProducer<String, T>,
    private val topic: String
) {
    companion object {
//        private val logger = KotlinLogging.logger {}

        fun <T : SpecificRecord> dataTopic(topic: String) =
            TopicProducer(createProducer<String, T>(AivenConfig.default.avroProducerConfig()), topic)

        private fun isFatalError(err: Exception) = when (err) {
            is InvalidConfigurationException,
            is InvalidTopicException,
            is RecordBatchTooLargeException,
            is RecordTooLargeException,
            is UnknownServerException,
            is AuthorizationException -> true

            else -> false
        }
    }

    fun publiser(innlop: T) {
        producer.send(ProducerRecord(topic, innlop)) { _, err ->
            if (err == null || !isFatalError(err)) return@send
//            logger.error(err) { "Shutting down producer due to fatal error: ${err.message}" }
            producer.flush()
            throw err
        }
    }
}

private fun <K, V> createProducer(producerConfig: Properties = Properties()) =
    KafkaProducer<K, V>(producerConfig).also { producer ->
        Runtime.getRuntime().addShutdownHook(
            Thread {
                producer.flush()
                producer.close()
            }
        )
    }
