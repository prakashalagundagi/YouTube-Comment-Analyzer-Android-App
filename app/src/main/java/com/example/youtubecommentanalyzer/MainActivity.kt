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

data class QuestionResult(
    val question: String,
    val count: Int
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YouTubeCommentAnalyzerTheme {
                YouTubeCommentAnalyzerScreen()
            }
        }
    }
}

@Composable
fun YouTubeCommentAnalyzerScreen() {
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
                Log.d("MainActivity", "=== ANALYSIS STARTED ====")
                Log.d("MainActivity", "Analyze Comments button clicked!")
                Log.d("MainActivity", "URL: $youtubeUrl")
                if (youtubeUrl.isNotBlank()) {
                    Log.d("MainActivity", "URL is not blank, proceeding...")
                    isLoading = true
                    errorMessage = ""
                    Log.d("MainActivity", "isLoading set to true")
                    scope.launch {
                        Log.d("MainActivity", "Coroutine launched!")
                        val startTime = System.currentTimeMillis()
                        try {
                            Log.d("MainActivity", "Creating API manager...")
                            val apiManager = YouTubeApiManager()
                            Log.d("MainActivity", "Extracting video ID...")
                            val videoId = apiManager.extractVideoId(youtubeUrl)
                            Log.d("MainActivity", "Extracted video ID: $videoId")
                            if (videoId != null) {
                                Log.d("MainActivity", "Video ID is not null, fetching comments...")
                                val comments = apiManager.getVideoComments(videoId)
                                Log.d("MainActivity", "Fetched ${comments.size} comments")
                                Log.d("MainActivity", "Optimizing comments...")
                                val optimizedComments = PerformanceOptimizer().optimizeCommentsList(comments.map { it.text })
                                Log.d("MainActivity", "Creating parser...")
                                val parser = CommentParser()
                                Log.d("MainActivity", "Parsing comments...")
                                val cleanComments = parser.parseComments(optimizedComments)
                                Log.d("MainActivity", "Creating detector...")
                                val detector = QuestionDetector()
                                Log.d("MainActivity", "Detecting questions...")
                                val questions = detector.detectQuestions(cleanComments)
                                Log.d("MainActivity", "Found ${questions.size} questions")
                                Log.d("MainActivity", "Creating counter...")
                                val counter = QuestionCounter()
                                Log.d("MainActivity", "Counting questions...")
                                val results = counter.countQuestions(questions)
                                Log.d("MainActivity", "Counted ${results.size} results")
                                Log.d("MainActivity", "Optimizing results...")
                                val optimizedResults = PerformanceOptimizer().optimizeResults(results)
                                Log.d("MainActivity", "Optimized results count: ${optimizedResults.size}")
                                Log.d("MainActivity", "Updating analysisResults...")
                                analysisResults = optimizedResults
                                Log.d("MainActivity", "analysisResults updated!")
                                
                                val processingTime = PerformanceOptimizer().getProcessingTime(startTime)
                                Log.d("MainActivity", "Analysis completed in ${PerformanceOptimizer().formatProcessingTime(processingTime)}")
                                println("Analysis completed in ${PerformanceOptimizer().formatProcessingTime(processingTime)}")
                            } else {
                                Log.d("MainActivity", "Invalid URL")
                                errorMessage = "Invalid YouTube URL"
                            }
                        } catch (e: Exception) {
                            Log.e("MainActivity", "Error analyzing comments: ${e.message}", e)
                            errorMessage = "Error: ${e.message}"
                        } finally {
                            Log.d("MainActivity", "Setting isLoading to false")
                            isLoading = false
                        }
                    }
                } else {
                    Log.d("MainActivity", "URL is blank")
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

