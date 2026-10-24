package com.example.prjmetrix



import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

// Outfits, the outfit calendar, wear logs and the self care quiz
class StyleRepository(context: Context) {

    private val api = NetworkRepository.create(context.applicationContext, OutfitApi::class.java)
    private val gson = Gson()

    private suspend fun <T> call(errorMessage: String, block: suspend () -> Response<T>): Result<T> {
        return try {
            val response = block()
            if (response.isSuccessful) {
                @Suppress("UNCHECKED_CAST")
                Result.success(response.body() as T)
            } else {
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("$errorMessage Check your connection."))
        }
    }

    private fun OutfitDto.toOutfit() = Outfit(
        id = id,
        name = name,
        pieces = items.map { OutfitPiece(it.id, it.name, it.category, it.imageUrl) }
    )

    private fun List<OutfitItemDto>.toPieces() =
        map { OutfitPiece(it.id, it.name, it.category, it.imageUrl) }

    //Outfits
    suspend fun outfits(): Result<List<Outfit>> =
        call("Could not load outfits.") { api.getOutfits() }.map { list -> list.map { it.toOutfit() } }

    suspend fun createOutfit(name: String, clothingItemIds: List<Int>): Result<Outfit> =
        call("Could not save the outfit.") { api.createOutfit(CreateOutfitRequest(name, clothingItemIds)) }
            .map { it.toOutfit() }

    suspend fun deleteOutfit(id: Int): Result<Unit> =
        call("Could not delete the outfit.") { api.deleteOutfit(id) }

    //Calendar
    suspend fun schedule(outfitId: Int, date: LocalDate): Result<Unit> =
        call("Could not plan the outfit.") { api.schedule(ScheduleOutfitRequest(outfitId, date.toString())) }

    suspend fun unschedule(date: LocalDate): Result<Unit> =
        call("Could not clear that day.") { api.unschedule(date.toString()) }


    suspend fun scheduleBetween(from: LocalDate, to: LocalDate): Result<Map<String, Int>> =
        call("Could not load planned outfits.") { api.getSchedule(from.toString(), to.toString()) }
            .map { list ->
                list.filter { it.outfitId != null }.associate { it.date to it.outfitId!! }
            }

    //Wear log
    suspend fun logWear(outfitId: Int, acceptedSuggestion: Boolean, date: LocalDate): Result<Unit> =
        call("Could not save what you wore.") {
            api.logWear(WearOutfitRequest(outfitId, acceptedSuggestion, date.toString()))
        }

    suspend fun deleteWearLog(date: LocalDate): Result<Unit> =
        call("Could not update what you wore.") { api.deleteWearLog(date.toString()) }

    suspend fun wearLogs(from: LocalDate, to: LocalDate): Result<List<WearEntry>> =
        call("Could not load what you wore.") { api.getWearLogs(from.toString(), to.toString()) }
            .map { list ->
                list.mapNotNull { dto ->
                    val date = runCatching { LocalDate.parse(dto.date.take(10)) }.getOrNull()
                        ?: return@mapNotNull null
                    WearEntry(date, dto.outfitId, dto.outfitName, dto.acceptedSuggestion, dto.items.toPieces())
                }
            }

    //Self care quiz
    suspend fun saveQuiz(score: Int, answers: List<QuizAnswer>): Result<Unit> =
        call("Could not save your check-in.") {
            api.saveQuiz(SelfCareQuizRequest(score, gson.toJson(answers)))
        }.map { }

    suspend fun quizzes(from: LocalDate, to: LocalDate): Result<List<QuizResult>> =
        call("Could not load your check-ins.") { api.getQuizzes(from.toString(), to.toString()) }
            .map { list ->
                list.mapNotNull { dto ->
                    val date = serverTimeToLocalDate(dto.createdAt) ?: return@mapNotNull null
                    val answers = runCatching {
                        gson.fromJson<List<QuizAnswer>>(
                            dto.reflection,
                            TypeToken.getParameterized(List::class.java, QuizAnswer::class.java).type
                        )
                    }.getOrNull().orEmpty()
                    QuizResult(dto.id, date.toString(), dto.score, answers)
                }
            }


    private fun serverTimeToLocalDate(value: String): LocalDate? = runCatching {
        LocalDateTime.parse(value.removeSuffix("Z").take(19))
            .atOffset(ZoneOffset.UTC)
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDate()
    }.getOrNull()
}

// One thing the user wore on a day
data class WearEntry(
    val date: LocalDate,
    val outfitId: Int,
    val outfitName: String,
    val acceptedSuggestion: Boolean,
    val pieces: List<OutfitPiece>
)
