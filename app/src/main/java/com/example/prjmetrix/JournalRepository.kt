package com.example.prjmetrix

import android.content.Context
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

//(Simplified Coding, 2021)
class JournalRepository(context: Context) {

    //calls the object NetworkRepository to create the retrofit builder
    private val api = NetworkRepository.create(context, JournalApi::class.java)

    //private val tokenStore = Token(context.applicationContext)

    //CRUD functions handle the link calling the journalResponse which defines data class objects that are passed the mood, text and id
    suspend fun create(text: String, mood: String): Result<JournalResponse> =
        callOne { api.create(JournalRequest(text, mood)) }

    suspend fun update(id: Int, mood: String, text: String): Result<JournalResponse> =
        callOne { api.update(id, JournalRequest(text, mood)) }

    suspend fun getAll(): Result<List<JournalResponse>> = callList {api.getAll() }

    suspend fun getById(id: Int): Result<JournalResponse> = callOne {api.getById(id)}

    suspend fun delete(id: Int): Result<Unit> {
        return try {
            val response = api.delete(id)
            if (response.isSuccessful){
                Result.success(Unit)
            }
            else{
                Result.failure(Exception(readError(response)))
            }
        } catch (e: IOException) {
            android.util.Log.e("JournalRepository", "Network error calling MeTrix API", e)
            Result.failure(Exception("Cannot reach MeTrix. Check your connection and try again."))
        } catch (e: Exception) {
            android.util.Log.e("JournalRepository", "Unexpected error calling MeTrix API", e)
            Result.failure(Exception("Something went wrong: ${e.message}"))
        }
   }

    //functions deals with error messages related to JournalRepository failing to get a JSON response objects
    private suspend fun <T> callOne(block: suspend () -> Response<T>): Result<T> {
        return try {
            val response = block()
            if (response.isSuccessful) {
                response.body()?.let {Result.success(it)}
                    ?: Result.failure(Exception("Empty response from the server."))

                //saves the email, user id and tokens to preferences data store Token.kt
//                tokenStore.save(body.uid, body.email, body.idToken, body.refreshToken)
//                Result.success(body)
            } else {
                Result.failure(Exception(readError(response)))
            }
            //error messages if errors occur logged
        } catch (e: IOException) {
            android.util.Log.e("JournalRepository", "Network error calling MeTrix API", e)
            Result.failure(Exception("Cannot reach MeTrix. Check your connection and try again."))
        } catch (e: Exception) {
            android.util.Log.e("JournalRepository", "Unexpected error calling MeTrix API", e)
            Result.failure(Exception("Something went wrong: ${e.message}"))
        }
    }

    //functions deals with error messages related to JournalRepository failing to get a JSON response objects
    private suspend fun callList(block: suspend () -> Response<List<JournalResponse>>): Result<List<JournalResponse>> {
        return try {
            val response = block()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())

                //saves the email, user id and tokens to preferences data store Token.kt
//                tokenStore.save(body.uid, body.email, body.idToken, body.refreshToken)
//                Result.success(body)
            } else {
                Result.failure(Exception(readError(response)))
            }
            //error messages if errors occur logged
        } catch (e: IOException) {
            android.util.Log.e("JournalRepository", "Network error calling MeTrix API", e)
            Result.failure(Exception("Cannot reach MeTrix. Check your connection and try again."))
        } catch (e: Exception) {
            android.util.Log.e("JournalRepository", "Unexpected error calling MeTrix API", e)
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
            defaultMessage(response.code()) //errors like 400 or 401  are sent by the defaultMessage function
        }
    }

    //errors like 400 or 401  are sent by the defaultMessage function
    private fun defaultMessage(code: Int) = when (code) {
        400, 401 -> "Error Loading Journal"
        else -> "Something went wrong. Please try again."
    }
}

/*References
*
* Burak, 2022. JWT Authentication and Refresh Token in Android with Retrofit Interceptor & Authenticator. (Version 10.0) [source code]. Available at: < https://proandroiddev.com/jwt-authentication-and-refresh-token-in-android-with-retrofit-interceptor-authenticator-da021f7f7534 > [Accessed 19 September 2026]
* Siddhi, 2025. Google OAuth in Android using Credential Manager (Modern Approach). (Version 10.0) [Source Code]. Available at : < https://medium.com/@InkAndLogic/google-oauth-in-android-using-credential-manager-modern-approach-7d5e8ea37ee4 > [Accessed 19 September 2026]
* Simplified Coding, 2021. #12 Android Login/Signup with MVVM - user Logout [video online] [source code]. Available at: < https://youtu.be/q_5xrPYRoAg?si=gRbHfP1TazLTqbrY > [Accessed 19 September 2026
* StackOverflow, 2023. How to add http logging interceptor along with another interceptor in android with kotlin. (Version 10.0) [source code]. Available at: < https://stackoverflow.com/questions/67239799/how-to-add-http-logging-interceptor-along-with-another-interceptor-in-android-wi > [Accessed 19 September 2026]
* */