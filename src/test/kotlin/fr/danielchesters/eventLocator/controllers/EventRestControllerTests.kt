package fr.danielchesters.eventLocator.controllers

import fr.danielchesters.eventLocator.models.Event
import fr.danielchesters.eventLocator.repositories.EventRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

/**
 * @author Daniel Chesters (on 28/03/2018).
 */
@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class EventRestControllerTests {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var eventRepository: EventRepository

    lateinit var eventsInDB: List<Event>

    @BeforeEach
    fun setup() {
        eventRepository.deleteAll()
        val events = listOf(Event(name = "existing Event",
                date = LocalDateTime.now(),
                description = "An existing event",
                latitude = 0.0, longitude = 0.0), Event(name = "second event",
                date = LocalDateTime.now(),
                description = "A second event",
                latitude = 0.0, longitude = 0.0))

        eventsInDB = events.map{eventRepository.save(it)}.toList()


    }

    @Test
    fun `create an event`() {
        val event = Event(name = "test",
                date = LocalDateTime.now(),
                description = "It is a test",
                latitude = 0.0, longitude = 0.0)
        val savedEvent = restTemplate.postForObject("/events", event, Event::class.java)

        assertNotNull(savedEvent)
        assertEquals(event.name, savedEvent.name)
        assertEquals(event.date, savedEvent.date)
        assertEquals(event.description, savedEvent.description)
        assertEquals(event.latitude, savedEvent.latitude, 0.01)
        assertEquals(event.longitude, savedEvent.longitude, 0.01)
    }

    @Test
    fun `get an existing event`() {
        val existingEventInDB = eventsInDB[0]
        val eventFromDB = restTemplate.getForObject("/events/" + existingEventInDB.id.toString(), Event::class.java)
        assertNotNull(eventFromDB)
        assertEquals(existingEventInDB.name, eventFromDB.name)
        assertEquals(existingEventInDB.date, eventFromDB.date)
        assertEquals(existingEventInDB.description, eventFromDB.description)
        assertEquals(existingEventInDB.latitude, eventFromDB.latitude, 0.01)
        assertEquals(existingEventInDB.longitude, eventFromDB.longitude, 0.01)
    }

    @Test
    fun `get all events`() {
        val allEvents = restTemplate.getForObject("/events", List::class.java)
        assertNotNull(allEvents)
        assertEquals(2, allEvents.count())

        allEvents.forEach {
            val event = it as Map<*, *>
            val existingEventInDB = eventsInDB.find { it.id.toString() == event["id"].toString() }
            assertNotNull(existingEventInDB)
            assertEquals(existingEventInDB?.name, event["name"])
            assertEquals(existingEventInDB?.date, LocalDateTime.parse(event["date"].toString()))
            assertEquals(existingEventInDB?.description, event["description"])
            assertEquals(existingEventInDB!!.latitude, event["latitude"].toString().toDouble(), 0.01)
            assertEquals(existingEventInDB.longitude, event["longitude"].toString().toDouble(), 0.01)
        }
    }
}
