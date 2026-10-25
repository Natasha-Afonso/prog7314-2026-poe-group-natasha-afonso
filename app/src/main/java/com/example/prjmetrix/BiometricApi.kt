package com.example.prjmetrix

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

//(Simplified Coding, 2021)
interface BiometricApi {
    //retrofit interface to map the functions to backend API endpoints

    //GET request to check if the user has a fallback PIN and if biometric login is on
    @GET("api/biometric")
    suspend fun getStatus(): Response<BiometricStatusResponse>

    //PUT request for the user to create or change their fallback PIN
    @PUT("api/biometric/pin")
    //takes the object with the new PIN into JSON request body
    suspend fun savePin(@Body body: SavePinRequest): Response<BiometricResponse>

    //POST request to check the PIN the user entered on the lock screen
    @POST("api/biometric/pin/verify")
    suspend fun verifyPin(@Body body: VerifyPinRequest): Response<BiometricResponse>

    //PUT request to turn biometric login on or off in settings
    @PUT("api/biometric/biometric")
    suspend fun setBiometric(@Body body: BiometricRequest): Response<BiometricStatusResponse>

}

/*References
*
* Simplified Coding, 2021. #12 Android Login/Signup with MVVM - user Logout (Version 1.0) [video online] [source code]. Available at: < https://youtu.be/q_5xrPYRoAg?si=gRbHfP1TazLTqbrY > [Accessed 19 September 2026]
*
* */