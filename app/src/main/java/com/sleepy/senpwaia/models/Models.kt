package com.sleepy.senpwaia.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Anime(
    val id: String,
    val title: String,
    val pageLink: String,
    val posterUrl: String? = null,
    val description: String? = null,
    val episodeCount: Int = 0,
    val isDubAvailable: Boolean = false
) : Parcelable

@Parcelize
data class AnimeDetails(
    val anime: Anime,
    val episodes: List<Episode> = emptyList(),
    val description: String? = null,
    val genres: List<String> = emptyList(),
    val releaseYear: Int = 0,
    val airingStatus: AiringStatus = AiringStatus.FINISHED
) : Parcelable

enum class AiringStatus {
    ONGOING, UPCOMING, FINISHED
}

@Parcelize
data class Episode(
    val id: String,
    val episodeNumber: Int,
    val title: String? = null,
    val description: String? = null,
    val downloadLinks: Map<String, String> = emptyMap() // Quality to download link mapping
) : Parcelable

@Parcelize
data class DownloadRequest(
    val animeId: String,
    val animeTitle: String,
    val startEpisode: Int,
    val endEpisode: Int,
    val quality: String = "720p",
    val isDub: Boolean = false,
    val downloadPath: String = ""
) : Parcelable

@Parcelize
data class DownloadProgress(
    val animeId: String,
    val animeTitle: String,
    val episodeNumber: Int,
    val progress: Int, // 0-100
    val totalSize: Long,
    val downloadedSize: Long
) : Parcelable

@Parcelize
data class Settings(
    val subOrDub: String = "sub",
    val quality: String = "720p",
    val downloadFolderPath: String = "/storage/emulated/0/Download/Anime",
    val maxSimultaneousDownloads: Int = 2,
    val allowNotifications: Boolean = true,
    val isIgnoreFillers: Boolean = false
) : Parcelable