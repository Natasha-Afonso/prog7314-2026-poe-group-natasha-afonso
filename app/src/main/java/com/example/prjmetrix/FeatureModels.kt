package com.example.prjmetrix


data class OutfitItemDto(
    val id: Int,
    val name: String,
    val category: String,
    val imageUrl: String?

)
data class OutfitDto(
    val id: Int,
    val name: String,
    val createdAt: String,
    val items: List<OutfitItemDto>
)

data class CalendarEventDto(
    val date: String,
    val title: String,
    val type: String,
    val outfitId: Int?,
    val outfitName: String?
)

data class WeatherDto(
    val date: String,
    val maxTemperature: Double,
    val minTemperature: Double,
    val precipitationProbability: Double,
    val weatherCode: Int
)

data class MagazineDto(
    val weekStart: String,
    val weekEnd: String,
    val goalsAchieved: Int,
    val journalEntries: Int,
    val outfitsWorn: Int,
    val suggestionsAccepted: Int,
    val suggestionsRejected: Int,
    val favouriteOutfit: String?,
    val latestMood: String?,
    val selfCareReflection: String?
)

data class SelfCareQuizRequest(
    val score: Int,
    val reflection: String
)
