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

    suspend fun uploadImage(
        uri: Uri
    ): Result<String> {

        return try {

            val inputStream =
                context.contentResolver.openInputStream(uri)
                    ?: return Result.failure(
                        Exception("Could not open image.")
                    )

            val tempFile = File(
                context.cacheDir,
                "upload_image.jpg"
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

            val response = api.uploadImage(body)

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

                Result.failure(
                    Exception("Image upload failed.")
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}