package com.sleepy.senpwaia.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController, query: String? = null, viewModel: AnimeViewModel = viewModel()) {
    var searchQuery by remember { query?.let { mutableStateOf(it) } ?: mutableStateOf("") }

    // Observe the ViewModel state
    val searchResults by viewModel.animeList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val scope = rememberCoroutineScope()

    LaunchedEffect(query) {
        if (!query.isNullOrEmpty()) {
            viewModel.searchAnime(query)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top app bar with back button
        TopAppBar(
            title = { Text("Search Anime") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            singleLine = true,
            onImeActionPerformed = {
                if (searchQuery.isNotBlank()) {
                    viewModel.searchAnime(searchQuery)
                }
            }
        )

        // Search button
        Button(
            onClick = {
                if (searchQuery.isNotBlank()) {
                    viewModel.searchAnime(searchQuery)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Search")
            }
        }

        // Results header
        if (searchResults.isNotEmpty()) {
            Text(
                text = "Search Results",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Results list
        LazyColumn {
            items(searchResults) { anime ->
                AnimeCard(
                    anime = anime,
                    onClick = {
                        navController.navigate("anime_details/${anime.id}")
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // No results message
        if (!isLoading && searchResults.isEmpty() && searchQuery.isNotEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No anime found. Try a different search term.")
            }
        }

        // Error message if there was an error
        error?.let { errorMessage ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: $errorMessage",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}