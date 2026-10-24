package com.example.prjmetrix

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class FileRepository(private val context: Context) {
    private val api = NetworkRepository.create(
        context,
        FileApi::class.java
    )



    // Uploads whatever image the user picked
    suspend fun uploadImage(uri: Uri): Result<String> {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return Result.failure(Exception("Could not open image."))

            val tempFile = File(context.cacheDir, "upload_${System.nanoTime()}.jpg")
            inputStream.use { input ->
                tempFile.outputStream().use { output -> input.copyTo(output) }
            }
            uploadImageFile(tempFile, "image/jpeg", deleteAfter = true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Uploads an image file we already have
    suspend fun uploadImageFile(
        file: File,
        mimeType: String,
        deleteAfter: Boolean = true
    ): Result<String> {
        return try {
            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            val response = api.uploadImage(body)

            if (deleteAfter) file.delete()

            if (response.isSuccessful) {
                val imageUrl = response.body()?.imageUrl
                if (!imageUrl.isNullOrEmpty()) {
                    Result.success(imageUrl)
                } else {
                    Result.failure(Exception("Image URL was not returned."))
                }
            } else {
                Result.failure(Exception("Image upload failed."))
            }
        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}