package com.example.youtubecommentanalyzer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import com.example.youtubecommentanalyzer.ui.theme.YouTubeCommentAnalyzerTheme

class ApiTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YouTubeCommentAnalyzerTheme {
                ApiTestScreen()
            }
        }
    }
}

@Composable
fun ApiTestScreen() {
    var youtubeUrl by remember { mutableStateOf("https://youtu.be/eG_uKvgSd7A?si=a0QcAOspfM3Z4wBA") }
    var isLoading by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Ready to test API") }
    
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "API Test Only",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        OutlinedTextField(
            value = youtubeUrl,
            onValueChange = { youtubeUrl = it },
            label = { Text("YouTube Video URL") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Button(
            onClick = {
                Log.d("ApiTest", "=== API TEST STARTED ===")
                Log.d("ApiTest", "Button clicked! URL: $youtubeUrl")
                if (youtubeUrl.isNotBlank()) {
                    isLoading = true
                    status = "Testing API..."
                    
                    scope.launch {
                        try {
                            Log.d("ApiTest", "Creating API manager...")
                            val apiManager = YouTubeApiManager()
                            
                            Log.d("ApiTest", "Extracting video ID...")
                            val videoId = apiManager.extractVideoId(youtubeUrl)
                            Log.d("ApiTest", "Video ID: $videoId")
                            
                            if (videoId != null) {
                                status = "Video ID found: $videoId"
                                Log.d("ApiTest", "Fetching comments...")
                                val comments = apiManager.getVideoComments(videoId)
                                Log.d("ApiTest", "Comments fetched: ${comments.size}")
                                status = "SUCCESS! Fetched ${comments.size} comments"
                            } else {
                                Log.d("ApiTest", "Invalid URL")
                                status = "Invalid YouTube URL"
                            }
                        } catch (e: Exception) {
                            Log.e("ApiTest", "Error: ${e.message}", e)
                            status = "Error: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                } else {
                    status = "Please enter a URL"
                }
            },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Test API Only")
            }
        }
        
        Text(
            text = status,
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}
