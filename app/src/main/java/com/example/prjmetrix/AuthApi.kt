package com.example.prjmetrix

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response

//(Simplified Coding, 2021)
interface AuthApi {
    //retrofit interface to map the functions to backend API endpoints

    //POST request for the user to sign up to the api endpoint
    @POST("api/auth/signup")
    //takes the object with email and password into JSON request body
    suspend fun signUp(@Body body: SignUpRequest): Response<AuthResponse>

    //POST request for the user to sign in to the api endpoint
    @POST("api/auth/signin")
    //sends the users email and password as a JSON request body
    suspend fun signIn(@Body body: SignInRequest): Response<AuthResponse>

    //POST request for goggle third party single sign on api endpoint
    @POST("api/auth/sso")
    //takes the google ID token to the backend into firebase for authentication to add that users account
    suspend fun signInWithGoogleSso(@Body body: GoogleSignInRequest): Response<AuthResponse>

    //GET request to get users data api endpoint
    @GET("api/auth/me")
    suspend fun me(): Response<Map<String, String?>> //works with JWT to identify logged in user
}

/*References
*
* Simplified Coding, 2021. #12 Android Login/Signup with MVVM - user Logout [video online] [source code]. Available at: < https://youtu.be/q_5xrPYRoAg?si=gRbHfP1TazLTqbrY > [Accessed 19 September 2026
*
* */