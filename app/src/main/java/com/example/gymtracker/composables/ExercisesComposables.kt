package com.example.gymtracker.composables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gymtracker.model.*
import com.example.gymtracker.viewmodel.ExercisesViewModel
import java.util.concurrent.TimeUnit

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
            onExerciseClicked = { exercise: Exercise ->
                notifyEvent(
                    ExercisesScreenEvent.OnExerciseClicked(
                        exercise = exercise
                    )
                )
            }
        )
    } else if (uiState is ExercisesScreenState.ShowingExerciseDetails) {
        ExerciseDetails(
            exercise = uiState.exercise,
            updateExercise = { exercise: Exercise ->
                notifyEvent(
                    ExercisesScreenEvent.OnExerciseCompleted(
                        exercise = exercise
                    )
                )
            },
            onExerciseNameChanged = { exercise: Exercise, exerciseName: String ->
                notifyEvent(
                    ExercisesScreenEvent.ChangeExerciseName(
                        exercise = exercise,
                        exerciseName = exerciseName
                    )
                )
            }
        )
        BackHandler {
            notifyEvent(ExercisesScreenEvent.LoadData)
        }
    }
}

@Composable
fun ExerciseDetails(
    exercise: Exercise,
    modifier: Modifier = Modifier,
    onExerciseNameChanged: (Exercise, String) -> Unit,
    updateExercise: (Exercise) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(color = Color.White)
    ) {
        Column(
            modifier = modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp)
                .background(color = Color.White)
        ) {

            Divider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            var exerciseName by remember { mutableStateOf(exercise.name) }
            TextField(modifier = Modifier.fillMaxWidth(), value = exerciseName, onValueChange = {
                exerciseName = it
            }, placeholder = { Text(text = "Change name") })

            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                onExerciseNameChanged(
                    exercise,
                    exerciseName
                )
            }) {
                Text(text = "Apply.")
            }

            Divider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            exercise.getDaysOfInactivity()?.let {
                val date = if (it == 0L) "today" else "$it day(s) ago"
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "Done $date"
                )
            } ?: kotlin.run {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "New"
                )
            }
            Button(modifier = Modifier.fillMaxWidth(), onClick = {
                val newExercise = exercise.copy(
                    lastDoneTimestamp = System.currentTimeMillis(),
                )
                updateExercise(
                    newExercise
                )
            }) {
                Text(text = "Set Done Today")
            }
            Row {
                Button(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), onClick = {
                    val newExercise = exercise.copy(
                        lastDoneTimestamp = (exercise.lastDoneTimestamp
                            ?: 0) + TimeUnit.DAYS.toMillis(1),
                    )
                    updateExercise(
                        newExercise
                    )
                }) {
                    Text(text = "+ 1 Day")
                }
                Button(modifier = Modifier.weight(1f).padding(horizontal = 8.dp), onClick = {
                    val newExercise = exercise.copy(
                        lastDoneTimestamp = (exercise.lastDoneTimestamp
                            ?: 0) - TimeUnit.DAYS.toMillis(1),
                    )
                    updateExercise(
                        newExercise
                    )
                }) {
                    Text(text = "- 1 Day")
                }
            }

            Divider(
                thickness = 1.dp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}

@Composable
fun ListOfExercises(
    exercises: List<Exercise>,
    modifier: Modifier = Modifier,
    onExerciseClicked: (Exercise) -> Unit
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
                onExerciseClicked = onExerciseClicked
            )
        }
    }
}

@Composable
fun ExerciseItemContent(
    exercise: Exercise,
    modifier: Modifier = Modifier,
    onExerciseClicked: (Exercise) -> Unit
) {
    val isConfirmationDialogShown = remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .background(color = Color.White)
            .padding(horizontal = 16.dp)
            .clickable {
                onExerciseClicked(exercise)
            }
    ) {
        Text(
            text = exercise.name,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        )
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
//        ConfirmationResetDialog(
//            exercise = exercise,
//            isConfirmationDialogShown = isConfirmationDialogShown,
//            onExerciseCompleted = onExerciseCompleted
//        )
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
