package com.sleepy.senpwaia.ui.settings

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
        subOrDub = context.dataStore.getSetting("sub_or_dub", "sub")
        quality = context.dataStore.getSetting("quality", "720p")
        allowNotifications = context.dataStore.getBooleanSetting("allow_notifications", true)
        ignoreFillers = context.dataStore.getBooleanSetting("ignore_fillers", false)
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
            items(qualities.size) { index ->
                FilterChip(
                    selected = index == selectedQualityIndex,
                    onClick = {
                        selectedQualityIndex = index
                        scope.launch {
                            context.dataStore.saveSetting("quality", qualities[index])
                            quality = qualities[index]
                        }
                    },
                    label = { Text(qualities[index]) },
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
        var selectedAudioIndex by remember { mutableIntStateOf(if (subOrDub == "dub") 1 else 0) }
        
        LazyRow {
            items(audioOptions.size) { index ->
                FilterChip(
                    selected = index == selectedAudioIndex,
                    onClick = {
                        selectedAudioIndex = index
                        val selectedValue = if (index == 1) "dub" else "sub"
                        scope.launch {
                            context.dataStore.saveSetting("sub_or_dub", selectedValue)
                            subOrDub = selectedValue
                        }
                    },
                    label = { Text(audioOptions[index]) },
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
                        context.dataStore.saveBooleanSetting("allow_notifications", checked)
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
                        context.dataStore.saveBooleanSetting("ignore_fillers", checked)
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

// Extension functions to work with DataStore
suspend fun DataStore<Preferences>.getSetting(key: String, defaultValue: String): String {
    val data = this.data.first()
    return data[stringPreferencesKey(key)] ?: defaultValue
}

suspend fun DataStore<Preferences>.getBooleanSetting(key: String, defaultValue: Boolean): Boolean {
    val data = this.data.first()
    return data[booleanPreferencesKey(key)] ?: defaultValue
}

suspend fun DataStore<Preferences>.saveSetting(key: String, value: String) {
    this.edit { preferences ->
        preferences[stringPreferencesKey(key)] = value
    }
}

suspend fun DataStore<Preferences>.saveBooleanSetting(key: String, value: Boolean) {
    this.edit { preferences ->
        preferences[booleanPreferencesKey(key)] = value
    }
}

fun openUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}