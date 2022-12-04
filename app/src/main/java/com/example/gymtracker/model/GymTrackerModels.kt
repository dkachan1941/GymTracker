package com.example.gymtracker.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import kotlin.math.absoluteValue

@Entity
data class Section(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String = "",
    var lastDoneTimestamp: Long? = null
)

@Entity
data class Exercise(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String = "",
    var lastDoneTimestamp: Long? = null,
    var sectionId: Long
)

data class SectionWithExercises(
    @Embedded var section: Section,
    @Relation(
        parentColumn = "id",
        entityColumn = "sectionId"
    ) var exercises: List<Exercise> = listOf()
)

fun SectionWithExercises.getDaysOfInactivity(): Long? =
    getDaysOfInactivity(section.lastDoneTimestamp)

fun Exercise.getDaysOfInactivity(): Long? = getDaysOfInactivity(lastDoneTimestamp)

private fun getDaysOfInactivity(timeStamp: Long?): Long? =
    timeStamp?.let {
        Duration.between(
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(System.currentTimeMillis()),
                TimeZone.getDefault().toZoneId()
            ),
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timeStamp),
                TimeZone.getDefault().toZoneId()
            )
        ).toMinutes().absoluteValue
    }