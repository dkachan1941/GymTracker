package com.example.gymtracker.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.gymtracker.composables.MAIN_SCREEN_DESTINATION
import com.example.gymtracker.composables.mainScreen

@Composable
fun GymTrackerNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = MAIN_SCREEN_DESTINATION
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        mainScreen()
    }
}