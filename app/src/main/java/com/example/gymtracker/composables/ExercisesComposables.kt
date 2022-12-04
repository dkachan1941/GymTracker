package com.example.gymtracker.composables

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gymtracker.model.*
import com.example.gymtracker.viewmodel.ExercisesViewModel

@Composable
fun ExercisesContent(
    modifier: Modifier = Modifier,
    viewModel: ExercisesViewModel = hiltViewModel()
) {
    val uiState = viewModel.state.collectAsState().value
    val notifyEvent = { event: ExercisesScreenEvent -> viewModel.processInput(event) }
    if (uiState is ExercisesScreenState.ShowingExercises) {
        ListOfExercises(
            uiState.exercises,
            modifier = modifier,
        ) { exercise: Exercise -> notifyEvent(ExercisesScreenEvent.OnExerciseCompleted(exercise = exercise)) }
    }
}

@Composable
fun ListOfExercises(
    exercises: List<Exercise>,
    modifier: Modifier = Modifier,
    resetExercise: (Exercise) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(color = Color.White)
    ) {
        item { Spacer(modifier = Modifier.padding(8.dp)) }
        items(exercises.sortedByDescending {
            it.getDaysOfInactivity() ?: Long.MAX_VALUE
        }) { item ->
            ExerciseItemContent(
                exercise = item,
                onExerciseCompleted = {
                    resetExercise(it)
                }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExerciseItemContent(
    exercise: Exercise,
    onExerciseCompleted: (exercise: Exercise) -> Unit,
    modifier: Modifier = Modifier
) {
    val isConfirmationDialogShown = remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .background(color = Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Text(text = exercise.name,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .combinedClickable(
                    onLongClick = {
                        isConfirmationDialogShown.value = true
                    },
                    onClick = {}
                ))
        exercise.getDaysOfInactivity()?.let {
            val date = if (it == 0L) "today" else "$it day(s) ago"
            Text(
                text = "Done $date"
            )
        } ?: kotlin.run {
            Text(
                text = "New"
            )
        }
        Divider(
            color = Color.LightGray,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
    if (isConfirmationDialogShown.value) {
        ConfirmationResetDialog(
            exercise = exercise,
            isConfirmationDialogShown = isConfirmationDialogShown,
            onExerciseCompleted = onExerciseCompleted
        )
    }
}

@Composable
fun ConfirmationResetDialog(
    exercise: Exercise,
    modifier: Modifier = Modifier,
    isConfirmationDialogShown: MutableState<Boolean>,
    onExerciseCompleted: (exercise: Exercise) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            isConfirmationDialogShown.value = true
        },
        title = {
            Text(text = "Mark '${exercise.name}' as completed?")
        },
        buttons = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f),
                    onClick = {
                        onExerciseCompleted(exercise)
                        isConfirmationDialogShown.value = false
                    }
                ) {
                    Text("Yes")
                }
                Button(
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(1f),
                    onClick = { isConfirmationDialogShown.value = false }
                ) {
                    Text("No")
                }
            }
        }
    )
}
