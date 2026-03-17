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

class TestMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YouTubeCommentAnalyzerTheme {
                TestScreen()
            }
        }
    }
}

@Composable
fun TestScreen() {
    var youtubeUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("App Ready!") }
    
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "YouTube Comment Analyzer",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        OutlinedTextField(
            value = youtubeUrl,
            onValueChange = { youtubeUrl = it },
            label = { Text("YouTube Video URL") },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("https://www.youtube.com/watch?v=...") }
        )
        
        Button(
            onClick = {
                Log.d("TestApp", "Test button clicked! URL: $youtubeUrl")
                if (youtubeUrl.isNotBlank()) {
                    isLoading = true
                    status = "Testing API..."
                    
                    scope.launch {
                        try {
                            val apiManager = YouTubeApiManager()
                            val videoId = apiManager.extractVideoId(youtubeUrl)
                            Log.d("TestApp", "Video ID: $videoId")
                            
                            if (videoId != null) {
                                status = "Video ID found: $videoId"
                                val comments = apiManager.getVideoComments(videoId)
                                status = "Success! Fetched ${comments.size} comments"
                            } else {
                                status = "Invalid YouTube URL"
                            }
                        } catch (e: Exception) {
                            Log.e("TestApp", "Error: ${e.message}", e)
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
                Text("Test API")
            }
        }
        
        Text(
            text = status,
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}
