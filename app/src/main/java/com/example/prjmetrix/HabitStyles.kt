package com.example.prjmetrix

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.vector.ImageVector
import kotlin.math.roundToInt

// Icons and colours available when creating a habit.
data class HabitIconOption(val key: String, val icon: ImageVector)

val HabitIcons = listOf(
    HabitIconOption("check", Icons.Default.Check),
    HabitIconOption("water", Icons.Default.WaterDrop),
    HabitIconOption("brush", Icons.Default.Brush),
    HabitIconOption("cardio", Icons.Default.DirectionsRun),
    HabitIconOption("walk", Icons.Default.DirectionsWalk),
    HabitIconOption("fitness", Icons.Default.FitnessCenter),
    HabitIconOption("book", Icons.Default.Book),
    HabitIconOption("study", Icons.Default.School),
    HabitIconOption("sleep", Icons.Default.Bedtime),
    HabitIconOption("food", Icons.Default.Restaurant),
    HabitIconOption("meds", Icons.Default.Medication),
    HabitIconOption("clean", Icons.Default.CleaningServices),
    HabitIconOption("meditate", Icons.Default.SelfImprovement),
    HabitIconOption("music", Icons.Default.MusicNote),
    HabitIconOption("heart", Icons.Default.Favorite),
    HabitIconOption("star", Icons.Default.Star)
)

val HabitColors = listOf(
    "#29B6F6", "#26A69A", "#66BB6A", "#9CCC65",
    "#FFCA28", "#FFA726", "#FF7043", "#EF5350",
    "#EC407A", "#AB47BC", "#7E57C2", "#5C6BC0"
)

// Uses the tick icon if the key is not found.
fun iconForKey(key: String): ImageVector =
    HabitIcons.firstOrNull { it.key == key }?.icon ?: Icons.Default.Check

// Coins and goals used by the reward system.
const val HALF_GOAL_COINS = 5      // Coins for reaching 50% progress.
const val GOAL_COINS = 10           // Coins for completing a goal.
const val MYSTERY_GIFT_GOALS = 3    // Goals needed for a mystery gift.
const val MAX_DAILY_TARGET = 20

// Calculates completed taps from the saved progress.
fun doneCount(progress: Float, target: Int): Int =
    (progress * target).roundToInt().coerceIn(0, target)

// Calculates progress after one more tap.
fun nextProgress(progress: Float, target: Int): Float {
    val safeTarget = target.coerceAtLeast(1)
    return (doneCount(progress, safeTarget) + 1).coerceAtMost(safeTarget) / safeTarget.toFloat()
}
