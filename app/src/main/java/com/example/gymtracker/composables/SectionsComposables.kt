package com.example.gymtracker.composables

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gymtracker.R
import com.example.gymtracker.model.*
import com.example.gymtracker.viewmodel.SectionsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SectionsContent(
    modifier: Modifier = Modifier,
    viewModel: SectionsViewModel = hiltViewModel()
) {
    val notifyEvent = { event: SectionsScreenEvent -> viewModel.processInput(event) }
    when (val uiState = viewModel.state.collectAsState().value) {
        is SectionsScreenState.ShowingCards ->
            MainSectionsList(
                state = uiState,
                notifyEvent = notifyEvent
            )
        is SectionsScreenState.ShowingCardDetails ->
            SectionDetails(
                state = uiState,
                notifyEvent = notifyEvent
            )
        is SectionsScreenState.ShowingExerciseDetails -> {
            ExerciseDetails(
                exercise = uiState.exercise,
                onExerciseNameChanged = { exercise: Exercise, exerciseName: String ->
                    notifyEvent(
                        SectionsScreenEvent.ChangeExerciseName(
                            exercise = exercise,
                            exerciseName = exerciseName
                        )
                    )
                },
                updateExercise = { exercise: Exercise ->
                    notifyEvent(SectionsScreenEvent.UpdateExercise(exercise = exercise))
                }
            )
            BackHandler {
                notifyEvent(SectionsScreenEvent.ShowSectionsList)
            }
        }
        SectionsScreenState.Initialized -> Unit
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SectionDetails(
    state: SectionsScreenState.ShowingCardDetails,
    notifyEvent: (SectionsScreenEvent) -> Unit
) {
    val newExerciseBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {},
        content = {
            SectionDetailsContent(
                state = state,
                notifyEvent = notifyEvent,
                modifier = Modifier.padding(it)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                coroutineScope.launch {
                    newExerciseBottomSheetState.show()
                }
            }) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = "fab",
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    )
    NewExerciseBottomSheet(
        newExerciseBottomSheetState = newExerciseBottomSheetState,
        coroutineScope = coroutineScope,
        notifyEvent = notifyEvent,
        sectionId = state.section.section.id
    )

    BackHandler {
        notifyEvent(SectionsScreenEvent.ShowSectionsList)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NewExerciseBottomSheet(
    newExerciseBottomSheetState: ModalBottomSheetState,
    coroutineScope: CoroutineScope,
    notifyEvent: (SectionsScreenEvent) -> Unit,
    sectionId: Long
) {
    ModalBottomSheetLayout(
        sheetBackgroundColor = Color.Transparent,
        sheetState = newExerciseBottomSheetState,
        sheetContent = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .wrapContentHeight()
                    .padding(32.dp)
            ) {
                Column {
                    val name = remember { mutableStateOf("") }
                    Text(text = "New exercise name:", modifier = Modifier.fillMaxWidth())
                    TextField(value = name.value, onValueChange = {
                        name.value = it
                    }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = {
                        notifyEvent(
                            SectionsScreenEvent.AddNewExercise(
                                exerciseName = name.value,
                                sectionId = sectionId
                            )
                        )
                        coroutineScope.launch {
                            newExerciseBottomSheetState.hide()
                        }
                    }) {
                        Text(text = "Add new exercise.", modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    ) {}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SectionDetailsContent(
    state: SectionsScreenState.ShowingCardDetails,
    notifyEvent: (SectionsScreenEvent) -> Unit,
    modifier: Modifier
) {
    val isConfirmationDialogShown = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .background(color = Color.White)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .combinedClickable(
                    onClick = {},
                    onLongClick = {
                        isConfirmationDialogShown.value = true
                    }
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = state.section.section.name,
                style = MaterialTheme.typography.h6,
            )
            state.section.getDaysOfInactivity()?.let {
                val date = if (it == 0L) "today" else "$it day(s) ago"
                Text(
                    text = "Done $date",
                    style = MaterialTheme.typography.body2,
                    modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            } ?: run {
                Text(
                    text = "New",
                    style = MaterialTheme.typography.body2,
                    modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        Divider(
            thickness = 1.dp, modifier = Modifier.fillMaxWidth()
        )
        ListOfExercises(
            exercises = state.section.exercises,
            onExerciseClicked = { exercise: Exercise ->
                notifyEvent(SectionsScreenEvent.ShowExerciseDetails(exercise = exercise))
            }
        )
    }
    if (isConfirmationDialogShown.value) {
        ConfirmationResetSectionDialog(
            section = state.section.section,
            isConfirmationDialogShown = isConfirmationDialogShown,
            onSectionCompleted = {
                notifyEvent(SectionsScreenEvent.ResetSection(section = state.section.section))
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainSectionsList(
    state: SectionsScreenState.ShowingCards,
    notifyEvent: (SectionsScreenEvent) -> Unit
) {
    val newSectionBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = {},
        content = {
            SectionsListContent(
                state = state,
                notifyEvent = notifyEvent,
                modifier = Modifier.padding(it)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                coroutineScope.launch {
                    newSectionBottomSheetState.show()
                }
            }) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = "fab",
                    contentScale = ContentScale.FillBounds
                )
            }
        }
    )
    NewSectionBottomSheet(
        newSectionBottomSheetState = newSectionBottomSheetState,
        coroutineScope = coroutineScope,
        notifyEvent = notifyEvent
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NewSectionBottomSheet(
    newSectionBottomSheetState: ModalBottomSheetState,
    coroutineScope: CoroutineScope,
    notifyEvent: (SectionsScreenEvent) -> Unit
) {
    ModalBottomSheetLayout(
        sheetBackgroundColor = Color.Transparent,
        sheetState = newSectionBottomSheetState,
        sheetContent = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .wrapContentHeight()
                    .padding(32.dp)
            ) {
                Column {
                    val sectionName = remember { mutableStateOf("") }
                    Text(text = "New section name:", modifier = Modifier.fillMaxWidth())
                    TextField(value = sectionName.value, onValueChange = {
                        sectionName.value = it
                    }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = {
                        notifyEvent(SectionsScreenEvent.AddNewSection(sectionName = sectionName.value))
                        coroutineScope.launch {
                            newSectionBottomSheetState.hide()
                        }
                    }) {
                        Text(text = "Add new section.", modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    ) {}
}

@Composable
fun SectionsListContent(
    state: SectionsScreenState.ShowingCards,
    notifyEvent: (SectionsScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(vertical = 8.dp)
            .verticalScroll(rememberScrollState())
    ) {
        state.sections.sortedByDescending { it.getDaysOfInactivity() ?: Long.MAX_VALUE }
            .forEach { section ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = 8.dp,
                    content = {
                        CardContent(section = section, notifyEvent = notifyEvent)
                    }
                )
            }
    }
}

@Composable
fun CardContent(
    section: SectionWithExercises,
    modifier: Modifier = Modifier,
    notifyEvent: (SectionsScreenEvent) -> Unit
) {
    Column(
        modifier = modifier
            .padding(vertical = 4.dp)
            .fillMaxWidth()
            .clickable {
                notifyEvent(SectionsScreenEvent.OnSectionClick(section))
            },
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = section.section.name,
                style = MaterialTheme.typography.h6,
                modifier = modifier
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            )
            section.getDaysOfInactivity()?.let {
                val date = if (it == 0L) "today" else "$it day(s) ago"
                Text(
                    text = "Done $date",
                    style = MaterialTheme.typography.body2,
                    modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            } ?: run {
                Text(
                    text = "New",
                    style = MaterialTheme.typography.body2,
                    modifier = modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
        if (section.exercises.isNotEmpty()) {
            Divider(
                thickness = 1.dp, modifier = Modifier.fillMaxWidth()
            )
        }
        section.exercises.forEach {
            Text(
                text = it.name,
                style = MaterialTheme.typography.body2,
                modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun ConfirmationResetSectionDialog(
    section: Section,
    modifier: Modifier = Modifier,
    isConfirmationDialogShown: MutableState<Boolean>,
    onSectionCompleted: (section: Section) -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            isConfirmationDialogShown.value = true
        },
        title = {
            Text(text = "Mark '${section.name}' as completed?")
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
                        onSectionCompleted(section)
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