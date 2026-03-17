package com.example.youtubecommentanalyzer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

class SimpleMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YouTubeCommentAnalyzerTheme {
                SimpleAnalyzerScreen()
            }
        }
    }
}

@Composable
fun SimpleAnalyzerScreen() {
    var youtubeUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var analysisResults by remember { mutableStateOf<List<QuestionResult>>(emptyList()) }
    
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "YouTube Comment Analyzer",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Enter a YouTube video URL to analyze viewer questions",
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
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
                Log.d("SimpleMainActivity", "=== ANALYSIS STARTED ====")
                Log.d("SimpleMainActivity", "Analyze Comments button clicked!")
                Log.d("SimpleMainActivity", "URL: $youtubeUrl")
                if (youtubeUrl.isNotBlank()) {
                    Log.d("SimpleMainActivity", "URL is not blank, proceeding...")
                    isLoading = true
                    errorMessage = ""
                    Log.d("SimpleMainActivity", "isLoading set to true")
                    scope.launch {
                        Log.d("SimpleMainActivity", "Coroutine launched!")
                        try {
                            Log.d("SimpleMainActivity", "Creating API manager...")
                            val apiManager = YouTubeApiManager()
                            Log.d("SimpleMainActivity", "Extracting video ID...")
                            val videoId = apiManager.extractVideoId(youtubeUrl)
                            Log.d("SimpleMainActivity", "Extracted video ID: $videoId")
                            if (videoId != null) {
                                Log.d("SimpleMainActivity", "Video ID is not null, fetching comments...")
                                val comments = apiManager.getVideoComments(videoId)
                                Log.d("SimpleMainActivity", "Fetched ${comments.size} comments")
                                
                                // Simple analysis - just create some dummy results for testing
                                Log.d("SimpleMainActivity", "Creating simple results...")
                                val simpleResults = listOf(
                                    QuestionResult("What is this video about?", comments.size / 10),
                                    QuestionResult("How does this work?", comments.size / 15),
                                    QuestionResult("Where can I learn more?", comments.size / 20)
                                )
                                Log.d("SimpleMainActivity", "Simple results created: ${simpleResults.size}")
                                Log.d("SimpleMainActivity", "Updating analysisResults...")
                                analysisResults = simpleResults
                                Log.d("SimpleMainActivity", "analysisResults updated!")
                                
                            } else {
                                Log.d("SimpleMainActivity", "Invalid URL")
                                errorMessage = "Invalid YouTube URL"
                            }
                        } catch (e: Exception) {
                            Log.e("SimpleMainActivity", "Error analyzing comments: ${e.message}", e)
                            errorMessage = "Error: ${e.message}"
                        } finally {
                            Log.d("SimpleMainActivity", "Setting isLoading to false")
                            isLoading = false
                        }
                    }
                } else {
                    Log.d("SimpleMainActivity", "URL is blank")
                    errorMessage = "Please enter a YouTube URL"
                }
            },
            enabled = !isLoading && youtubeUrl.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Analyze Comments")
            }
        }
        
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }
        
        if (analysisResults.isNotEmpty()) {
            Text(
                text = "Top Viewer Questions",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            
            analysisResults.forEachIndexed { index, result ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "${index + 1}. ${result.question}",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "(${result.count} viewers)",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
