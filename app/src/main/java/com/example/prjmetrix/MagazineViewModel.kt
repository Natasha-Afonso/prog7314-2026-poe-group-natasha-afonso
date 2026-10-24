package com.example.prjmetrix

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.IsoFields
import java.time.temporal.TemporalAdjusters

data class GoalSummary(val label: String, val daysCompleted: Int)

//An outfit the user actually wore on a day
data class WornOutfit(
    val date: LocalDate,
    val title: String,
    val pieces: List<OutfitPiece>,
    val fromSuggestion: Boolean
)

data class MagazineIssue(
    val weekStart: LocalDate,
    val weekEnd: LocalDate,
    val issueNumber: Int,
    val isCurrentWeek: Boolean,

    //goals
    val goalsLoaded: Boolean,
    val goalsAchieved: Int,
    val perfectDays: Int,
    val coinsEarned: Int,
    val topGoals: List<GoalSummary>,

    //outfits
    val outfitsLoaded: Boolean,
    val worn: List<WornOutfit>,
    val suggestionsWorn: Int,
    val suggestionsSkipped: Int,
    val mostWornPieces: List<Pair<OutfitPiece, Int>>,

    //self care quiz reflection
    val reflection: QuizResult?,
    val quizCount: Int
)

data class MagazineUiState(
    val isLoading: Boolean = true,
    val issue: MagazineIssue? = null,
    val weekOffset: Int = 0
)

class MagazineViewModel(
    private val habitApi: HabitAPI,
    private val repository: StyleRepository,
    private val store: StyleStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(MagazineUiState())
    val uiState: StateFlow<MagazineUiState> = _uiState

    init {
        load(0)
    }

    fun previousWeek() = load(_uiState.value.weekOffset - 1)

    fun nextWeek() {
        if (_uiState.value.weekOffset < 0) load(_uiState.value.weekOffset + 1)
    }

    fun load(weekOffset: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, weekOffset = weekOffset) }
            val issue = buildIssue(weekOffset)

            if (_uiState.value.weekOffset == weekOffset) {
                _uiState.update { it.copy(isLoading = false, issue = issue) }
            }
        }
    }

    private suspend fun buildIssue(weekOffset: Int): MagazineIssue {
        val today = LocalDate.now()
        val weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusWeeks(weekOffset.toLong())
        val weekEnd = weekStart.plusDays(6)
        val days = (0..6).map { weekStart.plusDays(it.toLong()) }.filter { !it.isAfter(today) }

        //Goals, the habits the user completed on each day
        var goalsLoaded = true
        val habitsPerDay: List<List<HabitItemDto>> = coroutineScope {
            days.map { day ->
                async {
                    runCatching {
                        val response = habitApi.getHabits(day.toString())
                        if (response.isSuccessful) response.body().orEmpty() else null
                    }.getOrNull()
                }
            }.awaitAll().map { result ->
                if (result == null) goalsLoaded = false
                result.orEmpty()
            }
        }
        val completed = habitsPerDay.map { list -> list.filter { it.progress >= 1f } }
        val topGoals = completed.flatten()
            .groupBy { it.label }
            .map { (label, list) -> GoalSummary(label, list.size) }
            .sortedByDescending { it.daysCompleted }

        //Outfits
        val outfitsResult = repository.outfits()
        val wearResult = repository.wearLogs(weekStart, weekEnd)
        val scheduleResult = repository.scheduleBetween(weekStart, weekEnd)
        val outfitsLoaded = outfitsResult.isSuccess && wearResult.isSuccess && scheduleResult.isSuccess

        val outfitsById = outfitsResult.getOrDefault(emptyList()).associateBy { it.id }
        val wearLogs = wearResult.getOrDefault(emptyList())

        val worn = mutableListOf<WornOutfit>()
        wearLogs.forEach { log ->
            worn += WornOutfit(
                date = log.date,
                title = if (log.acceptedSuggestion) "Weather pick" else log.outfitName,
                pieces = log.pieces,
                fromSuggestion = log.acceptedSuggestion
            )
        }
        scheduleResult.getOrDefault(emptyMap()).forEach { (dateText, outfitId) ->
            val date = runCatching { LocalDate.parse(dateText.take(10)) }.getOrNull() ?: return@forEach
            val alreadyLogged = wearLogs.any { it.date == date && it.outfitId == outfitId }
            val outfit = outfitsById[outfitId]
            if (!date.isAfter(today) && !alreadyLogged && outfit != null) {
                worn += WornOutfit(
                    date = date,
                    title = outfit.name.removePrefix("$WEATHER_PICK_PREFIX · ").ifBlank { "My outfit" },
                    pieces = outfit.pieces,
                    fromSuggestion = false
                )
            }
        }
        worn.sortBy { it.date }

        val mostWorn = worn.flatMap { it.pieces }
            .groupBy { it.itemId }
            .map { (_, list) -> list.first() to list.size }
            .sortedByDescending { it.second }
            .take(3)

        val skipped = store.suggestions().count { record ->
            record.decision == SuggestionDecision.SKIPPED &&
                    runCatching { LocalDate.parse(record.date) }.getOrNull()?.let { it in weekStart..weekEnd } == true
        }

        //Reflection, the self care check in taken this week
        val quizzes = repository.quizzes(weekStart.minusDays(1), weekEnd.plusDays(1))
            .getOrDefault(emptyList())
            .filter { result ->
                runCatching { LocalDate.parse(result.date) }.getOrNull()?.let { it in weekStart..weekEnd } == true
            }

        return MagazineIssue(
            weekStart = weekStart,
            weekEnd = weekEnd,
            issueNumber = weekStart.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR),
            isCurrentWeek = weekOffset == 0,
            goalsLoaded = goalsLoaded,
            goalsAchieved = completed.sumOf { it.size },
            perfectDays = habitsPerDay.indices.count { i ->
                habitsPerDay[i].isNotEmpty() && completed[i].size == habitsPerDay[i].size
            },
            coinsEarned = completed.flatten().sumOf { it.goldCoins },
            topGoals = topGoals,
            outfitsLoaded = outfitsLoaded,
            worn = worn,
            suggestionsWorn = wearLogs.count { it.acceptedSuggestion },
            suggestionsSkipped = skipped,
            mostWornPieces = mostWorn,
            reflection = quizzes.maxByOrNull { it.id },
            quizCount = quizzes.size
        )
    }
}

class MagazineViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val app = context.applicationContext
        return MagazineViewModel(HabitRepository(app).api, StyleRepository(app), StyleStore(app)) as T
    }
}
