package com.example.gymtracker.repository

import com.example.gymtracker.model.Exercise
import com.example.gymtracker.model.Section
import com.example.gymtracker.model.SectionWithExercises
import com.example.gymtracker.room.AppDatabase
import javax.inject.Inject

class GymTrackerRepositoryImpl @Inject constructor(
    private val database: AppDatabase
) : GymTrackerRepository {
    override suspend fun getSections(): List<SectionWithExercises> =
        database.dao().getSections()

    override suspend fun setSections(sections: List<Section>) {
        database.dao().setSections(sections = sections)
    }

    override suspend fun setExercises(exercises: List<Exercise>) {
        database.dao().setExercises(exercises = exercises)
    }

    override suspend fun insertExercise(exercise: Exercise) {
        database.dao().updateExercise(exercise = exercise)
    }

    override suspend fun getExercises(): List<Exercise> =
        database.dao().getExercises()

    override suspend fun insertSection(section: Section) {
        database.dao().insertSection(section = section)
    }
}