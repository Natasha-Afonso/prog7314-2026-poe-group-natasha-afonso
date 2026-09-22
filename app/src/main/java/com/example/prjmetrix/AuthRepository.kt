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
class AuthRepository(private val context: Context) {

    private val api: AuthApi by lazy {
        NetworkRepository.create(context, AuthApi::class.java)
    }

    private val tokenStore = Token(context.applicationContext)

    //sign up, sign in and signinwithgoogle functions handle the connection calling the authResponse which defines data class objects
    suspend fun signUp(email: String, password: String): Result<AuthResponse> =
        call { api.signUp(SignUpRequest(email.trim(), password)) }

    suspend fun signIn(email: String, password: String): Result<AuthResponse> =
        call { api.signIn(SignInRequest(email.trim(), password)) }

    suspend fun signInWithGoogle(webClientId: String): Result<AuthResponse> {
        val idToken = getGoogleIdToken(webClientId)
            ?: return Result.failure(Exception("Google sign-in failed."))

        return call { api.signInWithGoogleSso(GoogleSignInRequest(idToken)) }
    }

    //links to the AuthViewModel to clear storage
    suspend fun signOut() = tokenStore.clear()
    suspend fun isLoggedIn() = tokenStore.isLoggedIn()

    //(Sidi, 2025)
    //google sign in function uses a credential manager authorized through the web client ID
    private suspend fun getGoogleIdToken(webClientId: String): String? {
        //create a google ID option
        val option = GetSignInWithGoogleOption.Builder(serverClientId = webClientId).build()
        //create a credential request
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()

        //(Sidi, 2025)
        return try { //launches the sign in
            val result = CredentialManager.create(context).getCredential(context, request)
            val googleIdTokenCredential = //created a form that generated a google ID token that gets saved to DB
                GoogleIdTokenCredential.createFrom(result.credential.data)
            googleIdTokenCredential.idToken
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Google credential retrieval failed", e)
            null
        }
    }

    //functions deals with error messages related to authrepository failing to get a JSON response objects
    private suspend fun call(block: suspend () -> Response<AuthResponse>): Result<AuthResponse> {
        return try {
            val response = block()

            if (response.isSuccessful) {
                val body = response.body()
                    ?: return Result.failure(Exception("Empty response from the server."))

                //saves the email, user id and tokens to preferences data store Token.kt
                tokenStore.save(body.uid, body.email, body.idToken, body.refreshToken)
                Result.success(body)
            } else {
                Result.failure(Exception(readError(response)))
            }
            //error messages if errors occur logged
        } catch (e: IOException) {
            android.util.Log.e("AuthRepository", "Network error calling MeTrix API", e)
            Result.failure(Exception("Cannot reach MeTrix. Check your connection and try again."))
        } catch (e: Exception) {
            android.util.Log.e("AuthRepository", "Unexpected error calling MeTrix API", e)
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
        400, 401 -> "Incorrect email or password."
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