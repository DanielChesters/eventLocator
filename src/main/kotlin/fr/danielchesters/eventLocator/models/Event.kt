package fr.danielchesters.eventLocator.models

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Size

/**
 * @author Daniel Chesters (on 25/03/2018).
 */

@Entity
data class Event(@Id
                 @GeneratedValue
                 var id: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000"),
                 @Max(180) @Min(-180) @Column(nullable = false) var longitude: Double = 0.0,
                 @Max(90) @Min(-90) @Column(nullable = false) var latitude: Double = 0.0,
                 @Column(nullable = false) @Size(min = 1) var name: String = "",
                 @Column(nullable = false) var date: LocalDateTime = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.ofHours(0)),
                 @Column(length = 3000) var description: String?)
