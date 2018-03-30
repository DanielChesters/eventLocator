package fr.danielchesters.eventLocator.models

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

/**
 * @author Daniel Chesters (on 25/03/2018).
 */

@Entity
data class Event(@field:Id
                 @field:GeneratedValue
                 var id: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
                 @field:Max(180)
                 @field:Min(-180)
                 @field:Column(nullable = false)
                 var longitude: Double = 0.0,
                 @field:Max(90)
                 @field:Min(-90)
                 @field:Column(nullable = false)
                 var latitude: Double = 0.0,
                 @field:Column(nullable = false)
                 @field:NotEmpty
                 var name: String = "",
                 @field:Column(nullable = false)
                 var date: LocalDateTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.ofHours(0)),
                 @field:Column(length = 3000)
                 @field:Size(max = 3000)
                 var description: String? = null)
