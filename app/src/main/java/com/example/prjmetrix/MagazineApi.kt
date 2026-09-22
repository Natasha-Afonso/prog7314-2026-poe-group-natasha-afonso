package com.example.prjmetrix

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MagazineApi {
    @GET("api/magazine/weekly")
    suspend fun weekly(
        @Query("weekEnd")
        weekEnd: String? = null):
            Response<MagazineDto>

}