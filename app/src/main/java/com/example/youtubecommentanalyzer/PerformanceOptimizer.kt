package com.example.youtubecommentanalyzer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PerformanceOptimizer {
    
    companion object {
        const val MAX_COMMENTS_TO_ANALYZE = 300
        const val MAX_RESULTS_TO_DISPLAY = 50
        const val MIN_QUESTION_LENGTH = 5
        const val MAX_QUESTION_LENGTH = 200
    }
    
    suspend fun <T> performBackgroundOperation(operation: suspend () -> T): T {
        return withContext(Dispatchers.IO) {
            operation()
        }
    }
    
    fun optimizeCommentsList(comments: List<String>): List<String> {
        return comments
            .take(MAX_COMMENTS_TO_ANALYZE)
            .filter { it.isNotBlank() }
            .filter { it.length >= MIN_QUESTION_LENGTH }
            .filter { it.length <= MAX_QUESTION_LENGTH }
    }
    
    fun optimizeResults(results: List<QuestionResult>): List<QuestionResult> {
        return results
            .take(MAX_RESULTS_TO_DISPLAY)
            .filter { it.count > 1 } // Only show questions asked by multiple viewers
    }
    
    fun getProcessingTime(startTime: Long): Long {
        return System.currentTimeMillis() - startTime
    }
    
    fun formatProcessingTime(milliseconds: Long): String {
        val seconds = milliseconds / 1000.0
        return when {
            seconds < 1 -> "${milliseconds}ms"
            seconds < 60 -> "%.1fs".format(seconds)
            else -> "%.1f min".format(seconds / 60)
        }
    }
}
