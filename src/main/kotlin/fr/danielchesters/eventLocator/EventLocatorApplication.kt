package fr.danielchesters.eventLocator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EventLocatorApplication

fun main(args: Array<String>) {
    runApplication<EventLocatorApplication>(*args)
}
