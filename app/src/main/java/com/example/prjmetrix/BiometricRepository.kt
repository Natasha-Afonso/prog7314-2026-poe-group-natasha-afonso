package com.example.prjmetrix

import android.content.Context
import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException

//(Simplified Coding, 2021)
class BiometricRepository(context: Context) {

    //calls the object NetworkRepository to create the retrofit builder
    private val api = NetworkRepository.create(context, BiometricApi::class.java)

    //only the on/off switch is saved on the phone (not a secret), so the lock screen can show straight away when the app opens
    //the PIN is never saved on the phone, only the hash in the database
    private val prefs = context.applicationContext.getSharedPreferences("metrix_biometric", Context.MODE_PRIVATE)

    var cachedBiometricEnabled: Boolean
        get() = prefs.getBoolean("biometric_enabled", false)
        set(value) = prefs.edit().putBoolean("biometric_enabled", value).apply()

    //clears the saved switch when the user logs out
    fun clearCache() = prefs.edit().clear().apply()

    //(Alpasian, 2023)
    //functions handle the link calling the biometric API which returns the data class objects
    suspend fun getStatus(): Result<BiometricStatusResponse> =
        callOne { api.getStatus() }

    suspend fun savePin(newPin: String): Result<BiometricResponse> =
        callOne { api.savePin(SavePinRequest(newPin)) }

    suspend fun verifyPin(pin: String): Result<BiometricResponse> =
        callOne { api.verifyPin(VerifyPinRequest(pin)) }

    suspend fun setBiometric(enabled: Boolean): Result<BiometricStatusResponse> =
        callOne { api.setBiometric(BiometricRequest(enabled)) }

    //(Sharma, 2024)
    //functions deals with error messages related to BiometricRepository failing to get a JSON response objects
    private suspend fun <T> callOne(block: suspend () -> Response<T>): Result<T> {
        return try {
            val response = block()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response from the server."))
            } else {
                Result.failure(Exception(readError(response)))
            }
            //error messages if errors occur logged
        } catch (e: IOException) {
            android.util.Log.e("BiometricRepository", "Network error calling MeTrix API", e)
            Result.failure(Exception("Cannot reach MeTrix. Check your connection and try again."))
        } catch (e: Exception) {
            android.util.Log.e("BiometricRepository", "Unexpected error calling MeTrix API", e)
            Result.failure(Exception("Something went wrong: ${e.message}"))
        }
    }

    //errors gets passed to the ApiError data class's objects with specific errors
    private fun readError(response: Response<*>): String {
        val raw = response.errorBody()?.string().orEmpty()
        return try {
            Gson().fromJson(raw, ApiError::class.java)?.message
                ?: defaultMessage(response.code())
        } catch (e: Exception) {
            defaultMessage(response.code()) //errors like 400 or 401 are sent by the defaultMessage function
        }
    }

    //errors like 400 or 401 are sent by the defaultMessage function
    private fun defaultMessage(code: Int) = when (code) {
        400, 401 -> "Could not update biometric settings"
        else -> "Something went wrong. Please try again."
    }
}

/*References
*
* Alpasian, E., 2023. DiaryApp. (Version 10.0) [Source code]. Available at: < https://github.com/erenalpaslan/DiaryApp > [Accessed 19 September 2026]
* Sharma, S., 2024. Mood-Journal. (Version 10.0) [Source code]. Available at: < https://github.com/SakshamSharma2026/Mood-Journal > [Accessed 21 September 2026]
* Simplified Coding, 2021. #12 Android Login/Signup with MVVM - user Logout [video online] [source code]. Available at: < https://youtu.be/q_5xrPYRoAg?si=gRbHfP1TazLTqbrY > [Accessed 19 September 2026
*
* */