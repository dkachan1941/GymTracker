package com.example.gymtracker.repository

import com.example.gymtracker.model.Exercise
import com.example.gymtracker.model.Section
import com.example.gymtracker.model.SectionWithExercises

interface GymTrackerRepository {
    suspend fun getSections(): List<SectionWithExercises>
    suspend fun setSections(sections: List<Section>)
    suspend fun setExercises(exercises: List<Exercise>)
    suspend fun insertExercise(exercise: Exercise)
    suspend fun getExercises(): List<Exercise>
    suspend fun insertSection(section: Section)
}