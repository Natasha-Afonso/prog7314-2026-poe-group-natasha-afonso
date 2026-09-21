package com.example.prjmetrix

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException

class SettingsRepository(private val context: Context) {

    // Creates the Settings API using the same
    // NetworkRepository used by AuthRepository.
    private val api: SettingsApi by lazy {
        NetworkRepository.create(
            context,
            SettingsApi::class.java
        )
    }

    suspend fun getSettings(): Result<UserSettings> =
        call {
            api.getSettings()
        }


    suspend fun updateNotifications(
        enabled: Boolean
    ): Result<UserSettings> =
        call {
            api.updateNotifications(enabled)
        }

    suspend fun updateLanguage(
        language: String
    ): Result<UserSettings> =
        call {
            api.updateLanguage(language)
        }


    suspend fun updateTheme(
        theme: String
    ): Result<UserSettings> =
        call {
            api.updateTheme(theme)
        }


    suspend fun updateAccessibility(
        fontSize: String,
        highContrast: Boolean
    ): Result<UserSettings> {

        val request = AccessibilitySettingsRequest(
            fontSize = fontSize,
            highContrast = highContrast
        )

        return call {
            api.updateAccessibility(request)
        }
    }

    suspend fun submitBug(
        title: String,
        description: String
    ): Result<Boolean> {

        val request = BugReportRequest(
            title = title,
            description = description
        )

        return try {

            val response = api.submitBug(request)

            if (response.isSuccessful) {

                Result.success(true)

            } else {

                Result.failure(
                    Exception(
                        readError(response)
                    )
                )
            }

        } catch (e: IOException) {

            Log.e(
                "SettingsRepository",
                "Network error submitting bug report",
                e
            )

            Result.failure(
                Exception(
                    "Cannot reach MeTrix. Check your connection and try again."
                )
            )

        } catch (e: Exception) {

            Log.e(
                "SettingsRepository",
                "Unexpected error submitting bug report",
                e
            )

            Result.failure(
                Exception(
                    "Something went wrong: ${e.message}"
                )
            )
        }
    }

    suspend fun submitRating(
        rating: Int,
        comment: String?
    ): Result<Boolean> {

        val request = RatingRequest(
            rating = rating,
            comment = comment
        )

        return try {

            val response = api.submitRating(request)

            if (response.isSuccessful) {

                Result.success(true)

            } else {

                Result.failure(
                    Exception(
                        readError(response)
                    )
                )
            }

        } catch (e: IOException) {

            Log.e(
                "SettingsRepository",
                "Network error submitting rating",
                e
            )

            Result.failure(
                Exception(
                    "Cannot reach MeTrix. Check your connection and try again."
                )
            )

        } catch (e: Exception) {

            Log.e(
                "SettingsRepository",
                "Unexpected error submitting rating",
                e
            )

            Result.failure(
                Exception(
                    "Something went wrong: ${e.message}"
                )
            )
        }
    }

    private suspend fun call(
        block: suspend () -> Response<UserSettings>
    ): Result<UserSettings> {

        return try {

            val response = block()

            if (response.isSuccessful) {

                val body = response.body()

                if (body != null) {

                    Result.success(body)

                } else {

                    Result.failure(
                        Exception(
                            "Empty response from the server."
                        )
                    )
                }

            } else {

                Result.failure(
                    Exception(
                        readError(response)
                    )
                )
            }

        } catch (e: IOException) {

            Log.e(
                "SettingsRepository",
                "Network error calling MeTrix API",
                e
            )

            Result.failure(
                Exception(
                    "Cannot reach MeTrix. Check your connection and try again."
                )
            )

        } catch (e: Exception) {

            Log.e(
                "SettingsRepository",
                "Unexpected error calling MeTrix API",
                e
            )

            Result.failure(
                Exception(
                    "Something went wrong: ${e.message}"
                )
            )
        }
    }


    private fun readError(
        response: Response<*>
    ): String {

        val raw = response
            .errorBody()
            ?.string()
            .orEmpty()

        return try {

            Gson()
                .fromJson(
                    raw,
                    ApiError::class.java
                )
                ?.message
                ?: defaultMessage(response.code())

        } catch (e: Exception) {

            defaultMessage(response.code())
        }
    }

    private fun defaultMessage(
        code: Int
    ): String {

        return when (code) {

            400 -> "Invalid settings request."

            401 -> "Your session has expired. Please log in again."

            403 -> "You do not have permission to change these settings."

            404 -> "Settings could not be found."

            500 -> "The server encountered an error."

            else -> "Something went wrong. Please try again."
        }
    }
}
