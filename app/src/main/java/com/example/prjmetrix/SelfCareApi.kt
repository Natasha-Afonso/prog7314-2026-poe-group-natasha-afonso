package com.example.prjmetrix

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST



interface SelfCareApi {
    @POST("api/self-care/quiz")
    suspend fun save(
        @Body request: SelfCareQuizRequest):
            Response<Unit>
}