package com.example.prjmetrix

//(Simplified Coding, 2021)

//data class for sign up with email and password
data class SignUpRequest(
    val email: String,
    val password: String
)

//data class for sign in with email and password
data class SignInRequest(
    val email: String,
    val password: String
)

//data class for google SSO takes the google id token to firebase credentials to sav the account there
data class GoogleSignInRequest(
    val idToken: String
)

//when authentication is successfull the JSON body has these details
data class AuthResponse( //linked to authrepository
    val uid: String,
    val email: String,
    val idToken: String,
    val refreshToken: String
)

//used for error messages
data class ApiError(
    val message: String?
)

/*References
*
* Simplified Coding, 2021. #12 Android Login/Signup with MVVM - user Logout [video online] [source code]. Available at: < https://youtu.be/q_5xrPYRoAg?si=gRbHfP1TazLTqbrY > [Accessed 19 September 2026
*
* */