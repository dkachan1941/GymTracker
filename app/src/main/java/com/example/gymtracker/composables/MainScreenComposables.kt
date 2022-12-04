package com.example.gymtracker.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.gymtracker.model.*
import com.example.gymtracker.viewmodel.ExercisesViewModel
import com.example.gymtracker.viewmodel.SectionsViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import java.util.*

internal const val MAIN_SCREEN_DESTINATION = "mainScreen"


internal fun NavGraphBuilder.mainScreen() {
    composable(route = MAIN_SCREEN_DESTINATION) {
        MainScreenRoute()
    }
}

@Composable
private fun MainScreenRoute() {
    MainScreenContent()
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun MainScreenContent(
    modifier: Modifier = Modifier,
    exercisesViewModel: ExercisesViewModel = hiltViewModel(),
    sectionsViewModel: SectionsViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val pagerState = rememberPagerState()
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    color = Color.Red,
                    modifier = Modifier.pagerTabIndicatorOffset(
                        pagerState,
                        tabPositions
                    ),
                )
            }
        ) {
            listOf("Sections", "Exercises").forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }
        HorizontalPager(count = 2, state = pagerState) { page ->
            when (page) {
                0 -> SectionsContent()
                1 -> ExercisesContent()
            }
        }

        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                if (page == 0) {
                    sectionsViewModel.loadData()
                } else if (page == 1) {
                    exercisesViewModel.loadData()
                }
            }
        }
    }
}

fun onPageChange(page: Int) {
    TODO("Not yet implemented")
}

