package com.example.youtubecommentanalyzer

class CommentParser {
    
    fun parseComments(rawComments: List<String>): List<String> {
        return rawComments
            .map { comment -> cleanComment(comment) }
            .filter { comment -> comment.isNotBlank() && comment.length > 3 }
            .map { comment -> normalizeText(comment) }
    }
    
    private fun cleanComment(comment: String): String {
        return comment
            .removeHTMLTags()
            .removeEmojis()
            .removeExtraWhitespace()
            .trim()
    }
    
    private fun normalizeText(text: String): String {
        return text
            .lowercase()
            .replace(Regex("[^a-zA-Z0-9?.!\\s]"), "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }
}

private fun String.removeHTMLTags(): String {
    return this.replace(Regex("<[^>]*>"), "")
}

private fun String.removeEmojis(): String {
    return this.replace(Regex("[\\p{So}\\p{Sk}\\p{Sm}\\p{Sc}\\p{Cn}]"), "")
}

private fun String.removeExtraWhitespace(): String {
    return this.replace(Regex("\\s+"), " ")
}
