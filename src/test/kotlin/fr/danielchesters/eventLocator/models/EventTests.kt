package fr.danielchesters.eventLocator.models

import fr.danielchesters.eventLocator.repositories.EventRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

/**
 * @author Daniel Chesters (on 25/03/2018).
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest
class EventTests {
    @Autowired
    lateinit var eventRepository: EventRepository

    @Test
    fun `save an event`() {
        val event = Event(name = "test", date = LocalDateTime.now(), description = "It is a test", latitude = 0.0, longitude = 0.0)
        val savedEvent = eventRepository.save(event)

        val getEvent = eventRepository.findById(savedEvent.id)

        assertTrue(getEvent.isPresent)
        val eventFromDb = getEvent.get()
        assertEquals(event.name, eventFromDb.name)
        assertEquals(event.date, eventFromDb.date)
        assertEquals(event.description, eventFromDb.description)
        assertEquals(event.latitude, eventFromDb.latitude, 0.01)
        assertEquals(event.longitude, eventFromDb.longitude, 0.01)
    }
}
