package com.example.gymtracker.model

sealed class ExercisesScreenState {

    object Initialized : ExercisesScreenState()

    data class ShowingExercises(
        val exercises: List<Exercise> = emptyList()
    ) : ExercisesScreenState()
}

sealed class ExercisesScreenEvent {

    data class OnExerciseCompleted(
        val exercise: Exercise
    ) : ExercisesScreenEvent()

    object LoadData : ExercisesScreenEvent()
}