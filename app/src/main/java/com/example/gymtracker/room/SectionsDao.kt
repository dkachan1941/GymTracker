package com.example.gymtracker.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.gymtracker.model.Exercise
import com.example.gymtracker.model.Section
import com.example.gymtracker.model.SectionWithExercises

@Dao
interface SectionsDao {
    @Query("SELECT * FROM Section")
    suspend fun getSections(): List<SectionWithExercises>

    @Insert(onConflict = REPLACE)
    suspend fun setSections(sections: List<Section>)

    @Insert(onConflict = REPLACE)
    suspend fun setExercises(exercises: List<Exercise>)

    @Insert(onConflict = REPLACE)
    suspend fun updateExercise(exercise: Exercise)

    @Query("SELECT * FROM Exercise")
    suspend fun getExercises(): List<Exercise>

    @Insert(onConflict = REPLACE)
    suspend fun insertSection(section: Section)
}