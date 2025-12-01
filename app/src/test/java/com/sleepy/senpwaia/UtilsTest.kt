package com.sleepy.senpwaia.utils

import org.junit.Test
import org.junit.Assert.*

class FileUtilsTest {
    
    @Test
    fun testGetEpisodeFileName() {
        val fileName = FileUtils.getEpisodeFileName("Demon Slayer", 5)
        assertEquals("Demon Slayer_E05.mp4", fileName)
    }
    
    @Test
    fun testGetEpisodeFileNameWithDoubleDigit() {
        val fileName = FileUtils.getEpisodeFileName("Attack on Titan", 15)
        assertEquals("Attack on Titan_E15.mp4", fileName)
    }
    
    @Test
    fun testGetEpisodeFileNameWithSingleDigit() {
        val fileName = FileUtils.getEpisodeFileName("Jujutsu Kaisen", 1)
        assertEquals("Jujutsu Kaisen_E01.mp4", fileName)
    }
}

class PaheDecryptorTest {
    
    @Test
    fun testGetCharCode() {
        // This test is based on the algorithm, but the exact implementation might differ
        // depending on the specific parameters used in the real application
        val result = PaheDecryptor.getCharCode("123", 10)
        // This would need to match the actual algorithm implementation
        // For now, this is a placeholder test
        assertTrue(result >= 0) // Should not crash
    }
}