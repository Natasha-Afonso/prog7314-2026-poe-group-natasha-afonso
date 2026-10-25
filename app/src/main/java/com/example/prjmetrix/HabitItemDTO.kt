package com.example.prjmetrix

data class HabitItemDto(
    val id: Int = 0,
    val label: String = "",
    val iconKey: String = "default",
    val colorHex: String = "#29B6F6",
    val goldCoins: Int = 5,
    val targetCount: Int = 1,
    val progress: Float = 0f
)
