package com.example.gymtracker.model

sealed class SectionsScreenState {

    object Initialized : SectionsScreenState()

    data class ShowingCards(
        val sections: List<SectionWithExercises> = emptyList()
    ) : SectionsScreenState()

    data class ShowingCardDetails(
        val section: SectionWithExercises
    ) : SectionsScreenState()

    data class ShowingExerciseDetails(
        val exercise: Exercise
    ) : SectionsScreenState()
}

sealed class SectionsScreenEvent {
    data class OnSectionClick(val section: SectionWithExercises) : SectionsScreenEvent()
    data class ResetExerciseDate(val exercise: Exercise) : SectionsScreenEvent()
    data class AddNewSection(val sectionName: String) : SectionsScreenEvent()
    data class AddNewExercise(val exerciseName: String, val sectionId: Long) : SectionsScreenEvent()
    data class UpdateExercise(val exercise: Exercise) : SectionsScreenEvent()
    data class ResetSection(val section: Section) : SectionsScreenEvent()
    data class ShowExerciseDetails(val exercise: Exercise) : SectionsScreenEvent()
    data class ChangeExerciseName(val exercise: Exercise, val exerciseName: String) :
        SectionsScreenEvent()

    object LoadData : SectionsScreenEvent()
    object ShowSectionsList : SectionsScreenEvent()
}