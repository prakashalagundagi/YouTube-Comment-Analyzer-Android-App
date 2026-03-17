package com.example.youtubecommentanalyzer

class QuestionCounter {
    
    fun countQuestions(questions: List<String>): List<QuestionResult> {
        val normalizedQuestions = questions.map { normalizeQuestion(it) }
        val questionCounts = mutableMapOf<String, Int>()
        
        // Count occurrences of each normalized question
        normalizedQuestions.forEach { question ->
            questionCounts[question] = questionCounts.getOrDefault(question, 0) + 1
        }
        
        // Convert to QuestionResult objects and sort by frequency
        return questionCounts.entries
            .map { (question, count) ->
                QuestionResult(
                    question = question,
                    count = count
                )
            }
            .sortedByDescending { it.count }
    }
    
    private fun normalizeQuestion(question: String): String {
        return question
            .lowercase()
            .trim()
            .replace(Regex("\\s+"), " ") // Remove extra whitespace
            .replace(Regex("[^a-zA-Z0-9?.!\\s]"), "") // Remove special characters except punctuation
            .replace(Regex("\\?+$"), "?") // Normalize multiple question marks to single
            .replace(Regex("!+$"), "!") // Normalize multiple exclamation marks to single
            .replace(Regex("\\s+([?.!])"), "$1") // Remove space before punctuation
            .trim()
            .let { normalized ->
                // Further normalization for similar questions
                when {
                    normalized.startsWith("how to ") && normalized.contains("?") -> {
                        normalized.replace(Regex("\\s*\\?\\s*$"), "?")
                    }
                    normalized.startsWith("what is ") && normalized.contains("?") -> {
                        normalized.replace(Regex("\\s*\\?\\s*$"), "?")
                    }
                    normalized.startsWith("why ") && normalized.contains("?") -> {
                        normalized.replace(Regex("\\s*\\?\\s*$"), "?")
                    }
                    else -> normalized
                }
            }
    }
    
    fun groupSimilarQuestions(questions: List<String>): Map<String, List<String>> {
        val groups = mutableMapOf<String, MutableList<String>>()
        
        questions.forEach { question ->
            val normalized = normalizeQuestion(question)
            val groupKey = findGroupKey(normalized)
            
            if (!groups.containsKey(groupKey)) {
                groups[groupKey] = mutableListOf()
            }
            groups[groupKey]?.add(question)
        }
        
        return groups
    }
    
    private fun findGroupKey(normalizedQuestion: String): String {
        // Group questions by their core topic
        return when {
            normalizedQuestion.startsWith("how to") -> "how_to"
            normalizedQuestion.startsWith("what is") -> "what_is"
            normalizedQuestion.startsWith("why") -> "why"
            normalizedQuestion.startsWith("when") -> "when"
            normalizedQuestion.startsWith("where") -> "where"
            normalizedQuestion.startsWith("can you") -> "can_you"
            normalizedQuestion.startsWith("do you") -> "do_you"
            else -> "other"
        }
    }
}
