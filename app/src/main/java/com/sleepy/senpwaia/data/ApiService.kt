package com.sleepy.senpwaia.data

import com.sleepy.senpwaia.models.Anime
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// API interface for AnimePahe
interface AnimePaheApi {
    @GET("api?m=search")
    suspend fun searchAnime(@Query("q") query: String): Response<SearchResponse>
    
    @GET("api?m=release")
    suspend fun getAnimeEpisodes(
        @Query("id") animeId: String,
        @Query("sort") sort: String = "episode_asc",
        @Query("page") page: Int
    ): Response<EpisodesResponse>
}

data class SearchResponse(
    val data: List<AnimeResult>? = null
)

data class AnimeResult(
    val session: String, // This is the anime ID
    val title: String,
    val type: String? = null,
    val year: Int? = null,
    val status: String? = null,
    val episodes: Int? = null
)

data class EpisodesResponse(
    val data: List<EpisodeResult>? = null,
    val total: Int? = null,
    val per_page: Int? = null,
    val current_page: Int? = null,
    val last_page: Int? = null,
    val next_page_url: String? = null
)

data class EpisodeResult(
    val id: Int? = null,
    val episode: Int? = null,
    val title: String? = null,
    val session: String, // Episode session ID
    val snapshot: String? = null,
    val created_at: String? = null
)