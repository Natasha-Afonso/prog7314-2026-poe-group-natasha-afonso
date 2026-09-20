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

class HabitRepository(private val context: Context) {

    //The actual URL is http://localhost:5161 - use this if you are running through your android phone
    //if that fails then you need to use the IP address of your laptop eg: http://192.168.x.x:5161
    //if you are running on emulator then use this URL: http://10.0.2.2:5161
    private val baseUrl = "http://192.168.0.183:5161"

    private val tokenStore = Token(context) //instance is created in token db

    //(Burak, 2022)
    private val api: HabitAPI by lazy {
        val authInterceptor = Interceptor { chain -> //authinterceptor find the tokenID stored in db and when found gets sent to backend
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

        //creating an instance for OkHttp for network logging
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        //(StackOverflow, 2023)
        val client = OkHttpClient.Builder() //building the client object
            .addInterceptor(authInterceptor) //gets the token and passes through the interceptor to inject the Authorization JWT
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS) //connection timesout after 30sec
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        //(StackOverflow, 2023)
        //using GSON to parse JSON objects
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) //converts raw strings into objects for authresponse data class
            .build()
            .create(HabitAPI::class.java) //calls the functions endpoint routes in the interface
    }

}

/*References
*
* Burak, 2022. JWT Authentication and Refresh Token in Android with Retrofit Interceptor & Authenticator. (Version 10.0) [source code]. Available at: < https://proandroiddev.com/jwt-authentication-and-refresh-token-in-android-with-retrofit-interceptor-authenticator-da021f7f7534 > [Accessed 19 September 2026]
* Siddhi, 2025. Google OAuth in Android using Credential Manager (Modern Approach). (Version 10.0) [Source Code]. Available at : < https://medium.com/@InkAndLogic/google-oauth-in-android-using-credential-manager-modern-approach-7d5e8ea37ee4 > [Accessed 19 September 2026]
* Simplified Coding, 2021. #12 Android Login/Signup with MVVM - user Logout [video online] [source code]. Available at: < https://youtu.be/q_5xrPYRoAg?si=gRbHfP1TazLTqbrY > [Accessed 19 September 2026
* StackOverflow, 2023. How to add http logging interceptor along with another interceptor in android with kotlin. (Version 10.0) [source code]. Available at: < https://stackoverflow.com/questions/67239799/how-to-add-http-logging-interceptor-along-with-another-interceptor-in-android-wi > [Accessed 19 September 2026]
* */