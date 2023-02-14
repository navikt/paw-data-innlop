package no.nav.paw.data.innlop

import no.nav.paw.data.innlop.kafka.TopicProducer
import no.nav.paw.data.innlop.tjenester.AutomatiskReaktiveringTjeneste

fun main() {
    val topic = System.getenv("KAFKA_PRODUKT_TOPIC")

    AutomatiskReaktiveringTjeneste(TopicProducer.dataTopic(topic), TopicProducer.dataTopic(topic)).start()

    Thread.sleep(Long.MAX_VALUE)
}
