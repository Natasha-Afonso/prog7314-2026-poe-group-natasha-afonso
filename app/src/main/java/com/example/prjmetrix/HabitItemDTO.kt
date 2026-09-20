package com.example.prjmetrix

data class HabitItemDto(
    val id: Int = 0,
    val userId: String = "",
    val label: String = "",
    val iconKey: String = "default",
    val colorHex: String = "#29B6F6",
    val coinReward: Int = 5,
    val progress: Float = 0f
)
