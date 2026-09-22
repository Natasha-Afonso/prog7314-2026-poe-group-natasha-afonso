package com.example.prjmetrix

import android.content.Context
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import kotlin.collections.emptyList

private interface CalendarApi {
    @GET("api/calendar")
    suspend fun month(
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<List<CalendarEventDto>>
}

class CalendarRepository(context: Context) {
    private val api = NetworkRepository.create(context.applicationContext, CalendarApi::class.java)

    suspend fun month(year: Int, month: Int): Result<List<CalendarEventDto>> =
        api.month(year, month).let {
            if (it.isSuccessful) Result.success(
                it.body() ?: emptyList()
            ) else Result.failure(Exception("Could not load calendar."))
        }
}
