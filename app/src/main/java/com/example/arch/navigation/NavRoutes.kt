package com.example.arch.navigation

sealed class NavRoutes(val route: String) {
    // Auth graph
    data object AuthGraph : NavRoutes("auth_graph")
    data object Login     : NavRoutes("login")
    data object Register  : NavRoutes("register")

    // Main graph
    data object MainGraph   : NavRoutes("main_graph")
    data object Home        : NavRoutes("home")
    data object Profile     : NavRoutes("profile")
    data object Settings    : NavRoutes("settings")

    // Post detail with argument
    data object PostDetail : NavRoutes("post/{postId}") {
        const val ARG_POST_ID = "postId"
        fun createRoute(postId: String) = "post/$postId"
    }
}
