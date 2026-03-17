package com.example.youtubecommentanalyzer

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.foundation.clickable
import com.example.youtubecommentanalyzer.ui.theme.YouTubeCommentAnalyzerTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import androidx.compose.ui.draw.clip

// Import existing classes
import com.example.youtubecommentanalyzer.YouTubeApiManager
import com.example.youtubecommentanalyzer.CommentParser
import com.example.youtubecommentanalyzer.QuestionDetector
import com.example.youtubecommentanalyzer.QuestionCounter
import com.example.youtubecommentanalyzer.PerformanceOptimizer

data class SearchHistory(
    val url: String,
    val timestamp: Long,
    val videoTitle: String? = null
)

data class CommentWithStats(
    val text: String,
    val author: String? = null,
    val likeCount: Int = 0,
    val timestamp: String? = null,
    val replyCount: Int = 0,
    val authorImageUrl: String? = null,
    val isVerified: Boolean = false
)

data class SimilarComment(
    val text: String,
    val count: Int,
    val comments: List<String>
)

class AdvancedAnalyzerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YouTubeCommentAnalyzerTheme {
                AdvancedAnalyzerScreen()
            }
        }
    }
}

@Composable
fun AdvancedAnalyzerScreen() {
    var youtubeUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var comments by remember { mutableStateOf<List<CommentWithStats>>(emptyList()) }
    var analysisResults by remember { mutableStateOf<List<QuestionResult>>(emptyList()) }
    var similarComments by remember { mutableStateOf<List<SimilarComment>>(emptyList()) }
    var searchHistory by remember { mutableStateOf<List<SearchHistory>>(emptyList()) }
    var currentView by remember { mutableStateOf("comments") } // comments, questions, similar, recent, liked
    var videoInfo by remember { mutableStateOf<YouTubeApiManager.VideoInfo?>(null) }
    
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with Icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "YouTube Comment Analyzer",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Advanced YouTube Analyzer",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Text(
            text = "Analyze comments with multiple views and features",
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        // URL Input with History
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = youtubeUrl,
                onValueChange = { youtubeUrl = it },
                label = { Text("YouTube Video URL") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://www.youtube.com/watch?v=...") }
            )
            
            // Search History
            if (searchHistory.isNotEmpty()) {
                Text(
                    text = "Recent Searches:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                
                LazyColumn(
                    modifier = Modifier.height(120.dp)
                ) {
                    items(searchHistory.takeLast(5)) { history ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { youtubeUrl = history.url }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = history.url.take(50) + if (history.url.length > 50) "..." else "",
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "${(System.currentTimeMillis() - history.timestamp) / 60000}m ago",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
        
        // Action Buttons
        Button(
            onClick = {
                if (youtubeUrl.isNotBlank()) {
                    // Add to history
                    val newHistory = SearchHistory(
                        url = youtubeUrl,
                        timestamp = System.currentTimeMillis()
                    )
                    searchHistory = (searchHistory + newHistory).takeLast(10)
                    
                    isLoading = true
                    errorMessage = ""
                    scope.launch {
                        try {
                            val apiManager = YouTubeApiManager()
                            val videoId = apiManager.extractVideoId(youtubeUrl)
                            if (videoId != null) {
                                Log.d("CommentViewer", "Fetching video info...")
                                val fetchedVideoInfo = apiManager.getVideoInfo(videoId)
                                videoInfo = fetchedVideoInfo
                                
                                Log.d("CommentViewer", "Fetching comments...")
                                val fetchedComments = apiManager.getVideoComments(videoId)
                                Log.d("CommentViewer", "Fetched ${fetchedComments.size} comments")
                                
                                // Convert to CommentWithStats with real user data
                                comments = fetchedComments.map { comment ->
                                    CommentWithStats(
                                        text = comment.text,
                                        author = comment.author,
                                        authorImageUrl = comment.authorImageUrl,
                                        likeCount = comment.likeCount,
                                        replyCount = comment.replyCount,
                                        timestamp = comment.timestamp,
                                        isVerified = comment.isVerified
                                    )
                                }
                                
                                // Analysis
                                val parser = CommentParser()
                                val cleanComments = parser.parseComments(fetchedComments.map { it.text })
                                val detector = QuestionDetector()
                                val questions = detector.detectQuestions(cleanComments)
                                val counter = QuestionCounter()
                                analysisResults = counter.countQuestions(questions)
                                
                                // Find similar comments
                                similarComments = findSimilarComments(fetchedComments.map { it.text })
                                
                            } else {
                                errorMessage = "Invalid YouTube URL"
                            }
                        } catch (e: Exception) {
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
        
        // Video Info Display
        if (videoInfo != null) {
            VideoInfoCard(videoInfo = videoInfo!!)
        }
        
        // View Toggle Buttons
        if (comments.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val views = listOf(
                    "comments" to "Comments (${comments.size})",
                    "questions" to "Questions (${analysisResults.size})",
                    "similar" to "Similar (${similarComments.size})",
                    "recent" to "Recent (${comments.take(10).size})",
                    "liked" to "Top Liked (${comments.size})"
                )
                
                views.forEach { (key, label) ->
                    FilterChip(
                        onClick = { currentView = key },
                        label = { Text(label, fontSize = 10.sp) },
                        selected = currentView == key,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
        
        // Content based on current view
        when (currentView) {
            "comments" -> CommentsView(comments)
            "questions" -> QuestionsView(analysisResults)
            "similar" -> SimilarCommentsView(similarComments)
            "recent" -> RecentCommentsView(comments.take(10))
            "liked" -> LikedCommentsView(comments.sortedByDescending { it.likeCount })
        }
    }
}

@Composable
fun CommentsView(comments: List<CommentWithStats>) {
    Text(
        text = "All Comments",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp)
    )
    
    LazyColumn(
        modifier = Modifier.height(400.dp)
    ) {
        items(comments) { comment ->
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Author info with profile image
                            if (comment.authorImageUrl != null) {
                                // You would need to load image from URL here
                                // For now, show a placeholder
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(end = 8.dp)
                                ) {
                                    Text(
                                        text = comment.author?.take(2)?.uppercase() ?: "U",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = comment.author ?: "Anonymous",
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
                            }
                        }
                        
                        // Stats row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "Likes",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${comment.likeCount}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Replies",
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Text(
                                text = "${comment.replyCount}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                    Text(
                        text = comment.text,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = comment.timestamp ?: "",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionsView(questions: List<QuestionResult>) {
    Text(
        text = "Top Questions",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp)
    )
    
    LazyColumn(
        modifier = Modifier.height(400.dp)
    ) {
        items(questions) { question ->
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
                        text = question.question,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${question.count} viewers asked",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SimilarCommentsView(similarComments: List<SimilarComment>) {
    Text(
        text = "Similar Comments",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp)
    )
    
    LazyColumn(
        modifier = Modifier.height(400.dp)
    ) {
        items(similarComments) { similar ->
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
                        text = "${similar.count} similar comments:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = similar.text,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    similar.comments.take(2).forEach { comment ->
                        Text(
                            text = "• ${comment.take(60)}...",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentCommentsView(recentComments: List<CommentWithStats>) {
    Text(
        text = "Recent Comments",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp)
    )
    
    LazyColumn(
        modifier = Modifier.height(400.dp)
    ) {
        items(recentComments) { comment ->
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
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Recent",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = comment.author ?: "Anonymous",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = comment.text,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LikedCommentsView(likedComments: List<CommentWithStats>) {
    Text(
        text = "Most Liked Comments",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp)
    )
    
    LazyColumn(
        modifier = Modifier.height(400.dp)
    ) {
        items(likedComments) { comment ->
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
                            text = comment.author ?: "Anonymous",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "Likes",
                                modifier = Modifier.size(16.dp),
                                tint = Color.Red
                            )
                            Text(
                                text = "${comment.likeCount}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                            )
                        }
                    }
                    Text(
                        text = comment.text,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

// Helper function to find similar comments
fun findSimilarComments(comments: List<String>): List<SimilarComment> {
    val similarGroups = mutableMapOf<String, MutableList<String>>()
    
    comments.forEach { comment ->
        val key = comment.lowercase().take(20)
        if (similarGroups.containsKey(key)) {
            similarGroups[key]?.add(comment)
        } else {
            similarGroups[key] = mutableListOf(comment)
        }
    }
    
    return similarGroups
        .filter { it.value.size > 1 }
        .map { (key, similarList) ->
            SimilarComment(
                text = similarList.first(),
                count = similarList.size,
                comments = similarList
            )
        }
        .sortedByDescending { it.count }
        .take(5)
}

@Composable
fun VideoInfoCard(videoInfo: YouTubeApiManager.VideoInfo) {
    val context = LocalContext.current
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(videoInfo.thumbnailUrl)
            .crossfade(true)
            .transformations(RoundedCornersTransformation(8f))
            .build()
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with Thumbnail and Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Thumbnail
                Image(
                    painter = painter,
                    contentDescription = "Video Thumbnail",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                
                // Video Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = videoInfo.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = videoInfo.channelTitle,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Stats
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Views",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formatNumber(videoInfo.viewCount),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "Likes",
                                modifier = Modifier.size(16.dp),
                                tint = Color.Red
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formatNumber(videoInfo.likeCount),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Comments",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formatNumber(videoInfo.commentCount),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // Description (truncated)
            if (videoInfo.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = videoInfo.description.take(150) + if (videoInfo.description.length > 150) "..." else "",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Helper function to format numbers (e.g., 1000 -> 1K, 1000000 -> 1M)
fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}
