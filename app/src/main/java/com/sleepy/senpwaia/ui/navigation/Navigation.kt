package com.sleepy.senpwaia.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sleepy.senpwaia.ui.home.HomeScreen
import com.sleepy.senpwaia.ui.search.SearchScreen
import com.sleepy.senpwaia.ui.details.AnimeDetailsScreen
import com.sleepy.senpwaia.ui.download.DownloadMenuScreen
import com.sleepy.senpwaia.ui.download.DownloadProgressScreen
import com.sleepy.senpwaia.ui.settings.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(navController = navController)
        }
        composable("search/{query}") { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            SearchScreen(navController = navController, query = query)
        }
        composable("anime_details/{animeId}") {
            val animeId = it.arguments?.getString("animeId") ?: ""
            AnimeDetailsScreen(navController = navController, animeId = animeId)
        }
        composable("download_menu/{animeId}/{animeTitle}") {
            val animeId = it.arguments?.getString("animeId") ?: ""
            val animeTitle = it.arguments?.getString("animeTitle") ?: ""
            DownloadMenuScreen(navController = navController, animeId = animeId, animeTitle = animeTitle)
        }
        composable("download_progress") {
            DownloadProgressScreen(navController = navController)
        }
        composable("settings") {
            SettingsScreen(navController = navController)
        }
    }
}