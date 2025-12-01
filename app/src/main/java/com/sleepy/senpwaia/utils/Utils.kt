package com.sleepy.senpwaia.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import java.io.File

object Constants {
    const val PAHE_DOMAIN = "animepahe.ru"
    const val PAHE_HOME_URL = "https://$PAHE_DOMAIN"
    const val API_ENTRY_POINT = "$PAHE_HOME_URL/api?m="
    const val ANIME_PAGE_URL = "${API_ENTRY_POINT}release&id={}&sort=episode_asc"
    const val EPISODE_PAGE_URL = "$PAHE_HOME_URL/play/{}/{}"
    const val LOAD_EPISODES_URL = "{}&page={}"
    const val DUB_PATTERN = "eng"
    const val SUB = "sub"
    const val DUB = "dub"
    const val Q_1080 = "1080p"
    const val Q_720 = "720p"
    const val Q_480 = "480p"
    const val Q_360 = "360p"
    
    val QUALITIES = listOf(Q_360, Q_480, Q_720, Q_1080)
}

object FileUtils {
    fun getDownloadPath(context: Context, animeTitle: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Android 11+ uses scoped storage
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/Anime/$animeTitle"
        } else {
            // For older versions, we might need permission to write to external storage
            "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/Anime/$animeTitle"
        }
    }
    
    fun createAnimeDirectory(context: Context, animeTitle: String): File {
        val downloadPath = getDownloadPath(context, animeTitle)
        val animeDir = File(downloadPath)
        if (!animeDir.exists()) {
            animeDir.mkdirs()
        }
        return animeDir
    }
    
    fun getEpisodeFileName(animeTitle: String, episodeNumber: Int): String {
        val paddedEpisode = String.format("%02d", episodeNumber)
        return "${animeTitle}_E${paddedEpisode}.mp4"
    }
}

object UrlUtils {
    fun buildAnimePageUrl(animeId: String): String {
        return Constants.ANIME_PAGE_URL.replace("{}", animeId)
    }
    
    fun buildEpisodePageUrl(animeId: String, episodeSession: String): String {
        return Constants.EPISODE_PAGE_URL.replace("{}", animeId).replace("{}", episodeSession)
    }
    
    fun buildLoadEpisodesUrl(animePageLink: String, page: Int): String {
        return Constants.LOAD_EPISODES_URL.replace("{}", animePageLink).replace("{}", page.toString())
    }
}

object NetworkUtils {
    fun openUrlInBrowser(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            ContextCompat.startActivity(context, intent, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}