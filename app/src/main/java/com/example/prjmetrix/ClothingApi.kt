package com.example.prjmetrix

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path



interface ClothingApi {
    @GET("api/clothing")
    suspend fun getClothing(): Response<List<ClothingItem>>

    @GET("api/clothing/wishlist")
    suspend fun getWishlist(): Response<List<ClothingItem>>

    @POST("api/clothing")
    suspend fun addClothing(
        @Body item: ClothingItem
    ): Response<ClothingItem>

    @POST("api/clothing/wishlist")
    suspend fun addWishlist(
        @Body item: ClothingItem
    ): Response<ClothingItem>

    @DELETE("api/clothing/{id}")
    suspend fun deleteClothing(
        @Path("id") id: Int
    ): Response<Unit>
}