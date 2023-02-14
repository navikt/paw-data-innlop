package no.nav.paw.data.innlop.tjenester

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.nav.paw.data.innlop.AutomatiskReaktivering
import no.nav.paw.data.innlop.AutomatiskReaktiveringSvar
import no.nav.paw.data.innlop.eventer.AutomatiskReaktiveringEvent
import no.nav.paw.data.innlop.kafka.TopicProducer
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneId

class AutomatiskReaktiveringTjenesteTest {

    @Test
    fun mockingTest() {
        val reaktiveringProducerMock = mockk<TopicProducer<AutomatiskReaktivering>>()
        val svarProducerMock = mockk<TopicProducer<AutomatiskReaktiveringSvar>>()

        every { reaktiveringProducerMock.publiser(any())} returns Unit

        val tjeneste = AutomatiskReaktiveringTjeneste(reaktiveringProducerMock, svarProducerMock)

        val createdDate = LocalDateTime.now()

        tjeneste.consume(AutomatiskReaktiveringEvent("test", createdDate,"AutomatiskReaktivering"))

        val avroData = AutomatiskReaktivering.newBuilder().apply {
            brukerId = "test"
            created = createdDate.atZone(ZoneId.of("Europe/Oslo")).toInstant()
        }.build()

        verify { reaktiveringProducerMock.publiser(avroData) }
    }
}
