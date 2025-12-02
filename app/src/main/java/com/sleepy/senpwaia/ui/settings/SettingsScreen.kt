package com.sleepy.senpwaia.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavController
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Load settings from DataStore
    var subOrDub by remember { mutableStateOf("sub") }
    var quality by remember { mutableStateOf("720p") }
    var allowNotifications by remember { mutableStateOf(true) }
    var ignoreFillers by remember { mutableStateOf(false) }
    
    // Load settings on first composition
    LaunchedEffect(Unit) {
        val data = context.dataStore.data.first()
        subOrDub = data[stringPreferencesKey("sub_or_dub")] ?: "sub"
        quality = data[stringPreferencesKey("quality")] ?: "720p"
        allowNotifications = data[booleanPreferencesKey("allow_notifications")] ?: true
        ignoreFillers = data[booleanPreferencesKey("ignore_fillers")] ?: false
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top app bar with back button
        TopAppBar(
            title = { Text("Settings") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        // Quality Settings
        Text(
            text = "Quality",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        
        val qualities = listOf("360p", "480p", "720p", "1080p")
        var selectedQualityIndex by remember { mutableIntStateOf(qualities.indexOf(quality).takeIf { it >= 0 } ?: 2) }
        
        LazyRow {
            itemsIndexed(qualities) { index, item ->
                FilterChip(
                    selected = item == quality,
                    onClick = {
                        scope.launch {
                            context.dataStore.edit { preferences ->
                                preferences[stringPreferencesKey("quality")] = item
                            }
                            quality = item
                        }
                    },
                    label = { Text(item) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        
        // Audio Settings
        Text(
            text = "Audio Language",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        
        val audioOptions = listOf("Sub", "Dub")
        
        LazyRow {
            itemsIndexed(audioOptions) { index, item ->
                FilterChip(
                    selected = item.lowercase() == subOrDub,
                    onClick = {
                        val selectedValue = item.lowercase()
                        scope.launch {
                            context.dataStore.edit { preferences ->
                                preferences[stringPreferencesKey("sub_or_dub")] = selectedValue
                            }
                            subOrDub = selectedValue
                        }
                    },
                    label = { Text(item) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }
        
        // Notification Settings
        Text(
            text = "Notifications",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Allow notifications")
            Switch(
                checked = allowNotifications,
                onCheckedChange = { checked ->
                    scope.launch {
                        context.dataStore.edit { preferences ->
                            preferences[booleanPreferencesKey("allow_notifications")] = checked
                        }
                        allowNotifications = checked
                    }
                }
            )
        }
        
        // Filler Settings
        Text(
            text = "Filler Episodes",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Ignore filler episodes")
            Switch(
                checked = ignoreFillers,
                onCheckedChange = { checked ->
                    scope.launch {
                        context.dataStore.edit { preferences ->
                            preferences[booleanPreferencesKey("ignore_fillers")] = checked
                        }
                        ignoreFillers = checked
                    }
                }
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // About section - this is what was requested in the spec
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "About",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "Made by Sleepy 😴",
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val githubUrl = "https://github.com/Dreamyboyyt/"
                TextButton(
                    onClick = { openUrl(context, githubUrl) },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    Text("GitHub: https://github.com/Dreamyboyyt/")
                }
            }
        }
    }
}

private fun openUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}