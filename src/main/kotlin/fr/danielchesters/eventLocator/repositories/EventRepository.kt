package fr.danielchesters.eventLocator.repositories

import fr.danielchesters.eventLocator.models.Event
import org.springframework.data.repository.CrudRepository
import java.util.*

/**
 * @author Daniel Chesters (on 25/03/2018).
 */
interface EventRepository : CrudRepository<Event, UUID>
