package com.example.vidyarthibus3

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

sealed class Screen(val route: String) {
    object Dashboard    : Screen("dashboard")
    object Schedule     : Screen("schedule")
    object Alternatives : Screen("alternatives")
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            BusDashboard(navController = navController)
        }
        composable(Screen.Schedule.route) {
            ScheduleScreen(navController = navController)
        }
        composable(Screen.Alternatives.route) {
            AlternativesScreen(navController = navController)
        }
    }
}