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
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime
import java.util.*

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

    val unknownUUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

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

        eventsInDB = events.map { eventRepository.save(it) }.toList()


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
        val eventFromDB = restTemplate.getForObject("/events/${existingEventInDB.id}", Event::class.java)
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

    @Test
    fun `update an existing event`() {
        val existingEventInDB = eventsInDB[0]
        existingEventInDB.name = "New name"
        existingEventInDB.description = "New description"

        val responseEntity = restTemplate.postForEntity("/events/${existingEventInDB.id}", existingEventInDB, Event::class.java)
        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        val newEvent = responseEntity.body
        assertNotNull(newEvent)
        assertEquals(existingEventInDB.name, newEvent?.name)
        assertEquals(existingEventInDB.date, newEvent?.date)
        assertEquals(existingEventInDB.description, newEvent?.description)
        assertEquals(existingEventInDB.latitude, newEvent!!.latitude, 0.01)
        assertEquals(existingEventInDB.longitude, newEvent.longitude, 0.01)
    }

    @Test
    fun `try to get an unknown event`() {
        val responseEntity = restTemplate.getForEntity("/events/$unknownUUID", Event::class.java)
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)
    }

    @Test
    fun `try to update an unknown event`() {
        val event = Event(name = "Test")
        val responseEntity = restTemplate.postForEntity("/events/$unknownUUID", event, Event::class.java)
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)
    }

    @Test
    fun `delete an existing event`() {
        val idToDelete = eventsInDB[0].id
        restTemplate.delete("/events/$idToDelete")
        val responseEntity = restTemplate.getForEntity("/events/$idToDelete", Event::class.java)
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)
    }

    @Test
    fun `try to create an invalid event (latitude too low)`() {
        val event = Event(name = "Test", date = LocalDateTime.now(), latitude = -100.0, longitude = 15.0)
        val responseEntity = restTemplate.postForEntity("/events", event, Event::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        println(responseEntity.body)
    }

    @Test
    fun `try to create an invalid event (latitude too high)`() {
        val event = Event(name = "Test", date = LocalDateTime.now(), latitude = 100.0, longitude = 15.0)
        val responseEntity = restTemplate.postForEntity("/events", event, Event::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        println(responseEntity.body)
    }

    @Test
    fun `try to create an invalid event (longitude too west)`() {
        val event = Event(name = "Test", date = LocalDateTime.now(), latitude = 45.0, longitude = -200.0)
        val responseEntity = restTemplate.postForEntity("/events", event, Event::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        println(responseEntity.body)
    }

    @Test
    fun `try to create an invalid event (longitude too east)`() {
        val event = Event(name = "Test", date = LocalDateTime.now(), latitude = 45.0, longitude = 200.0)
        val responseEntity = restTemplate.postForEntity("/events", event, Event::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        println(responseEntity.body)
    }

    @Test
    fun `try to create an invalid event (name empty)`() {
        val event = Event(date = LocalDateTime.now(), latitude = 45.0, longitude = 15.0)
        val responseEntity = restTemplate.postForEntity("/events", event, Event::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        println(responseEntity.body)
    }

    @Test
    fun `try to create an invalid event (description too big)`() {
        val event = Event(name = "Test", date = LocalDateTime.now(),
                latitude = 45.0, longitude = 15.0,
                description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nec odio. Praesent libero. Sed cursus ante dapibus diam. Sed nisi. Nulla quis sem at nibh elementum imperdiet. Duis sagittis ipsum. Praesent mauris. Fusce nec tellus sed augue semper porta. Mauris massa. Vestibulum lacinia arcu eget nulla. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Curabitur sodales ligula in libero. Sed dignissim lacinia nunc. Curabitur tortor. Pellentesque nibh. Aenean quam. In scelerisque sem at dolor. Maecenas mattis. Sed convallis tristique sem. Proin ut ligula vel nunc egestas porttitor. Morbi lectus risus, iaculis vel, suscipit quis, luctus non, massa. Fusce ac turpis quis ligula lacinia aliquet. Mauris ipsum. Nulla metus metus, ullamcorper vel, tincidunt sed, euismod in, nibh. Quisque volutpat condimentum velit. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nam nec ante. Sed lacinia, urna non tincidunt mattis, tortor neque adipiscing diam, a cursus ipsum ante quis turpis. Nulla facilisi. Ut fringilla. Suspendisse potenti. Nunc feugiat mi a tellus consequat imperdiet. Vestibulum sapien. Proin quam. Etiam ultrices. Suspendisse in justo eu magna luctus suscipit. Sed lectus. Integer euismod lacus luctus magna. Quisque cursus, metus vitae pharetra auctor, sem massa mattis sem, at interdum magna augue eget diam. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Morbi lacinia molestie dui. Praesent blandit dolor. Sed non quam. In vel mi sit amet augue congue elementum. Morbi in ipsum sit amet pede facilisis laoreet. Donec lacus nunc, viverra nec, blandit vel, egestas et, augue. Vestibulum tincidunt malesuada tellus. Ut ultrices ultrices enim. Curabitur sit amet mauris. Morbi in dui quis est pulvinar ullamcorper. Nulla facilisi. Integer lacinia sollicitudin massa. Cras metus. Sed aliquet risus a tortor. Integer id quam. Morbi mi. Quisque nisl felis, venenatis tristique, dignissim in, ultrices sit amet, augue. Proin sodales libero eget ante. Nulla quam. Aenean laoreet. Vestibulum nisi lectus, commodo ac, facilisis ac, ultricies eu, pede. Ut orci risus, accumsan porttitor, cursus quis, aliquet eget, justo. Sed pretium blandit orci. Ut eu diam at pede suscipit sodales. Aenean lectus elit, fermentum non, convallis id, sagittis at, neque. Nullam mauris orci, aliquet et, iaculis et, viverra vitae, ligula. Nulla ut felis in purus aliquam imperdiet. Maecenas aliquet mollis lectus. Vivamus consectetuer risus et tortor. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer nec odio. Praesent libero. Sed cursus ante dapibus diam. Sed nisi. Nulla quis sem at nibh elementum imperdiet. Duis sagittis ipsum. Praesent mauris. Fusce nec tellus sed augue semper porta. Mauris massa. Vestibulum lacinia arcu eget nulla. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Curabitur sodales ligula in libero. Sed dignissim lacinia nunc. Curabitur tortor. Pellentesque nibh. Aenean quam. In scelerisque sem at dolor. Maecenas mattis. Sed convallis tristique sem. Proin ut ligula vel nunc egestas porttitor. Morbi lectus risus, iaculis vel, suscipit quis, luctus non, massa. Fusce ac turpis quis ligula lacinia aliquet. Mauris ipsum. Nulla metus metus, ullamcorper vel, tincidunt sed, euismod in, nibh.")
        val responseEntity = restTemplate.postForEntity("/events", event, Event::class.java)
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.statusCode)
        println(responseEntity.body)
    }

}
