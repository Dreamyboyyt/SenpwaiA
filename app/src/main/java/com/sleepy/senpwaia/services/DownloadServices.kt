package com.sleepy.senpwaia.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.sleepy.senpwaia.MainActivity
import com.sleepy.senpwaia.R
import com.sleepy.senpwaia.data.AnimePaheScraper
import com.sleepy.senpwaia.models.DownloadRequest
import com.sleepy.senpwaia.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DownloadService : Service() {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "download_channel"
        const val NOTIFICATION_ID = 1
        const val DOWNLOAD_REQUEST_EXTRA = "download_request"
    }
    
    private val scraper = AnimePaheScraper()
    private val okHttpClient = OkHttpClient()
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val downloadRequest = intent?.getParcelableExtra<DownloadRequest>(DOWNLOAD_REQUEST_EXTRA)
        if (downloadRequest != null) {
            val notification = createNotification(downloadRequest.animeTitle)
            startForeground(NOTIFICATION_ID, notification.build())
            
            // Start the download work
            val workRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
                .setInputData(DownloadWorker.buildData(downloadRequest))
                .build()
            
            val workManager = WorkManager.getInstance(this)
            workManager.enqueueUniqueWork(
                "download_${downloadRequest.animeId}",
                ExistingWorkPolicy.KEEP,
                workRequest
            )
        }
        
        return START_NOT_STICKY
    }
    
    private fun createNotification(title: String): NotificationCompat.Builder {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Downloading $title")
            .setContentText("Download in progress...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Download Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for anime downloads"
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
}

class DownloadWorker(
    private val appContext: android.content.Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_DOWNLOAD_REQUEST = "download_request"

        fun buildData(downloadRequest: DownloadRequest): androidx.work.Data {
            return androidx.work.Data.Builder()
                .putString("animeId", downloadRequest.animeId)
                .putString("animeTitle", downloadRequest.animeTitle)
                .putInt("startEpisode", downloadRequest.startEpisode)
                .putInt("endEpisode", downloadRequest.endEpisode)
                .putString("quality", downloadRequest.quality)
                .putBoolean("isDub", downloadRequest.isDub)
                .putString("downloadPath", downloadRequest.downloadPath)
                .build()
        }
    }

    private val scraper = AnimePaheScraper()
    private val okHttpClient = OkHttpClient()

    override suspend fun doWork(): Result {
        return try {
            val animeId = inputData.getString("animeId") ?: return Result.failure()
            val animeTitle = inputData.getString("animeTitle") ?: return Result.failure()
            val startEpisode = inputData.getInt("startEpisode", 1)
            val endEpisode = inputData.getInt("endEpisode", 1)
            val quality = inputData.getString("quality") ?: "720p"
            val isDub = inputData.getBoolean("isDub", false)
            val downloadPath = inputData.getString("downloadPath") ?: ""
            
            // Get anime details to access episodes
            val animeDetails = scraper.getAnimeDetails(animeId)
            
            // Find episodes in the requested range
            val episodesToDownload = animeDetails.episodes.filter { 
                it.episodeNumber >= startEpisode && it.episodeNumber <= endEpisode 
            }
            
            // For each episode in the range, get download links and download
            for ((index, episode) in episodesToDownload.withIndex()) {
                // Update progress
                val progress = (index + 1) * 100 / episodesToDownload.size
                setProgressAsync(
                    androidx.work.Data.Builder()
                        .putInt("episode", episode.episodeNumber)
                        .putInt("progress", progress)
                        .build()
                )
                
                // Get episode download page URL
                val episodePageUrl = "https://animepahe.ru/play/$animeId/${episode.id}"
                
                // Get download links for this episode
                val downloadLinks = scraper.getEpisodeDownloadLinks(episodePageUrl)
                
                // Find the link matching the requested quality
                val qualityLink = downloadLinks[quality] ?: downloadLinks.values.firstOrNull()
                
                if (qualityLink != null) {
                    // Extract the direct download link
                    val directDownloadLink = scraper.extractDirectDownloadLink(qualityLink)
                    
                    // Download the episode
                    downloadEpisodeFile(
                        animeTitle = animeTitle,
                        episodeNumber = episode.episodeNumber,
                        downloadUrl = directDownloadLink,
                        downloadPath = downloadPath
                    )
                }
            }
            
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
    
    private suspend fun downloadEpisodeFile(
        animeTitle: String,
        episodeNumber: Int,
        downloadUrl: String,
        downloadPath: String
    ) = withContext(Dispatchers.IO) {
        try {
            val fileName = FileUtils.getEpisodeFileName(animeTitle, episodeNumber)
            val animeDir = FileUtils.createAnimeDirectory(appContext, animeTitle)
            val file = File(animeDir, fileName)
            
            // Create a temporary file while downloading
            val tempFile = File(animeDir, "${fileName}.tmp")
            
            // Create the download request
            val request = Request.Builder()
                .url(downloadUrl)
                .build()
            
            // Execute the request
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw Exception("Download failed with code: ${response.code}")
                }
                
                // Get the file size from the response
                val totalSize = response.body?.contentLength() ?: 0
                
                // Write the response body to file
                response.body?.byteStream()?.use { inputStream ->
                    FileOutputStream(tempFile).use { outputStream ->
                        val buffer = ByteArray(8192)
                        var bytesCopied = 0L
                        var bytesRead: Int
                        
                        while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                            outputStream.write(buffer, 0, bytesRead)
                            bytesCopied += bytesRead
                            
                            // Update progress in a background context
                            val progress = ((bytesCopied.toDouble() / totalSize) * 100).toInt()
                            // Note: We can't directly update work progress here due to threading
                            // For a complete implementation, we'd need to track progress differently
                        }
                    }
                }
            }
            
            // Rename the temporary file to the final name after download completes
            tempFile.renameTo(file)
        } catch (e: Exception) {
            e.printStackTrace()
            // Make sure to delete the temp file if download fails
            val fileName = FileUtils.getEpisodeFileName(animeTitle, episodeNumber)
            val animeDir = FileUtils.createAnimeDirectory(appContext, animeTitle)
            val tempFile = File(animeDir, "${fileName}.tmp")
            if (tempFile.exists()) {
                tempFile.delete()
            }
            throw e
        }
    }
}