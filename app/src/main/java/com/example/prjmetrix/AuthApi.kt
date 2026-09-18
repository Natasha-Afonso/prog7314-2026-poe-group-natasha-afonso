package com.example.prjmetrix

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response

interface AuthApi {

    @POST("api/auth/signup")
    suspend fun signUp(@Body body: SignUpRequest): Response<AuthResponse>

    @POST("api/auth/signin")
    suspend fun signIn(@Body body: SignInRequest): Response<AuthResponse>

    @POST("api/auth/sso")
    suspend fun signInWithGoogleSso(@Body body: GoogleSignInRequest): Response<AuthResponse>

    @GET("api/auth/me")
    suspend fun me(): Response<Map<String, String?>>
}