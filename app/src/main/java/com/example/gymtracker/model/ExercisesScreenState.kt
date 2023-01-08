package com.example.gymtracker.model

import kotlinx.coroutines.flow.MutableStateFlow

sealed class ExercisesScreenState {

    object Initialized : ExercisesScreenState()

    data class ShowingExercises(
        val exercises: List<Exercise> = emptyList()
    ) : ExercisesScreenState()

    data class ShowingExerciseDetails(
        val exercise: Exercise
    ) : ExercisesScreenState()
}

sealed class ExercisesScreenEvent {

    data class OnExerciseCompleted(
        val exercise: Exercise
    ) : ExercisesScreenEvent()

    data class ChangeExerciseName(
        val exercise: Exercise,
        val exerciseName: String
    ) : ExercisesScreenEvent()

    data class OnExerciseClicked(
        val exercise: Exercise
    ) : ExercisesScreenEvent()

    object LoadData : ExercisesScreenEvent()
}