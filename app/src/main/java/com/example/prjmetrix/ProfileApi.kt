package com.example.prjmetrix

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT



// Retrofit turns this interface into real HTTP calls
interface ProfileApi {

    // if 200 that means profile found, 404 means this user has no saved profile yet
    @GET("api/Profile")
    suspend fun getProfile(): Response<ProfileItemDTO>

    // creates the profile the first time, updates it afterwards
    @PUT("api/Profile")
    suspend fun saveProfile(@Body body: ProfileItemDTO): Response<ProfileItemDTO>
}