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
import androidx.compose.ui.graphics.Color

class CommentViewerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YouTubeCommentAnalyzerTheme {
                CommentViewerScreen()
            }
        }
    }
}

@Composable
fun CommentViewerScreen() {
    var youtubeUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf<List<YouTubeApiManager.YouTubeComment>>(emptyList()) }
    var analysisResults by remember { mutableStateOf<List<QuestionResult>>(emptyList()) }
    var showComments by remember { mutableStateOf(true) }
    
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
            text = "YouTube Comment Viewer & Analyzer",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "See actual comments and analyzed questions",
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
                Log.d("CommentViewer", "=== ANALYSIS STARTED ====")
                Log.d("CommentViewer", "Button clicked! URL: $youtubeUrl")
                if (youtubeUrl.isNotBlank()) {
                    isLoading = true
                    errorMessage = ""
                    scope.launch {
                        try {
                            Log.d("CommentViewer", "Creating API manager...")
                            val apiManager = YouTubeApiManager()
                            Log.d("CommentViewer", "Extracting video ID...")
                            val videoId = apiManager.extractVideoId(youtubeUrl)
                            Log.d("CommentViewer", "Extracted video ID: $videoId")
                            if (videoId != null) {
                                Log.d("CommentViewer", "Fetching comments...")
                                val fetchedComments = apiManager.getVideoComments(videoId)
                                Log.d("CommentViewer", "Fetched ${fetchedComments.size} comments")
                                comments = fetchedComments
                                
                                // Simple analysis
                                val parser = CommentParser()
                                val cleanComments = parser.parseComments(fetchedComments.map { it.text })
                                val detector = QuestionDetector()
                                val questions = detector.detectQuestions(cleanComments)
                                val counter = QuestionCounter()
                                val results = counter.countQuestions(questions)
                                analysisResults = results
                                
                                Log.d("CommentViewer", "Analysis complete: ${results.size} questions found")
                                
                            } else {
                                errorMessage = "Invalid YouTube URL"
                            }
                        } catch (e: Exception) {
                            Log.e("CommentViewer", "Error: ${e.message}", e)
                            errorMessage = "Error: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                } else {
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
                Text("Load Comments")
            }
        }
        
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }
        
        // Toggle buttons
        if (comments.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { showComments = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showComments) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Raw Comments (${comments.size})")
                }
                
                Button(
                    onClick = { showComments = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (!showComments) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Questions (${analysisResults.size})")
                }
            }
        }
        
        // Show content based on toggle
        if (showComments && comments.isNotEmpty()) {
            Text(
                text = "Raw Comments (${comments.size})",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            
            comments.forEachIndexed { index, comment ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${index + 1}. ${comment.author}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (comment.isVerified) {
                                Text(
                                    text = "✓",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Blue,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                        Text(
                            text = comment.text,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "${comment.likeCount} likes • ${comment.replyCount} replies",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
        
        if (!showComments && analysisResults.isNotEmpty()) {
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
