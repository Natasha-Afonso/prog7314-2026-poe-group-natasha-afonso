package com.example.prjmetrix

data class SignUpRequest(
    val email: String,
    val password: String
)

data class SignInRequest(
    val email: String,
    val password: String
)

data class GoogleSignInRequest(
    val idToken: String
)

data class AuthResponse(
    val uid: String,
    val email: String,
    val idToken: String,
    val refreshToken: String
)

data class ApiError(
    val message: String?
)