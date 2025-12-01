package com.sleepy.senpwaia.ui.download

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sleepy.senpwaia.models.DownloadProgress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadProgressScreen(navController: NavController) {
    // In a real app, this would fetch the current downloads from the download manager
    // For now, using sample data
    val downloads = listOf(
        DownloadProgress(
            animeId = "1",
            animeTitle = "Demon Slayer",
            episodeNumber = 5,
            progress = 45,
            totalSize = 500 * 1024 * 1024, // 500MB
            downloadedSize = (500 * 1024 * 1024 * 0.45).toLong() // 45% of 500MB
        ),
        DownloadProgress(
            animeId = "2",
            animeTitle = "Jujutsu Kaisen",
            episodeNumber = 12,
            progress = 78,
            totalSize = 450 * 1024 * 1024, // 450MB
            downloadedSize = (450 * 1024 * 1024 * 0.78).toLong() // 78% of 450MB
        )
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Top app bar with back button
        TopAppBar(
            title = { Text("Downloads") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Current Downloads",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        
        LazyColumn {
            items(downloads) { download ->
                DownloadItem(
                    download = download,
                    onCancel = {
                        // In a real implementation, this would cancel the download
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DownloadItem(
    download: DownloadProgress,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${download.animeTitle} - Episode ${download.episodeNumber}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                IconButton(
                    onClick = onCancel,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel Download",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            LinearProgressIndicator(
                progress = download.progress / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${download.progress}% - ${formatFileSize(download.downloadedSize)}/${formatFileSize(download.totalSize)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun formatFileSize(bytes: Long): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var size = bytes.toDouble()
    var unitIndex = 0
    
    while (size >= 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }
    
    return if (unitIndex == 0) {
        "${size.toInt()} ${units[unitIndex]}"
    } else {
        "%.2f ${units[unitIndex]}".format(size)
    }
}