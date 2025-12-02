package com.sleepy.senpwaia.utils

import org.jsoup.Jsoup
import java.math.BigInteger
import kotlin.math.pow

object PaheDecryptor {
    
    private const val CHAR_MAP = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ+/"
    private const val CHAR_MAP_BASE = 10
    private val CHAR_MAP_DIGITS = CHAR_MAP.take(CHAR_MAP_BASE)
    
    /**
     * Get the character code based on the algorithm used in AnimePahe
     */
    fun getCharCode(content: String, s1: Int): Int {
        var j = 0
        content.reversed().forEachIndexed { index, c ->
            val digit = if (c.isDigit()) c.digitToInt() else 0
            j += digit * s1.toDouble().pow(index).toInt()
        }
        
        var k = ""
        var tempJ = j
        while (tempJ > 0) {
            k = CHAR_MAP_DIGITS[tempJ % CHAR_MAP_BASE] + k
            tempJ = (tempJ - (tempJ % CHAR_MAP_BASE)) / CHAR_MAP_BASE
        }
        
        return if (k.isNotEmpty()) k.toInt() else 0
    }
    
    /**
     * Decrypt the post form data using the algorithm from the original Python version
     */
    fun decryptPostForm(fullKey: String, key: String, v1: Int, v2: Int): String {
        var result = ""
        var i = 0
        
        while (i < fullKey.length) {
            var s = ""
            while (i < fullKey.length && fullKey[i] != key[v2 % key.length]) {
                s += fullKey[i]
                i++
            }
            
            // Replace characters in the key with their index
            for (idx in key.indices) {
                s = s.replace(key[idx].toString(), idx.toString())
            }
            
            val charCode = getCharCode(s, v2) - v1
            result += charCode.toChar()
            i++
        }
        
        return result
    }
    
    /**
     * Extract and decrypt download link from the intermediate page
     */
    suspend fun extractDownloadLink(kwikPageLink: String, okHttpClient: okhttp3.OkHttpClient): String {
        // Get the intermediate page containing the encrypted data
        val request = okhttp3.Request.Builder()
            .url(kwikPageLink)
            .addHeader("Referer", kwikPageLink)
            .build()
        
        val response = okHttpClient.newCall(request).execute()
        val pageContent = response.body?.string() ?: throw Exception("Could not fetch page content")
        
        // Extract the encrypted parameters using regex
        val paramRegex = Regex("""\("(\w+)",(\d+),"(\w+)",(\d+),(\d+),(\d+)\)""")
        val matchResult = paramRegex.find(pageContent)
        
        if (matchResult != null) {
            val (fullKey, _, keyStr, v1Str, v2Str, _) = matchResult.destructured
            val v1 = v1Str.toIntOrNull() ?: 0
            val v2 = v2Str.toIntOrNull() ?: 0
            
            // Decrypt the form data
            val decryptedForm = decryptPostForm(fullKey, keyStr, v1, v2)
            
            // Parse the decrypted form to extract action URL and token
            val doc = Jsoup.parse(decryptedForm)
            val form = doc.selectFirst("form") ?: throw Exception("No form found in decrypted content")
            val actionUrl = form.attr("action")
            val tokenInput = form.selectFirst("input") ?: throw Exception("No input found in form")
            val tokenValue = tokenInput.attr("value")
            
            // Make POST request to get the final download URL
            val postBody = okhttp3.FormBody.Builder()
                .add("_token", tokenValue)
                .build()
                
            val postRequest = okhttp3.Request.Builder()
                .url(actionUrl)
                .post(postBody)
                .addHeader("Referer", kwikPageLink)
                .build()
            
            val postResponse = okHttpClient.newCall(postRequest).execute()
            
            // Get the final download URL from the redirect location
            return postResponse.header("Location") ?: throw Exception("No redirect location found")
        } else {
            throw Exception("Could not find parameters in page")
        }
    }
}