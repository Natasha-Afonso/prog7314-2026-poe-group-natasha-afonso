package com.example.prjmetrix

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

data class ProfileDto(
    val displayName: String = "",
    val personality: String = "",
    val birthday: String? = null,          // "2003-09-15"
    val bodyKey: String = "base_body_f",
    val hairKey: String = "hair_1",
    val eyesKey: String = "eye1c",
    val lipsKey: String = "lipstick6_grin"
)

interface ProfileApi {
    @GET("api/Profile")
    suspend fun getProfile(): Response<ProfileDto>

    @PUT("api/Profile")
    suspend fun saveProfile(@Body body: ProfileDto): Response<ProfileDto>
}