package com.example.prjmetrix

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class ImageUploadResponse(
    val message: String,
    val imageUrl: String
)
interface FileApi {
    @Multipart
    @POST("api/File/upload-image")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part
    ): Response<ImageUploadResponse>

    @Multipart
    @POST("api/clothing/remove-background")
    suspend fun removeBackground(
        @Part file: MultipartBody.Part
    ): Response<ImageUploadResponse>
}