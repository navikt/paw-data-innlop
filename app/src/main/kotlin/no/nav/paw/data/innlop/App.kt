package no.nav.paw.data.innlop

import no.nav.paw.data.innlop.kafka.TopicProducer.Companion.dataTopic
import no.nav.paw.data.innlop.tjenester.AutomatiskReaktiveringTjeneste

fun main() {
    val dataReaktiveringTopic = System.getenv("DATA_REAKTIVERING_TOPIC")
    val dataReaktiveringSvarTopic = System.getenv("DATA_REAKTIVERING_SVAR_TOPIC")

    AutomatiskReaktiveringTjeneste(dataTopic<AutomatiskReaktivering>(dataReaktiveringTopic), dataTopic<AutomatiskReaktiveringSvar>(dataReaktiveringSvarTopic)).start()
}
