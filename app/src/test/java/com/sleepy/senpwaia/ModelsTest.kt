package com.sleepy.senpwaia.models

import org.junit.Test
import org.junit.Assert.*

class ModelsTest {
    
    @Test
    fun testAnimeModel() {
        val anime = Anime(
            id = "123",
            title = "Test Anime",
            pageLink = "https://example.com/anime/123",
            posterUrl = "https://example.com/poster.jpg",
            description = "Test description",
            episodeCount = 12,
            isDubAvailable = true
        )
        
        assertEquals("123", anime.id)
        assertEquals("Test Anime", anime.title)
        assertEquals("https://example.com/anime/123", anime.pageLink)
        assertEquals("https://example.com/poster.jpg", anime.posterUrl)
        assertEquals("Test description", anime.description)
        assertEquals(12, anime.episodeCount)
        assertTrue(anime.isDubAvailable)
    }
    
    @Test
    fun testAnimeWithDefaults() {
        val anime = Anime(
            id = "456",
            title = "Default Test Anime",
            pageLink = "https://example.com/anime/456"
        )
        
        assertEquals("456", anime.id)
        assertEquals("Default Test Anime", anime.title)
        assertEquals("https://example.com/anime/456", anime.pageLink)
        assertNull(anime.posterUrl)
        assertNull(anime.description)
        assertEquals(0, anime.episodeCount)
        assertFalse(anime.isDubAvailable)
    }
    
    @Test
    fun testDownloadRequest() {
        val downloadRequest = DownloadRequest(
            animeId = "789",
            animeTitle = "Download Test",
            startEpisode = 1,
            endEpisode = 10,
            quality = "720p",
            isDub = true,
            downloadPath = "/storage/emulated/0/Download/Anime"
        )
        
        assertEquals("789", downloadRequest.animeId)
        assertEquals("Download Test", downloadRequest.animeTitle)
        assertEquals(1, downloadRequest.startEpisode)
        assertEquals(10, downloadRequest.endEpisode)
        assertEquals("720p", downloadRequest.quality)
        assertTrue(downloadRequest.isDub)
        assertEquals("/storage/emulated/0/Download/Anime", downloadRequest.downloadPath)
    }
    
    @Test
    fun testSettingsModel() {
        val settings = Settings(
            subOrDub = "dub",
            quality = "1080p",
            downloadFolderPath = "/custom/path",
            maxSimultaneousDownloads = 3,
            allowNotifications = false,
            isIgnoreFillers = true
        )
        
        assertEquals("dub", settings.subOrDub)
        assertEquals("1080p", settings.quality)
        assertEquals("/custom/path", settings.downloadFolderPath)
        assertEquals(3, settings.maxSimultaneousDownloads)
        assertFalse(settings.allowNotifications)
        assertTrue(settings.isIgnoreFillers)
    }
    
    @Test
    fun testSettingsWithDefaults() {
        val settings = Settings()
        
        assertEquals("sub", settings.subOrDub)
        assertEquals("720p", settings.quality)
        assertEquals("/storage/emulated/0/Download/Anime", settings.downloadFolderPath)
        assertEquals(2, settings.maxSimultaneousDownloads)
        assertTrue(settings.allowNotifications)
        assertFalse(settings.isIgnoreFillers)
    }
}