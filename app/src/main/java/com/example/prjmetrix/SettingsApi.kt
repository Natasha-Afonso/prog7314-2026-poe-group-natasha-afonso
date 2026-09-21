package com.example.prjmetrix

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

data class UserSettings(
    val id: Int = 0,
    val userId: String = "",
    val language: String = "English",
    val theme: String = "System",
    val pushNotifications: Boolean = true,
    val fontSize: String = "Medium",
    val highContrast: Boolean = false
)

data class AccessibilitySettingsRequest(
    val fontSize: String,
    val highContrast: Boolean
)

data class BugReportRequest(
    val title: String,
    val description: String
)

data class RatingRequest(
    val rating: Int,
    val comment: String?
)

interface SettingsApi {
    // Get the current user's settings
    @GET("api/settings")
    suspend fun getSettings(): Response<UserSettings>

    // Change language
    @PUT("api/settings/language")
    suspend fun updateLanguage(
        @Body language: String
    ): Response<UserSettings>

    // Change theme
    @PUT("api/settings/theme")
    suspend fun updateTheme(
        @Body theme: String
    ): Response<UserSettings>

    // Turn push notifications on or off
    @PUT("api/settings/notifications")
    suspend fun updateNotifications(
        @Body enabled: Boolean
    ): Response<UserSettings>

    // Update accessibility settings
    @PUT("api/settings/accessibility")
    suspend fun updateAccessibility(
        @Body settings: AccessibilitySettingsRequest
    ): Response<UserSettings>

    // Submit a bug report
    @POST("api/settings/bugs")
    suspend fun submitBug(
        @Body request: BugReportRequest
    ): Response<Map<String, String>>

    // Submit an app rating
    @POST("api/settings/rating")
    suspend fun submitRating(
        @Body request: RatingRequest
    ): Response<Map<String, String>>
}