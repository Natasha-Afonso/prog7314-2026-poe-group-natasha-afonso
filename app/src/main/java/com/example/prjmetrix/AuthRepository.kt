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

class AuthRepository(private val context: Context) {

    //The actual URL is http://localhost:5151 - use this if you are running through your android phone
    //if that fails then you need to use the IP address eg: http://192.168.x.x:5161
    //if you are running on emulator then use this URL: http://10.0.2.2:5161
    private val baseUrl = "http://10.0.2.2:5161"

    private val tokenStore = Token(context)

    private val api: AuthApi by lazy {
        val authInterceptor = Interceptor { chain ->
            val token = runBlocking { tokenStore.getIdToken() }
            val request = if (token.isNullOrBlank()) {
                chain.request()
            } else {
                chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            }
            chain.proceed(request)
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    suspend fun signUp(email: String, password: String): Result<AuthResponse> =
        call { api.signUp(SignUpRequest(email.trim(), password)) }

    suspend fun signIn(email: String, password: String): Result<AuthResponse> =
        call { api.signIn(SignInRequest(email.trim(), password)) }

    suspend fun signInWithGoogle(webClientId: String): Result<AuthResponse> {
        val idToken = getGoogleIdToken(webClientId)
            ?: return Result.failure(Exception("Google sign-in was cancelled."))

        return call { api.signInWithGoogleSso(GoogleSignInRequest(idToken)) }
    }

    suspend fun signOut() = tokenStore.clear()

    suspend fun isLoggedIn() = tokenStore.isLoggedIn()

    private suspend fun getGoogleIdToken(webClientId: String): String? {
        val option = GetSignInWithGoogleOption.Builder(serverClientId = webClientId).build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()

        return try {
            val result = CredentialManager.create(context).getCredential(context, request)
            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(result.credential.data)
            googleIdTokenCredential.idToken
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Google credential retrieval failed", e)
            null
        }
    }

    private suspend fun call(block: suspend () -> Response<AuthResponse>): Result<AuthResponse> {
        return try {
            val response = block()

            if (response.isSuccessful) {
                val body = response.body()
                    ?: return Result.failure(Exception("Empty response from the server."))

                tokenStore.save(body.uid, body.email, body.idToken, body.refreshToken)
                Result.success(body)
            } else {
                Result.failure(Exception(readError(response)))
            }
        } catch (e: IOException) {
            android.util.Log.e("AuthRepository", "Network error calling MeTrix API", e)
            Result.failure(Exception("Cannot reach MeTrix. Check your connection and try again."))
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Unexpected error calling MeTrix API", e)
            Result.failure(Exception("Something went wrong: ${e.message}"))
        }
    }

    private fun readError(response: Response<*>): String {
        val raw = response.errorBody()?.string().orEmpty()
        return try {
            Gson().fromJson(raw, ApiError::class.java)?.message
                ?: defaultMessage(response.code())
        } catch (e: Exception) {
            defaultMessage(response.code())
        }
    }

    private fun defaultMessage(code: Int) = when (code) {
        400, 401 -> "Incorrect email or password."
        else -> "Something went wrong. Please try again."
    }
}