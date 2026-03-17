package com.example.youtubecommentanalyzer

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class YouTubeApiManager {
    private val API_KEY = "AIzaSyC_AhrL3GjwtX9qZ1R8KvM0u6v5opV55bs" // IMPORTANT: Replace this with your actual YouTube Data API v3 key
    private val BASE_URL = "https://www.googleapis.com/youtube/v3/"
    private val client = OkHttpClient()
    private val gson = Gson()
    
    fun extractVideoId(url: String): String? {
        return try {
            when {
                url.contains("youtube.com/watch?v=") -> {
                    url.substringAfter("v=").substringBefore("&")
                }
                url.contains("youtu.be/") -> {
                    url.substringAfter("youtu.be/").substringBefore("?")
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    data class YouTubeComment(
    val text: String,
    val author: String,
    val authorImageUrl: String? = null,
    val likeCount: Int = 0,
    val replyCount: Int = 0,
    val timestamp: String,
    val isVerified: Boolean = false
)

data class VideoInfo(
    val videoId: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val channelTitle: String,
    val publishedAt: String,
    val viewCount: Long,
    val likeCount: Long,
    val commentCount: Long
)

suspend fun getVideoInfo(videoId: String): VideoInfo? {
    return withContext(Dispatchers.IO) {
        Log.d("YouTubeApiManager", "Getting video info for: $videoId")
        try {
            val url = StringBuilder("${BASE_URL}videos?")
                .append("part=snippet,statistics")
                .append("&id=$videoId")
                .append("&key=$API_KEY")
                .toString()
            
            Log.d("YouTubeApiManager", "Video info URL: $url")
            
            val request = Request.Builder()
                .url(url)
                .get()
                .build()
            
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            
            Log.d("YouTubeApiManager", "Video info response: ${responseBody?.take(200)}...")
            
            if (!responseBody.isNullOrBlank()) {
                val jsonResponse = gson.fromJson(responseBody, com.google.gson.JsonObject::class.java)
                val items = jsonResponse.getAsJsonArray("items")
                
                if (items != null && items.size() > 0) {
                    val video = items.get(0).asJsonObject
                    val snippet = video.getAsJsonObject("snippet")
                    val statistics = video.getAsJsonObject("statistics")
                    
                    val title = snippet.get("title")?.asString ?: "Unknown Title"
                    val description = snippet.get("description")?.asString ?: ""
                    val thumbnailUrl = snippet.getAsJsonObject("thumbnails")
                        ?.getAsJsonObject("high")
                        ?.get("url")?.asString ?: ""
                    val channelTitle = snippet.get("channelTitle")?.asString ?: ""
                    val publishedAt = snippet.get("publishedAt")?.asString ?: ""
                    val viewCount = statistics.get("viewCount")?.asLong ?: 0L
                    val likeCount = statistics.get("likeCount")?.asLong ?: 0L
                    val commentCount = statistics.get("commentCount")?.asLong ?: 0L
                    
                    Log.d("YouTubeApiManager", "Video info retrieved: $title")
                    
                    VideoInfo(
                        videoId = videoId,
                        title = title,
                        description = description,
                        thumbnailUrl = thumbnailUrl,
                        channelTitle = channelTitle,
                        publishedAt = publishedAt,
                        viewCount = viewCount,
                        likeCount = likeCount,
                        commentCount = commentCount
                    )
                } else {
                    Log.e("YouTubeApiManager", "No video found for ID: $videoId")
                    null
                }
            } else {
                Log.e("YouTubeApiManager", "Empty video info response")
                null
            }
        } catch (e: Exception) {
            Log.e("YouTubeApiManager", "Error getting video info: ${e.message}", e)
            null
        }
    }
}

suspend fun getVideoComments(videoId: String): List<YouTubeComment> {
        return withContext(Dispatchers.IO) {
            Log.d("YouTubeApiManager", "Starting getVideoComments for video: $videoId")
            val youtubeComments = mutableListOf<YouTubeComment>()
            var nextPageToken: String? = null
            var totalComments = 0
            val maxComments = 300 // Performance optimization: limit to first 300 comments
            
            Log.d("YouTubeApiManager", "Starting to fetch comments for video: $videoId")
            
            do {
                try {
                    val url = StringBuilder("${BASE_URL}commentThreads?")
                        .append("part=snippet")
                        .append("&videoId=$videoId")
                        .append("&maxResults=${if (totalComments + 50 > maxComments) maxComments - totalComments else 50}")
                        .append("&textFormat=plainText")
                        .append("&key=$API_KEY")
                    
                    if (nextPageToken != null) {
                        url.append("&pageToken=$nextPageToken")
                    }
                    
                    val urlString = url.toString()
                    Log.d("YouTubeApiManager", "Making request to: $urlString")
                    
                    val request = Request.Builder()
                        .url(urlString)
                        .build()
                    
                    Log.d("YouTubeApiManager", "Executing HTTP request...")
                    val response: Response = client.newCall(request).execute()
                    Log.d("YouTubeApiManager", "Response code: ${response.code}, successful: ${response.isSuccessful}")
                    
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string()
                        Log.d("YouTubeApiManager", "Response body length: ${responseBody?.length}")
                        if (!responseBody.isNullOrBlank()) {
                            val jsonResponse = gson.fromJson(responseBody, JsonObject::class.java)
                            Log.d("YouTubeApiManager", "Parsed JSON response")
                            
                            // Check for API errors
                            if (jsonResponse.has("error")) {
                                val error = jsonResponse.getAsJsonObject("error")
                                val errorMessage = error.get("message")?.asString ?: "Unknown API error"
                                val errorCode = error.get("code")?.asInt ?: -1
                                Log.e("YouTubeApiManager", "API Error $errorCode: $errorMessage")
                                throw Exception("API Error $errorCode: $errorMessage")
                            }
                            
                            val items = jsonResponse.getAsJsonArray("items")
                            Log.d("YouTubeApiManager", "Items array size: ${items?.size()}")
                            if (items != null) {
                                items.forEach { item ->
                                    val itemObj = item.asJsonObject
                                    val snippet = itemObj.getAsJsonObject("snippet")
                                    val topLevelComment = snippet.getAsJsonObject("topLevelComment")
                                    val commentSnippet = topLevelComment.getAsJsonObject("snippet")
                                    val authorDisplayName = commentSnippet.get("authorDisplayName")?.asString ?: "Anonymous"
                                    val authorImageUrl = commentSnippet.get("authorProfileImageUrl")?.asString
                                    val commentText = commentSnippet.get("textDisplay")?.asString ?: ""
                                    val likeCount = commentSnippet.get("likeCount")?.asInt ?: 0
                                    val replyCount = commentSnippet.get("totalReplyCount")?.asInt ?: 0
                                    val publishedAt = commentSnippet.get("publishedAt")?.asString ?: ""
                                    val isVerified = commentSnippet.getAsJsonObject("authorChannelId") != null
                                    
                                    if (commentText.isNotBlank()) {
                                        val youtubeComment = YouTubeComment(
                                            text = commentText.trim(),
                                            author = authorDisplayName,
                                            authorImageUrl = authorImageUrl,
                                            likeCount = likeCount,
                                            replyCount = replyCount,
                                            timestamp = publishedAt,
                                            isVerified = isVerified
                                        )
                                        youtubeComments.add(youtubeComment)
                                        totalComments++
                                        Log.d("YouTubeApiManager", "Added comment from $authorDisplayName: ${commentText.take(50)}...")
                                    }
                                }
                            }
                            
                            nextPageToken = jsonResponse.get("nextPageToken")?.asString
                            Log.d("YouTubeApiManager", "Next page token: $nextPageToken")
                        } else {
                            Log.e("YouTubeApiManager", "Empty response from YouTube API")
                            throw Exception("Empty response from YouTube API")
                        }
                    } else {
                        Log.e("YouTubeApiManager", "HTTP Error ${response.code}: ${response.message}")
                        throw Exception("HTTP Error ${response.code}: ${response.message}")
                    }
                    
                } catch (e: Exception) {
                    Log.e("YouTubeApiManager", "Error fetching comments: ${e.message}", e)
                    Log.e("YouTubeApiManager", "Error type: ${e::class.java.simpleName}")
                    Log.e("YouTubeApiManager", "Error cause: ${e.cause?.message}")
                    throw Exception("Failed to fetch comments: ${e.message ?: "Unknown error - ${e::class.java.simpleName}"}")
                }
            } while (nextPageToken != null && totalComments < maxComments)
            
            Log.d("YouTubeApiManager", "Returning ${youtubeComments.size} comments")
            youtubeComments
        }
    }
}
