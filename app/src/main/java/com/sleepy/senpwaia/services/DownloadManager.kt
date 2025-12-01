package com.sleepy.senpwaia.services

import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.sleepy.senpwaia.models.DownloadRequest

class DownloadManager(private val context: Context) {
    
    fun startDownload(downloadRequest: DownloadRequest) {
        // Start the foreground service to handle the download
        val intent = Intent(context, DownloadService::class.java).apply {
            putExtra(DownloadService.DOWNLOAD_REQUEST_EXTRA, downloadRequest)
        }
        context.startForegroundService(intent)
        
        // Also schedule the work with WorkManager for robustness
        val workRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
            .setInputData(DownloadWorker.buildData(downloadRequest))
            .build()
        
        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniqueWork(
            "download_${downloadRequest.animeId}",
            ExistingWorkPolicy.REPLACE,  // Replace any existing work for the same anime
            workRequest
        )
    }
    
    fun cancelDownload(animeId: String) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork("download_${animeId}")
    }
    
    fun getDownloadProgress(animeId: String): Int {
        // In a real implementation, this would return actual download progress
        // For now, returning a dummy value
        return 0
    }
}