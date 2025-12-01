package com.sleepy.senpwaia.utils

import org.junit.Test
import org.junit.Assert.*

class ConstantsTest {
    
    @Test
    fun testConstantsDefined() {
        // Test that constants are properly defined
        assertNotNull(Constants.PAHE_DOMAIN)
        assertNotNull(Constants.PAHE_HOME_URL)
        assertNotNull(Constants.API_ENTRY_POINT)
        assertNotNull(Constants.ANIME_PAGE_URL)
        assertNotNull(Constants.EPISODE_PAGE_URL)
        assertNotNull(Constants.LOAD_EPISODES_URL)
        assertNotNull(Constants.DUB_PATTERN)
        
        // Check that PAHE_HOME_URL is formed correctly
        assertTrue(Constants.PAHE_HOME_URL.contains(Constants.PAHE_DOMAIN))
        
        // Check that qualities are defined
        assertTrue(Constants.QUALITIES.isNotEmpty())
        assertTrue(Constants.QUALITIES.contains("360p"))
        assertTrue(Constants.QUALITIES.contains("480p"))
        assertTrue(Constants.QUALITIES.contains("720p"))
        assertTrue(Constants.QUALITIES.contains("1080p"))
    }
    
    @Test
    fun testUrlUtils() {
        val animeId = "test123"
        val episodeSession = "episode456"
        val page = 2
        
        val animeUrl = UrlUtils.buildAnimePageUrl(animeId)
        assertTrue(animeUrl.contains(animeId))
        
        val episodeUrl = UrlUtils.buildEpisodePageUrl(animeId, episodeSession)
        assertTrue(episodeUrl.contains(animeId))
        assertTrue(episodeUrl.contains(episodeSession))
        
        val loadUrl = UrlUtils.buildLoadEpisodesUrl("https://example.com", page)
        assertTrue(loadUrl.contains("page=$page"))
    }
}