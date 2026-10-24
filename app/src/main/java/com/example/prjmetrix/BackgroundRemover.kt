package com.example.prjmetrix



import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

// Sends a clothing photo to remove.bg and returns a transparent PNG
class BackgroundRemover(private val context: Context) {

    private val api = NetworkRepository.create(context.applicationContext, FileApi::class.java)

    suspend fun removeBackground(uri: Uri): Result<String> = withContext(Dispatchers.IO) {
        var upload: File? = null
        try {
            upload = ImageUtils.toScaledJpegFile(context, uri)
                ?: return@withContext Result.failure(Exception("Could not read that image."))

            val part = MultipartBody.Part.createFormData(
                "file",
                upload.name,
                upload.asRequestBody("image/jpeg".toMediaType())
            )
            val response = api.removeBackground(part)

            if (!response.isSuccessful) {
                val reason = when (response.code()) {
                    401 -> "Please log in again to use the background remover."
                    402 -> "Background remover has run out of credits."
                    429 -> "Background remover is busy, try again shortly."
                    503 -> "Background remover isn't set up on the server."
                    else -> "Background remover failed (${response.code()})."
                }
                return@withContext Result.failure(Exception(reason))
            }

            val url = response.body()?.imageUrl
            if (url.isNullOrBlank()) {
                Result.failure(Exception("Background remover returned nothing."))
            } else {
                Result.success(url)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Background remover could not be reached."))
        } finally {
            upload?.delete()
        }
    }
}
