package com.sleepy.senpwaia.data

import com.sleepy.senpwaia.models.Anime
import com.sleepy.senpwaia.models.AnimeDetails
import com.sleepy.senpwaia.models.AiringStatus
import com.sleepy.senpwaia.models.Episode
import com.sleepy.senpwaia.utils.Constants
import com.sleepy.senpwaia.utils.PaheDecryptor
import com.sleepy.senpwaia.utils.UrlUtils
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AnimePaheScraper {
    private val apiService: AnimePaheApi
    private val okHttpClient: OkHttpClient

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.PAHE_HOME_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(AnimePaheApi::class.java)
        okHttpClient = OkHttpClient.Builder().build()
    }

    suspend fun searchAnime(query: String): List<Anime> {
        val response = apiService.searchAnime(query)
        if (!response.isSuccessful) {
            throw Exception("Failed to search anime: ${response.code()} - ${response.message()}")
        }

        val searchResponse = response.body()
        return searchResponse?.data?.map { result ->
            Anime(
                id = result.session,
                title = result.title,
                pageLink = UrlUtils.buildAnimePageUrl(result.session),
                episodeCount = result.episodes ?: 0
            )
        } ?: emptyList()
    }

    suspend fun getAnimeDetails(animeId: String): AnimeDetails {
        // First, get anime metadata from the main page
        val animePageUrl = UrlUtils.buildAnimePageUrl(animeId)
        val document = Jsoup.connect(animePageUrl).get()

        val title = document.select(".title-wrapper h1").text()
        val description = document.select(".anime-synopsis").text()

        // Extract poster URL
        val posterElement = document.selectFirst(".youtube-preview") ?: document.selectFirst(".poster-image")
        val posterUrl = posterElement?.attr("href")

        // Extract genres
        val genreElements = document.select(".anime-genre a")
        val genres = genreElements.map { it.attr("title") }

        // Extract release year
        val releaseYearElement = document.select('a[href*="/anime/season/"]').first()
        var releaseYear = 0
        if (releaseYearElement != null) {
            val seasonAndYear = releaseYearElement.attr("title")
            releaseYear = seasonAndYear.split(" ").lastOrNull()?.toIntOrNull() ?: 0
        }

        // Determine airing status
        val airingStatus = if (document.select("[title=\"Currently Airing\"]").isNotEmpty()) {
            AiringStatus.ONGOING
        } else if (document.select(".episode-number").isEmpty()) {
            AiringStatus.UPCOMING
        } else {
            AiringStatus.FINISHED
        }

        // Check if dub is available
        val isDubAvailable = checkDubAvailability(animeId)

        // Get episodes
        val episodes = getEpisodes(animeId)

        val anime = Anime(
            id = animeId,
            title = title,
            pageLink = animePageUrl,
            posterUrl = posterUrl,
            description = description,
            episodeCount = episodes.size,
            isDubAvailable = isDubAvailable
        )

        return AnimeDetails(
            anime = anime,
            episodes = episodes,
            description = description,
            genres = genres,
            releaseYear = releaseYear,
            airingStatus = airingStatus
        )
    }

    private suspend fun checkDubAvailability(animeId: String): Boolean {
        // Get first page of episodes to check if any are dubbed
        val response = apiService.getAnimeEpisodes(animeId, page = 1)
        if (!response.isSuccessful) return false

        val episodesResponse = response.body()
        val episodes = episodesResponse?.data ?: return false

        if (episodes.isNotEmpty()) {
            // Get episode details page to check for dub availability
            val firstEpisodeSession = episodes[0].session
            val episodePageUrl = UrlUtils.buildEpisodePageUrl(animeId, firstEpisodeSession)

            try {
                val document = Jsoup.connect(episodePageUrl).get()
                val dubLinks = document.select("a.dropdown-item:contains(eng)")

                return dubLinks.isNotEmpty()
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }

        return false
    }

    private suspend fun getEpisodes(animeId: String): List<Episode> {
        val allEpisodes = mutableListOf<Episode>()
        var currentPage = 1
        var hasMorePages = true

        while (hasMorePages) {
            val response = apiService.getAnimeEpisodes(animeId, page = currentPage)
            if (!response.isSuccessful) break

            val episodesResponse = response.body()
            val episodes = episodesResponse?.data ?: break

            // Process episodes from current page
            episodes.forEach { episodeResult ->
                val episode = Episode(
                    id = episodeResult.session,
                    episodeNumber = episodeResult.episode ?: 0,
                    title = episodeResult.title
                )
                allEpisodes.add(episode)
            }

            // Check if there are more pages
            hasMorePages = episodesResponse.next_page_url != null
            currentPage++
        }

        return allEpisodes
    }

    suspend fun getEpisodeDownloadLinks(episodePageUrl: String): Map<String, String> {
        val downloadLinks = mutableMapOf<String, String>()

        try {
            val document = Jsoup.connect(episodePageUrl).get()

            // Find download options on the page
            val downloadOptions = document.select("a.dropdown-item[target='_blank']")

            for (option in downloadOptions) {
                val link = option.attr("href")
                val text = option.text()

                // Extract quality from text (e.g., "1080p", "720p", etc.) and check for sub/dub
                val qualityRegex = Regex("\\b(\\d{3,4})p\\b")
                val qualityMatch = qualityRegex.find(text)

                if (qualityMatch != null) {
                    val quality = qualityMatch.value
                    // Add the intermediate link - later we'll decrypt to get the actual download link
                    downloadLinks[quality] = link
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return downloadLinks
    }

    /**
     * Extract direct download links from the intermediate download page
     */
    suspend fun extractDirectDownloadLink(downloadPageUrl: String): String {
        return PaheDecryptor.extractDownloadLink(downloadPageUrl, okHttpClient)
    }
}