package com.sleepy.senpwaia.data

import com.sleepy.senpwaia.models.Anime
import com.sleepy.senpwaia.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AnimeRepository {
    private val apiService: AnimePaheApi
    
    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.PAHE_HOME_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        
        apiService = retrofit.create(AnimePaheApi::class.java)
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
                pageLink = Constants.ANIME_PAGE_URL.replace("{}", result.session),
                episodeCount = result.episodes ?: 0
            )
        } ?: emptyList()
    }
    
    suspend fun getAnimeEpisodes(animeId: String, page: Int = 1) = 
        apiService.getAnimeEpisodes(animeId, page = page)
}