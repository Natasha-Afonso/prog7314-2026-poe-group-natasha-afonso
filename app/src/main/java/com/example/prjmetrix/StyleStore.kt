package com.example.prjmetrix



import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.util.UUID


//outfits, the daily suggestion decisions, quiz results, the wallpaper and  holidays.

private val Context.styleDataStore by preferencesDataStore(name = "metrix_style")

//One clothing item as it was when it was added to an outfit
data class OutfitPiece(
    val itemId: Int = 0,
    val name: String = "",
    val category: String = "",
    val imageUrl: String? = null
)


object SuggestionDecision {
    const val PENDING = "PENDING"
    const val WEARING = "WEARING"
    const val SKIPPED = "SKIPPED"
}

//What the app suggested
data class SuggestionRecord(
    val date: String = "",
    val pieces: List<OutfitPiece> = emptyList(),
    val weatherSummary: String = "",
    val variant: Int = 0,
    val decision: String = SuggestionDecision.PENDING
)


class StyleStore(context: Context) {

    private val appContext = context.applicationContext
    private val token = Token(appContext)
    private val gson = Gson()

    private suspend fun uid(): String = token.getUid().orEmpty().ifBlank { "guest" }

    private suspend fun key(name: String): Preferences.Key<String> =
        stringPreferencesKey("${name}_${uid()}")

    private inline fun <reified T> decode(json: String?): List<T> {
        if (json.isNullOrBlank()) return emptyList()
        return runCatching {
            gson.fromJson<List<T>>(
                json,
                TypeToken.getParameterized(List::class.java, T::class.java).type
            )
        }.getOrNull()?.filterNotNull() ?: emptyList()
    }

    private suspend inline fun <reified T> read(name: String): List<T> {
        val k = key(name)
        return decode(appContext.styleDataStore.data.first()[k])
    }

    private suspend inline fun <reified T> update(name: String, crossinline transform: (List<T>) -> List<T>) {
        val k = key(name)
        appContext.styleDataStore.edit { prefs ->
            val current = decode<T>(prefs[k])
            prefs[k] = gson.toJson(transform(current))
        }
    }


    //Daily suggestion decisions
    suspend fun suggestions(): List<SuggestionRecord> = read("suggestions")

    suspend fun saveSuggestion(record: SuggestionRecord) =
        update<SuggestionRecord>("suggestions") { list ->
            (list.filterNot { it.date == record.date } + record).sortedBy { it.date }.takeLast(120)
        }


    //Holidays, cached so the calendar works offline
    suspend fun cachedHolidays(country: String, year: Int): List<PublicHoliday>? {
        val k = stringPreferencesKey("holidays_${country}_$year")
        val json = appContext.styleDataStore.data.first()[k] ?: return null
        return decode<PublicHoliday>(json)
    }

    suspend fun cacheHolidays(country: String, year: Int, holidays: List<PublicHoliday>) {
        val k = stringPreferencesKey("holidays_${country}_$year")
        appContext.styleDataStore.edit { it[k] = gson.toJson(holidays) }
    }

    //Wardrobe wallpaper
    suspend fun wallpaperPath(): String? {
        val path = appContext.styleDataStore.data.first()[key("wallpaper")]
        return path?.takeIf { File(it).exists() }
    }

    suspend fun setWallpaper(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            val bitmap = ImageUtils.decodeScaled(appContext, uri, 1600)
                ?: error("Could not read that image.")
            val file = File(appContext.filesDir, "wardrobe_wallpaper_${uid()}_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 88, it) }
            bitmap.recycle()

            val k = key("wallpaper")
            val old = appContext.styleDataStore.data.first()[k]
            appContext.styleDataStore.edit { it[k] = file.absolutePath }
            old?.let { File(it).delete() }
            file.absolutePath
        }
    }

    suspend fun clearWallpaper() {
        val k = key("wallpaper")
        val old = appContext.styleDataStore.data.first()[k]
        appContext.styleDataStore.edit { it.remove(k) }
        withContext(Dispatchers.IO) { old?.let { File(it).delete() } }
    }
}
