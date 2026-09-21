package com.example.prjmetrix

import android.content.Context


class HabitRepository(private val context: Context) {

    val api: HabitAPI by lazy {
        NetworkRepository.create(context, HabitAPI::class.java)
    }



}

/*References
*
* Burak, 2022. JWT Authentication and Refresh Token in Android with Retrofit Interceptor & Authenticator. (Version 10.0) [source code]. Available at: < https://proandroiddev.com/jwt-authentication-and-refresh-token-in-android-with-retrofit-interceptor-authenticator-da021f7f7534 > [Accessed 19 September 2026]
* Siddhi, 2025. Google OAuth in Android using Credential Manager (Modern Approach). (Version 10.0) [Source Code]. Available at : < https://medium.com/@InkAndLogic/google-oauth-in-android-using-credential-manager-modern-approach-7d5e8ea37ee4 > [Accessed 19 September 2026]
* Simplified Coding, 2021. #12 Android Login/Signup with MVVM - user Logout [video online] [source code]. Available at: < https://youtu.be/q_5xrPYRoAg?si=gRbHfP1TazLTqbrY > [Accessed 19 September 2026
* StackOverflow, 2023. How to add http logging interceptor along with another interceptor in android with kotlin. (Version 10.0) [source code]. Available at: < https://stackoverflow.com/questions/67239799/how-to-add-http-logging-interceptor-along-with-another-interceptor-in-android-wi > [Accessed 19 September 2026]
* */