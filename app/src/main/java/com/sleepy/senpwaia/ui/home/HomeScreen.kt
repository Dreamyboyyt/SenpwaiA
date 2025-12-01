package com.sleepy.senpwaia.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sleepy.senpwaia.models.Anime
import com.sleepy.senpwaia.ui.components.AnimeCard
import com.sleepy.senpwaia.ui.viewmodels.AnimeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: AnimeViewModel = viewModel()) {
    var searchQuery by remember { mutableStateOf("") }

    // For the home screen, we could load popular anime if we have an endpoint for that
    // For now, using sample data
    val popularAnime = listOf(
        Anime(
            id = "1",
            title = "Demon Slayer",
            pageLink = "",
            posterUrl = null,
            episodeCount = 26
        ),
        Anime(
            id = "2",
            title = "Jujutsu Kaisen",
            pageLink = "",
            posterUrl = null,
            episodeCount = 24
        ),
        Anime(
            id = "3",
            title = "Attack on Titan",
            pageLink = "",
            posterUrl = null,
            episodeCount = 75
        ),
        Anime(
            id = "4",
            title = "One Piece",
            pageLink = "",
            posterUrl = null,
            episodeCount = 1000 // Placeholder
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search Anime") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            singleLine = true
        )

        // Search button
        Button(
            onClick = {
                if (searchQuery.isNotBlank()) {
                    navController.navigate("search/$searchQuery")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Search")
        }

        // Popular Anime Section
        Text(
            text = "Popular Anime",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        LazyColumn {
            items(popularAnime) { anime ->
                AnimeCard(
                    anime = anime,
                    onClick = {
                        navController.navigate("anime_details/${anime.id}")
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}