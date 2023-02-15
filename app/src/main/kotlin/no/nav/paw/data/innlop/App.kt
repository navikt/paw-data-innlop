package no.nav.paw.data.innlop

import no.nav.paw.data.innlop.kafka.TopicProducer.Companion.dataTopic
import no.nav.paw.data.innlop.tjenester.AutomatiskReaktiveringTjeneste

fun main() {
    val automatiskReaktiveringTopic = System.getenv("AUTOMATISK_REAKTIVERING_TOPIC")
    val automatiskReaktiveringSvarTopic = System.getenv("AUTOMATISK_REAKTIVERING_SVAR_TOPIC")

    AutomatiskReaktiveringTjeneste(dataTopic<AutomatiskReaktivering>(automatiskReaktiveringTopic), dataTopic<AutomatiskReaktiveringSvar>(automatiskReaktiveringSvarTopic)).start()
}
