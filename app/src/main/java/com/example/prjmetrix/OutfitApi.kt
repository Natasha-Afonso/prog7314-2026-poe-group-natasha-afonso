package com.example.prjmetrix



import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query



data class OutfitItemDto(
    val id: Int = 0,
    val name: String = "",
    val category: String = "",
    val imageUrl: String? = null
)

data class OutfitDto(
    val id: Int = 0,
    val name: String = "",
    val createdAt: String = "",
    val items: List<OutfitItemDto> = emptyList()
)

data class CreateOutfitRequest(val name: String, val clothingItemIds: List<Int>)
data class ScheduleOutfitRequest(val outfitId: Int, val date: String)
data class WearOutfitRequest(val outfitId: Int, val acceptedSuggestion: Boolean, val date: String?)

data class CalendarEventDto(
    val date: String = "",
    val title: String = "",
    val type: String = "",
    val outfitId: Int? = null,
    val outfitName: String? = null
)

data class WearLogDto(
    val date: String = "",
    val outfitId: Int = 0,
    val outfitName: String = "",
    val acceptedSuggestion: Boolean = false,
    val items: List<OutfitItemDto> = emptyList()
)

data class SelfCareQuizRequest(val score: Int, val reflection: String)

data class SelfCareQuizDto(
    val id: Int = 0,
    val createdAt: String = "",
    val score: Int = 0,
    val reflection: String = ""
)

interface OutfitApi {
    @GET("api/outfits")
    suspend fun getOutfits(): Response<List<OutfitDto>>

    @POST("api/outfits")
    suspend fun createOutfit(@Body request: CreateOutfitRequest): Response<OutfitDto>

    @DELETE("api/outfits/{id}")
    suspend fun deleteOutfit(@Path("id") id: Int): Response<Unit>

    @POST("api/outfits/schedule")
    suspend fun schedule(@Body request: ScheduleOutfitRequest): Response<Unit>

    @GET("api/outfits/schedule")
    suspend fun getSchedule(
        @Query("from") from: String,
        @Query("to") to: String
    ): Response<List<CalendarEventDto>>

    @DELETE("api/outfits/schedule/{date}")
    suspend fun unschedule(@Path("date") date: String): Response<Unit>

    @POST("api/outfits/wear-log")
    suspend fun logWear(@Body request: WearOutfitRequest): Response<Unit>

    @GET("api/outfits/wear-log")
    suspend fun getWearLogs(
        @Query("from") from: String,
        @Query("to") to: String
    ): Response<List<WearLogDto>>

    @DELETE("api/outfits/wear-log/{date}")
    suspend fun deleteWearLog(@Path("date") date: String): Response<Unit>

    @POST("api/selfcare/quiz")
    suspend fun saveQuiz(@Body request: SelfCareQuizRequest): Response<SelfCareQuizDto>

    @GET("api/selfcare/quiz")
    suspend fun getQuizzes(
        @Query("from") from: String,
        @Query("to") to: String
    ): Response<List<SelfCareQuizDto>>
}
