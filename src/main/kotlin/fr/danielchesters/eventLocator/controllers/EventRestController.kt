package fr.danielchesters.eventLocator.controllers

import fr.danielchesters.eventLocator.models.Event
import fr.danielchesters.eventLocator.repositories.EventRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import reactor.core.publisher.toMono
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
    fun getAll() = eventRepository.findAll().toFlux()

    @RequestMapping(method = [RequestMethod.POST])
    fun createEvent(@Valid @RequestBody event: Event) =
            ResponseEntity(eventRepository.save(event), HttpStatus.CREATED).toMono()

    @RequestMapping(method = [RequestMethod.GET], value = ["/{uuid}"])
    fun getEvent(@PathVariable uuid: String) =
            eventRepository.findById(UUID.fromString(uuid)).map {
                ResponseEntity.ok(it).toMono()
            }.orElse(ResponseEntity.notFound().build<Event>().toMono())

    @RequestMapping(method = [RequestMethod.POST], value = ["/{uuid}"])
    fun updateEvent(@PathVariable uuid: String, @Valid @RequestBody event: Event) =
            eventRepository.findById(UUID.fromString(uuid)).map {
                event.id = it.id
                ResponseEntity(eventRepository.save(event), HttpStatus.CREATED).toMono()
            }.orElse(ResponseEntity<Event>(HttpStatus.NOT_FOUND).toMono())

    @RequestMapping(method = [RequestMethod.DELETE], value = ["/{uuid}"])
    fun deleteEvent(@PathVariable uuid: String) =
            eventRepository.findById(UUID.fromString(uuid)).map {
                eventRepository.delete(it).toMono()
            }.orElse(Mono.empty())


}
