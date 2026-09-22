package com.example.prjmetrix

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import kotlin.jvm.java

class FileRepository(private val context: Context) {
    private val api = NetworkRepository.create(
        context.applicationContext,
        FileApi::class.java
    )

    suspend fun uploadImage(
        uri: Uri,
        removeBackground: Boolean = false
    ): Result<String> {

        return try {

            val inputStream =
                context.contentResolver.openInputStream(uri)
                    ?: return Result.failure(
                        Exception("Could not open image.")
                    )

            val tempFile = File(
                context.cacheDir,
                "upload_image_${System.currentTimeMillis()}.jpg"
            )

            inputStream.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val requestFile =
                tempFile.asRequestBody(
                    "image/*".toMediaTypeOrNull()
                )

            val body =
                MultipartBody.Part.createFormData(
                    "file",
                    tempFile.name,
                    requestFile
                )

            val response = api.uploadImage(file = body,removeBackground = removeBackground)

            tempFile.delete()

            if (response.isSuccessful) {

                val imageUrl =
                    response.body()?.imageUrl

                if (!imageUrl.isNullOrEmpty()) {

                    Result.success(imageUrl)

                } else {

                    Result.failure(
                        Exception("Image URL was not returned.")
                    )
                }

            } else {

                val errorMessage = response.errorBody()?.string()?: "Image upload failed (${response.code()})."
                Result.failure(Exception(errorMessage))
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}