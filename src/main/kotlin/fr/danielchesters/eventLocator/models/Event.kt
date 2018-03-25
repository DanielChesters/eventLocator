package fr.danielchesters.eventLocator.models

import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

/**
 * @author Daniel Chesters (on 25/03/2018).
 */
@Entity
data class Event(@Id
                 @GeneratedValue
                 var id: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
                 var longitude: Double = 0.0,
                 var latitude: Double = 0.0,
                 var name: String = "",
                 var date: LocalDateTime = LocalDateTime.now(),
                 @Column(length = 3000) var description: String = "")
