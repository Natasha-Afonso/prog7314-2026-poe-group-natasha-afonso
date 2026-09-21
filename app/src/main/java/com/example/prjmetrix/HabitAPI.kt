package com.example.prjmetrix

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

//(Simplified Coding, 2021)
interface HabitAPI {

    @GET("api/Habits")
    suspend fun getHabits(@Query("date") date: String): Response<List<HabitItemDto>>

    @POST("api/Habits")
    suspend fun createHabit(@Body body: HabitItemDto): Response<HabitItemDto>

    @PUT("api/Habits/{id}")
    suspend fun updateHabit(@Path("id") id: Int, @Body body: HabitItemDto): Response<Unit>

    @PUT("api/Habits/{id}/progress")
    suspend fun updateProgress(
        @Path("id") id: Int,
        @Query("progress") progress: Float,
        @Query("date") date: String
    ): Response<Unit>

    @DELETE("api/Habits/{id}")
    suspend fun deleteHabit(@Path("id") id: Int): Response<Unit>
}


/*References
*
* Simplified Coding, 2021. #12 Android Login/Signup with MVVM - user Logout [video online] [source code]. Available at: < https://youtu.be/q_5xrPYRoAg?si=gRbHfP1TazLTqbrY > [Accessed 19 September 2026
*
* */