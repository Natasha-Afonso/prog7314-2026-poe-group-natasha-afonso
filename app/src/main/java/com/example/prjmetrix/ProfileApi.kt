package com.example.prjmetrix

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT



interface ProfileApi {
    @GET("api/Profile")
    suspend fun getProfile(): Response<ProfileItemDTO>

    @PUT("api/Profile")
    suspend fun saveProfile(@Body body: ProfileItemDTO): Response<ProfileItemDTO>
}