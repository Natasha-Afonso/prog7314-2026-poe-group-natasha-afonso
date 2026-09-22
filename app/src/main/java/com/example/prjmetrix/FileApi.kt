package com.example.prjmetrix

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

data class ImageUploadResponse(
    val message: String,
    val imageUrl: String
)
interface FileApi {
    @Multipart
    @POST("api/File/upload-image")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Query("removeBackground") removeBackground: Boolean = false
    ): Response<ImageUploadResponse>
}