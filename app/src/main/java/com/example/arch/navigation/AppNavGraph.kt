package com.example.arch.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.arch.feature.auth.presentation.login.LoginScreen
import com.example.arch.feature.auth.presentation.register.RegisterScreen
import com.example.arch.feature.home.presentation.HomeScreen
import com.example.arch.feature.profile.presentation.ProfileScreen
import com.example.arch.feature.settings.presentation.SettingsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavRoutes.AuthGraph.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        // Auth nested graph — isolated so feature:auth owns its own back-stack
        navigation(
            startDestination = NavRoutes.Login.route,
            route = NavRoutes.AuthGraph.route,
        ) {  composable(NavRoutes.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(NavRoutes.MainGraph.route) {
                            popUpTo(NavRoutes.AuthGraph.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(NavRoutes.Register.route) },
                )
            }
            composable(NavRoutes.Register.route) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(NavRoutes.MainGraph.route) {
                            popUpTo(NavRoutes.AuthGraph.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() },
                )
            }
        }

        // Main nested graph
        navigation(
            startDestination = NavRoutes.Home.route,
            route = NavRoutes.MainGraph.route,
        ) {
            composable(NavRoutes.Home.route) {
                HomeScreen(
                    onNavigateToProfile  = { navController.navigate(NavRoutes.Profile.route) },
                    onNavigateToSettings = { navController.navigate(NavRoutes.Settings.route) },
                    onSessionExpired     = {
                        navController.navigate(NavRoutes.AuthGraph.route) {
                            popUpTo(NavRoutes.MainGraph.route) { inclusive = true }
                        }
                    },
                )
            }
            composable(NavRoutes.Profile.route) {
                ProfileScreen(
                    onNavigateBack   = { navController.popBackStack() },
                    onSessionExpired = {
                        navController.navigate(NavRoutes.AuthGraph.route) {
                            popUpTo(NavRoutes.MainGraph.route) { inclusive = true }
                        }
                    },
                )
            }
            composable(NavRoutes.Settings.route) {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate(NavRoutes.AuthGraph.route) {
                            popUpTo(NavRoutes.MainGraph.route) { inclusive = true }
                        }
                    },
                )
            }
        }
    }
}
