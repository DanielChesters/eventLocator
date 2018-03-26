package fr.danielchesters.eventLocator

import fr.danielchesters.eventLocator.models.Event
import fr.danielchesters.eventLocator.repositories.EventRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class EventLocatorApplication {
    @Bean
    fun init(eventRepository: EventRepository): CommandLineRunner {
        return CommandLineRunner {
            eventRepository.save(Event(name = "Test"))
            eventRepository.save(Event())
        }
    }
}

fun main(args: Array<String>) {
    runApplication<EventLocatorApplication>(*args)
}

