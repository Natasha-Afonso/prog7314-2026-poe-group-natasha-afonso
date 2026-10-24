package com.example.prjmetrix

data class Outfit(
    val id: Int,
    val name: String,
    val pieces: List<OutfitPiece>
)

// Outfits made by tapping "Wear it" on the daily suggestion are saved

const val WEATHER_PICK_PREFIX = "Weather pick"

val Outfit.isWeatherPick: Boolean
    get() = name.startsWith(WEATHER_PICK_PREFIX)

data class QuizAnswer(
    val question: String = "",
    val answer: String = "",
    val emoji: String = ""
)

data class QuizResult(
    val id: Int = 0,
    val date: String = "",
    val score: Int = 0,
    val answers: List<QuizAnswer> = emptyList()
)
