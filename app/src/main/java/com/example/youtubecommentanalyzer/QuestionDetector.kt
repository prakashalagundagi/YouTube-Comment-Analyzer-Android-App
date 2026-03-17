package com.example.youtubecommentanalyzer

class QuestionDetector {
    
    private val questionWords = setOf(
        "how", "why", "what", "when", "where", "who", "which", "can", "could", 
        "would", "should", "is", "are", "was", "were", "do", "does", "did", 
        "will", "would", "shall", "should", "may", "might", "must", "have", "has"
    )
    
    private val questionIndicators = setOf(
        "?", "??", "???", "pls", "please", "help", "explain", "clarify", "tell me"
    )
    
    fun detectQuestions(comments: List<String>): List<String> {
        return comments.filter { comment ->
            isQuestion(comment) && isValidQuestion(comment)
        }
    }
    
    private fun isQuestion(comment: String): Boolean {
        val lowerComment = comment.lowercase()
        
        // Check for question mark
        if (lowerComment.contains("?")) return true
        
        // Check for question words at the beginning
        val words = lowerComment.split(" ")
        if (words.isNotEmpty() && questionWords.contains(words[0])) return true
        
        // Check for question indicators
        if (questionIndicators.any { lowerComment.contains(it) }) return true
        
        // Check for question patterns
        val questionPatterns = listOf(
            Regex("\\b(how|why|what|when|where|who|which)\\b"),
            Regex("\\b(can|could|would|should)\\s+(you|we|they|someone)\\b"),
            Regex("\\b(is|are|was|were)\\s+(there|this|that|it)\\b"),
            Regex("\\b(do|does|did)\\s+(you|we|they|someone)\\b"),
            Regex("\\b(tell|explain|show|help)\\s+me\\b")
        )
        
        return questionPatterns.any { it.containsMatchIn(lowerComment) }
    }
    
    private fun isValidQuestion(comment: String): Boolean {
        // Filter out very short or very long comments
        if (comment.length < 5 || comment.length > 200) return false
        
        // Filter out comments that are just single words with question mark
        val words = comment.trim().split(" ")
        if (words.size == 1 && words[0].endsWith("?")) return false
        
        // Filter out common non-question patterns
        val nonQuestionPatterns = listOf(
            Regex("^(thank|thanks|awesome|great|nice|cool|good|love|like)"),
            Regex("^(lol|haha|wow|omg|wtf)"),
            Regex("^(first|early|here)"),
            Regex("^(subscribe|like|share)"),
            Regex("^(check out|watch this|see this)")
        )
        
        return !nonQuestionPatterns.any { it.containsMatchIn(comment.lowercase()) }
    }
}
