package com.mospee.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mospee.ui.history.HistoryScreen
import com.mospee.ui.home.HomeScreen
import com.mospee.ui.settings.SettingsScreen
import com.mospee.ui.summary.TripSummaryScreen
import com.mospee.ui.trip.LiveTripScreen

sealed class Screen(val route: String) {
    data object Home       : Screen("home")
    data object LiveTrip   : Screen("live_trip")
    data object Summary    : Screen("summary/{tripId}") {
        fun createRoute(tripId: Long) = "summary/$tripId"
    }
    data object History    : Screen("history")
    data object Settings   : Screen("settings")
}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onStartTrip = {
                    navController.navigate(Screen.LiveTrip.route) {
                        launchSingleTop = true
                    }
                },
                onOpenHistory = {
                    navController.navigate(Screen.History.route)
                },
                onOpenSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.LiveTrip.route) {
            LiveTripScreen(
                onTripStopped = { tripId ->
                    navController.navigate(Screen.Summary.createRoute(tripId)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Summary.route,
            arguments = listOf(
                navArgument("tripId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getLong("tripId") ?: return@composable
            TripSummaryScreen(
                tripId = tripId,
                onBack = { navController.popBackStack() },
                onOpenHistory = {
                    navController.navigate(Screen.History.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onTripClick = { tripId ->
                    navController.navigate(Screen.Summary.createRoute(tripId))
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
