package com.example.gymtracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gymtracker.model.*
import com.example.gymtracker.repository.GymTrackerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExercisesViewModel @Inject constructor(
    private val repository: GymTrackerRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            processInput(ExercisesScreenEvent.LoadData)
        }
    }

    private val _state = MutableStateFlow<ExercisesScreenState>(ExercisesScreenState.Initialized)

    val state: StateFlow<ExercisesScreenState>
        get() = _state

    fun processInput(event: ExercisesScreenEvent) {
        when (event) {
            ExercisesScreenEvent.LoadData -> loadData()
            is ExercisesScreenEvent.OnExerciseCompleted -> updateExercise(exercise = event.exercise)
            is ExercisesScreenEvent.ChangeExerciseName -> changeExerciseName(
                exercise = event.exercise,
                exerciseName = event.exerciseName
            )
            is ExercisesScreenEvent.OnExerciseClicked -> displayExerciseDetails(
                exercise = event.exercise
            )
        }
    }

    private fun changeExerciseName(exercise: Exercise, exerciseName: String) {
        viewModelScope.launch {
            repository.insertExercise(
                exercise.copy(
                    name = exerciseName
                )
            )
        }
    }

    private fun displayExerciseDetails(exercise: Exercise) {
        _state.value = ExercisesScreenState.ShowingExerciseDetails(
            exercise = exercise
        )
    }

    private fun updateExercise(exercise: Exercise) {
        viewModelScope.launch {
            repository.insertExercise(
                exercise
            )
        }
        if (_state.value is ExercisesScreenState.ShowingExerciseDetails) {
            _state.value = (state.value as ExercisesScreenState.ShowingExerciseDetails).copy(
                exercise = exercise
            )
        }
    }

    fun loadData() {
        viewModelScope.launch {
            val data = repository.getExercises()
            _state.value = ExercisesScreenState.ShowingExercises(
                exercises = data
            )
        }
    }
}