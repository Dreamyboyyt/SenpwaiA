package com.sleepy.senpwaia.ui.download

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sleepy.senpwaia.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadMenuScreen(
    navController: NavController,
    animeId: String,
    animeTitle: String
) {
    var startEpisode by remember { mutableIntStateOf(1) }
    var endEpisode by remember { mutableIntStateOf(1) }
    var selectedQuality by remember { mutableStateOf(Constants.Q_720) }
    var isDub by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top app bar with back button
        TopAppBar(
            title = { Text("Download Settings") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Anime Name - Prefilled and non-editable
        OutlinedTextField(
            value = animeTitle,
            onValueChange = { }, // Non-editable
            label = { Text("Anime Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            enabled = false
        )
        
        // Episode Range
        Text(
            text = "Episode Range",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Starting Episode
            OutlinedTextField(
                value = startEpisode.toString(),
                onValueChange = { 
                    val num = it.toIntOrNull()
                    if (num != null && num > 0) startEpisode = num
                },
                label = { Text("Starting Episode") },
                modifier = Modifier.weight(1f)
            )
            
            // Ending Episode
            OutlinedTextField(
                value = endEpisode.toString(),
                onValueChange = { 
                    val num = it.toIntOrNull()
                    if (num != null && num > 0) endEpisode = num
                },
                label = { Text("Ending Episode") },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Quality Selection
        Text(
            text = "Quality",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        DropdownMenuExample(
            selectedOption = selectedQuality,
            onOptionSelected = { selectedQuality = it },
            options = Constants.QUALITIES
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Sub/Dub Selection
        Text(
            text = "Audio Language",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FilterChip(
                selected = !isDub,
                onClick = { isDub = false },
                label = { Text("Sub") }
            )
            
            FilterChip(
                selected = isDub,
                onClick = { isDub = true },
                label = { Text("Dub") }
            )
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Download Button
        Button(
            onClick = {
                // In a real implementation, this would start the download process
                // Create DownloadRequest and pass to download manager
                navController.navigate("download_progress")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Start Download")
        }
    }
}

@Composable
fun DropdownMenuExample(
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    options: List<String>
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Quality") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onOptionSelected(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}