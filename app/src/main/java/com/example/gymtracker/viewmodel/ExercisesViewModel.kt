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
        }
    }

    private fun updateExercise(exercise: Exercise) {
        val newExercise = exercise.copy(
            lastDoneTimestamp = System.currentTimeMillis(),
        )
        viewModelScope.launch {
            repository.insertExercise(
                newExercise
            )
        }
        if (_state.value is ExercisesScreenState.ShowingExercises) {
            _state.value = (state.value as ExercisesScreenState.ShowingExercises).copy(
                exercises = (state.value as ExercisesScreenState.ShowingExercises)
                    .exercises.map { oldExercise ->
                        if (oldExercise.id == exercise.id) {
                            newExercise
                        } else {
                            oldExercise
                        }
                    }
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