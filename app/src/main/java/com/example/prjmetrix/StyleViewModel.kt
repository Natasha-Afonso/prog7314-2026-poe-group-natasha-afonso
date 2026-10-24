package com.example.prjmetrix





import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale

data class StyleUiState(
    val outfits: List<Outfit> = emptyList(),                   val schedule: Map<String, Int> = emptyMap(),
    val weather: WeatherSnapshot? = null,
    val weatherError: String? = null,
    val suggestion: SuggestionRecord? = null,
    val suggestionReason: String = "",
    val wallpaperPath: String? = null,
    val holidays: Map<String, List<PublicHoliday>> = emptyMap(),
    val message: String? = null
)

// Shared by the Wardrobe (suggestion, outfits, wallpaper) and the Calendar (outfits, schedule, holidays)
class StyleViewModel(
    private val context: Context,
    private val store: StyleStore,
    private val weatherRepository: WeatherRepository,
    private val repository: StyleRepository
) : ViewModel() {

    private val holidayApi = ExternalApis.holidays

    private val _uiState = MutableStateFlow(StyleUiState())
    val uiState: StateFlow<StyleUiState> = _uiState

    private val loadedHolidayYears = mutableSetOf<Int>()

    private val countryCode: String
        get() = context.resources.configuration.locales[0].country
            .takeIf { it.length == 2 } ?: Locale.getDefault().country.takeIf { it.length == 2 } ?: "ZA"

    private fun say(text: String?) {
        _uiState.update { it.copy(message = text) }
    }

    fun clearMessage() = say(null)

    //Outfits & calendar
    fun loadOutfits() {
        viewModelScope.launch {
            repository.outfits()
                .onSuccess { list -> _uiState.update { it.copy(outfits = list) } }
                .onFailure { say(it.message) }
        }
    }

    fun loadSchedule(from: LocalDate, to: LocalDate) {
        viewModelScope.launch {
            repository.scheduleBetween(from, to)
                .onSuccess { loaded ->
                    _uiState.update { state ->
                        val outside = state.schedule.filterKeys { key ->
                            val d = runCatching { LocalDate.parse(key) }.getOrNull()
                            d == null || d.isBefore(from) || d.isAfter(to)
                        }
                        state.copy(schedule = outside + loaded)
                    }
                }
                .onFailure { say(it.message) }
        }
    }

    fun createOutfit(name: String, pieces: List<OutfitPiece>, date: LocalDate?) {
        viewModelScope.launch {
            repository.createOutfit(name.trim(), pieces.map { it.itemId })
                .onSuccess { created ->
                    _uiState.update { it.copy(outfits = listOf(created) + it.outfits) }
                    if (date != null) planOutfit(created.id, date) else say("Outfit saved")
                }
                .onFailure { say(it.message) }
        }
    }

    fun scheduleOutfit(outfitId: Int, date: LocalDate) {
        viewModelScope.launch { planOutfit(outfitId, date) }
    }

    private suspend fun planOutfit(outfitId: Int, date: LocalDate) {
        repository.schedule(outfitId, date)
            .onSuccess {
                _uiState.update {
                    it.copy(schedule = it.schedule + (date.toString() to outfitId), message = "Outfit planned for $date")
                }
            }
            .onFailure { say(it.message) }
    }

    fun unschedule(date: LocalDate) {
        viewModelScope.launch {
            repository.unschedule(date)
                .onSuccess { _uiState.update { it.copy(schedule = it.schedule - date.toString()) } }
                .onFailure { say(it.message) }
        }
    }

    fun deleteOutfit(id: Int) {
        viewModelScope.launch {
            repository.deleteOutfit(id)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            outfits = state.outfits.filterNot { it.id == id },
                            schedule = state.schedule.filterValues { it != id }
                        )
                    }
                }
                .onFailure { say(it.message) }
        }
    }

    //Daily weather suggestion

    fun refreshSuggestion(clothing: List<ClothingItem>) {
        viewModelScope.launch {
            _uiState.update { it.copy(wallpaperPath = store.wallpaperPath()) }

            val weather = weatherRepository.today().fold(
                onSuccess = { w -> _uiState.update { it.copy(weather = w, weatherError = null) }; w },
                onFailure = { e -> _uiState.update { it.copy(weatherError = e.message) }; _uiState.value.weather }
            ) ?: return@launch

            val today = LocalDate.now()
            val saved = store.suggestions().firstOrNull { it.date == today.toString() }
            val clothingIds = clothing.map { it.id }.toSet()

            val stillValid = saved != null &&
                    saved.pieces.isNotEmpty() &&
                    saved.pieces.all { it.itemId in clothingIds }

            val record = if (stillValid) {
                saved!!
            } else {
                buildSuggestion(clothing, weather, today, saved?.variant ?: 0)
                    .also { store.saveSuggestion(it) }
            }
            _uiState.update {
                it.copy(suggestion = record, suggestionReason = OutfitSuggester.reason(weather))
            }
        }
    }

    private fun buildSuggestion(
        clothing: List<ClothingItem>,
        weather: WeatherSnapshot,
        date: LocalDate,
        variant: Int
    ): SuggestionRecord {
        val items = OutfitSuggester.suggest(clothing, weather, date, variant)
        return SuggestionRecord(
            date = date.toString(),
            pieces = items.map { OutfitPiece(it.id, it.name, it.category, it.imageUrl) },
            weatherSummary = weather.summary,
            variant = variant,
            decision = SuggestionDecision.PENDING
        )
    }

    fun tryAnother(clothing: List<ClothingItem>) {
        val weather = _uiState.value.weather ?: return
        val current = _uiState.value.suggestion
        viewModelScope.launch {

            if (current?.decision == SuggestionDecision.WEARING) repository.deleteWearLog(LocalDate.now())
            val record = buildSuggestion(clothing, weather, LocalDate.now(), (current?.variant ?: 0) + 1)
            store.saveSuggestion(record)
            _uiState.update { it.copy(suggestion = record) }
        }
    }

    //Saves the suggestion as an outfit
    fun wearSuggestion() {
        val current = _uiState.value.suggestion ?: return
        if (current.pieces.isEmpty()) return
        viewModelScope.launch {
            val today = LocalDate.now()
            val ids = current.pieces.map { it.itemId }
            val existing = _uiState.value.outfits.firstOrNull { o ->
                o.isWeatherPick && o.pieces.map { it.itemId }.toSet() == ids.toSet()
            }

            val outfitId: Int = existing?.id ?: repository
                .createOutfit("$WEATHER_PICK_PREFIX · $today", ids)
                .fold(
                    onSuccess = { created ->
                        _uiState.update { it.copy(outfits = listOf(created) + it.outfits) }
                        created.id
                    },
                    onFailure = { say(it.message); return@launch }
                )

            repository.deleteWearLog(today)
            repository.logWear(outfitId, acceptedSuggestion = true, date = today)
                .onSuccess { saveDecision(SuggestionDecision.WEARING) }
                .onFailure { say(it.message) }
        }
    }

    //The user can still see the suggestion and change their mind
    fun skipSuggestion() {
        viewModelScope.launch {
            if (_uiState.value.suggestion?.decision == SuggestionDecision.WEARING) {
                repository.deleteWearLog(LocalDate.now())
            }
            saveDecision(SuggestionDecision.SKIPPED)
        }
    }

    fun resetSuggestion() {
        viewModelScope.launch {
            if (_uiState.value.suggestion?.decision == SuggestionDecision.WEARING) {
                repository.deleteWearLog(LocalDate.now())
            }
            saveDecision(SuggestionDecision.PENDING)
        }
    }

    private suspend fun saveDecision(decision: String) {
        val current = _uiState.value.suggestion ?: return
        val updated = current.copy(decision = decision)
        store.saveSuggestion(updated)
        _uiState.update { it.copy(suggestion = updated) }
    }

    //Wallpaper
    fun setWallpaper(uri: Uri) {
        viewModelScope.launch {
            store.setWallpaper(uri)
                .onSuccess { path -> _uiState.update { it.copy(wallpaperPath = path) } }
                .onFailure { say(it.message ?: "Could not set wallpaper.") }
        }
    }

    fun clearWallpaper() {
        viewModelScope.launch {
            store.clearWallpaper()
            _uiState.update { it.copy(wallpaperPath = null) }
        }
    }

    //National holidays
    fun loadHolidays(year: Int) {
        if (!loadedHolidayYears.add(year)) return
        viewModelScope.launch {
            val country = countryCode
            val cached = store.cachedHolidays(country, year)
            val list = cached ?: runCatching {
                val response = holidayApi.holidays(year, country)
                if (response.isSuccessful) response.body().orEmpty() else null
            }.getOrNull()?.also { store.cacheHolidays(country, year, it) }

            if (list == null) {
                loadedHolidayYears.remove(year)
                return@launch
            }
            _uiState.update { state ->
                state.copy(holidays = state.holidays + list.groupBy { it.date })
            }
        }
    }
}

class StyleViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val app = context.applicationContext
        return StyleViewModel(app, StyleStore(app), WeatherRepository(app), StyleRepository(app)) as T
    }
}
