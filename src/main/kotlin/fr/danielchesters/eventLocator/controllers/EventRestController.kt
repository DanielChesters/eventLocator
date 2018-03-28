package fr.danielchesters.eventLocator.controllers

import fr.danielchesters.eventLocator.models.Event
import fr.danielchesters.eventLocator.repositories.EventRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

/**
 * @author Daniel Chesters (on 26/03/2018).
 */
@RestController
@RequestMapping("/events")
class EventRestController {

    @Autowired
    lateinit var eventRepository: EventRepository

    @RequestMapping(method = [RequestMethod.GET])
    fun getAll(): List<Event> {
        return eventRepository.findAll().toList()
    }

    @RequestMapping(method = [RequestMethod.POST])
    fun createEvent(@Valid @RequestBody event: Event): ResponseEntity<Any> {
        val savedEvent = eventRepository.save(event)
        return ResponseEntity.ok(savedEvent)
    }

    @RequestMapping(method = [RequestMethod.GET], value = ["/{uuid}"])
    fun getEvent(@PathVariable uuid: String): ResponseEntity<Event>? {
        return eventRepository.findById(UUID.fromString(uuid)).map({ ResponseEntity.ok(it) })
                .orElse(ResponseEntity.notFound().build())
    }

    @RequestMapping(method = [RequestMethod.POST], value = ["/{uuid}"])
    fun updateEvent(@PathVariable uuid: String, @Valid @RequestBody event: Event): ResponseEntity<Event>? {
        return eventRepository.findById(UUID.fromString(uuid)).map({
            it.date = event.date
            it.description = event.description
            it.latitude = event.latitude
            it.longitude = event.longitude
            it.name = event.name
            ResponseEntity.ok(eventRepository.save(it))
        }).orElse(ResponseEntity.notFound().build())
    }

}
