package com.example.dreamweaver.data.model

/**
 * Domain-level model used throughout the UI layer.
 * `mood` is stored as a simple key ("happy", "scary", "weird", "peaceful", "sad")
 * and mapped to an emoji purely in the UI layer.
 */
data class Dream(
    val id: Int = 0,
    val title: String,
    val description: String,
    val mood: String,
    val isLucid: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

fun moodEmoji(mood: String): String = when (mood) {
    "happy" -> "😄"
    "scary" -> "😨"
    "weird" -> "🌀"
    "peaceful" -> "🌙"
    "sad" -> "😢"
    else -> "✨"
}

val availableMoods = listOf("happy", "scary", "weird", "peaceful", "sad")
