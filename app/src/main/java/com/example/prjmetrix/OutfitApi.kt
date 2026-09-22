package com.example.prjmetrix

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class CreateOutfitRequest(val name: String, val clothingItemIds: List<Int>)
data class ScheduleOutfitRequest(val outfitId: Int, val date: String)
data class WearOutfitRequest(
    val outfitId: Int,
    val acceptedSuggestion: Boolean,
    val date: String? = null
)

interface OutfitApi {
    @GET("api/outfits")
    suspend fun getOutfits(): Response<List<OutfitDto>>

    @POST("api/outfits")
    suspend fun create(@Body request: CreateOutfitRequest): Response<OutfitDto>

    @POST("api/outfits/schedule")
    suspend fun schedule(@Body request: ScheduleOutfitRequest): Response<Unit>

    @GET("api/outfits/schedule")
    suspend fun schedule(
        @Query("from") from: String,
        @Query("to") to: String
    ): Response<List<CalendarEventDto>>

    @POST("api/outfits/wear-log")
    suspend fun wearLog(@Body request: WearOutfitRequest): Response<Unit>
}
