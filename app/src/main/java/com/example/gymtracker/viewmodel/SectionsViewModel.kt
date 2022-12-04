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
class SectionsViewModel @Inject constructor(
    private val repository: GymTrackerRepository
) : ViewModel() {

    private val initialSections = listOf(
        Section(
            id = 1,
            name = "Side Pressure",
        ),
        Section(
            id = 2,
            name = "Wrist"
        )
    )

    private val initialExercises = listOf(
        Exercise(
            id = 1,
            name = "pull ups",
            sectionId = 1
        ),
        Exercise(
            id = 2,
            name = "side pressure with cable machine",
            sectionId = 1
        ),
        Exercise(
            id = 3,
            name = "wrist curls",
            sectionId = 2
        ),
        Exercise(
            id = 4,
            name = "bar curls",
            sectionId = 3
        ),
    )

    private val _state = MutableStateFlow<SectionsScreenState>(SectionsScreenState.Initialized)

    init {
        processInput(SectionsScreenEvent.LoadData)
    }

    val state: StateFlow<SectionsScreenState>
        get() = _state

    fun processInput(event: SectionsScreenEvent) {
        when (event) {
            is SectionsScreenEvent.OnSectionClick -> processSectionClick(section = event.section)
            is SectionsScreenEvent.ShowSectionsList -> showSectionsList()
            is SectionsScreenEvent.ResetExerciseDate -> resetExerciseDate(exercise = event.exercise)
            is SectionsScreenEvent.LoadData -> loadData()
            is SectionsScreenEvent.AddNewSection -> addNewSection(name = event.sectionName)
            is SectionsScreenEvent.AddNewExercise -> addNewExercise(
                name = event.exerciseName,
                sectionId = event.sectionId
            )
            is SectionsScreenEvent.ResetExercise -> updateExercise(exercise = event.exercise)
            is SectionsScreenEvent.ResetSection -> resetSection(section = event.section)
        }
    }

    private fun resetSection(section: Section) {
        val newSection = section.copy(
            lastDoneTimestamp = System.currentTimeMillis(),
        )
        viewModelScope.launch {
            repository.insertSection(
                newSection
            )
            _state.value = SectionsScreenState.ShowingCards(
                sections = repository.getSections()
            )
        }
    }

    private fun addNewExercise(name: String, sectionId: Long) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val alreadyExists = repository.getExercises()
                .firstOrNull { it.name.equals(name, ignoreCase = true) } != null
            if (!alreadyExists) {
                repository.insertExercise(
                    Exercise(
                        name = name,
                        sectionId = sectionId
                    )
                )
                _state.value = SectionsScreenState.ShowingCards(
                    sections = repository.getSections()
                )
            }
        }
    }

    private fun addNewSection(name: String) {
        if (name.isBlank()) return
        val alreadyExists = (state.value as SectionsScreenState.ShowingCards).sections.firstOrNull {
            it.section.name.equals(
                name,
                ignoreCase = true
            )
        } != null
        if (!alreadyExists) {
            viewModelScope.launch {
                repository.insertSection(
                    Section(
                        name = name,
                        lastDoneTimestamp = null
                    )
                )
                _state.value = SectionsScreenState.ShowingCards(
                    sections = repository.getSections()
                )
            }
        }
    }

    fun loadData() {
        viewModelScope.launch {
            _state.value = SectionsScreenState.ShowingCards(
                sections = repository.getSections()
            )
        }
    }

    private fun resetExerciseDate(exercise: Exercise) {
        viewModelScope.launch {
            repository.insertExercise(
                exercise.copy(
                    lastDoneTimestamp = System.currentTimeMillis(),
                )
            )
        }
        val section = (state.value as SectionsScreenState.ShowingCardDetails).section
        val newSection = section.copy(exercises = section.exercises.map { oldExercise ->
            oldExercise.copy(
                lastDoneTimestamp = if (oldExercise.id == exercise.id) {
                    System.currentTimeMillis()
                } else {
                    oldExercise.lastDoneTimestamp
                }
            )
        })
        _state.value = SectionsScreenState.ShowingCardDetails(section = newSection)
    }

    private fun showSectionsList() {
        viewModelScope.launch {
            _state.value = SectionsScreenState.ShowingCards(
                sections =
                repository.getSections()
            )
        }
    }

    private fun processSectionClick(section: SectionWithExercises) {
        _state.value = SectionsScreenState.ShowingCardDetails(section = section)
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
        if (state.value is SectionsScreenState.ShowingCardDetails) {
            val state = (state.value as SectionsScreenState.ShowingCardDetails)
            _state.value = state.copy(
                section = state.section.copy(
                    exercises = state.section.exercises.map { oldExercise ->
                        if (oldExercise.id == exercise.id) {
                            newExercise
                        } else {
                            oldExercise
                        }
                    }
                )
            )
        }
    }
}