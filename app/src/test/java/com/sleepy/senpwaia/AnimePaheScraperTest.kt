package com.sleepy.senpwaia.data

import com.sleepy.senpwaia.models.Anime
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

class AnimePaheScraperTest {
    
    private val scraper = AnimePaheScraper()
    
    @Test
    fun testSearchAnime() = runBlocking {
        // This test might fail if the API is down or network is unavailable
        // In a real test environment, we'd use mocks
        try {
            val results = scraper.searchAnime("Demon Slayer")
            assertTrue(results.isNotEmpty())
            
            val firstResult = results.first()
            assertNotNull(firstResult.id)
            assertNotNull(firstResult.title)
            assertTrue(firstResult.title.contains("Demon", ignoreCase = true))
        } catch (e: Exception) {
            // Fail the test if there's an unexpected error
            fail("Search failed with error: ${e.message}")
        }
    }
    
    @Test
    fun testSearchAnimeWithEmptyQuery() = runBlocking {
        try {
            val results = scraper.searchAnime("")
            assertTrue(results.isEmpty())
        } catch (e: Exception) {
            // An exception is expected for an empty query
            // This is acceptable behavior
        }
    }
    
    @Test
    fun testSearchAnimeWithInvalidQuery() = runBlocking {
        val results = scraper.searchAnime("asdhfjkghsdfkjghsdkjfghsdkjfghsdkjfgh")
        assertTrue(results.isEmpty())
    }
}