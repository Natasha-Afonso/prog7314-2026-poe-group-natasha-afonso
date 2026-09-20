package com.example.prjmetrix

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path

//(Simplified Coding, 2021)
interface JournalApi {
    //retrofit interface to map the functions to backend API endpoints

    //POST request for the user to sign up to the api endpoint
    @POST("api/journal")
    //takes the object with email and password into JSON request body
    suspend fun create(@Body body: JournalRequest): Response<JournalResponse>

    //POST request for the user to sign in to the api endpoint
    @GET("api/journal/{id}")
    //sends the users email and password as a JSON request body
    suspend fun getById(@Body body: JournalRequest): Response<JournalResponse>

    //POST request for goggle third party single sign on api endpoint
    @GET("api/journal")
    //takes the google ID token to the backend into firebase for authentication to add that users account
    suspend fun getAll(@Body body: JournalRequest): Response<JournalResponse>

    //GET request to get users data api endpoint
    @DELETE("api/journal/{id}")
    suspend fun delete(@Path("id") id: Int): Response<Unit> //works with JWT to identify logged in user

    @PUT("api/journal/{id}")
    suspend fun update(@Path("id") id: Int, @Body body: JournalRequest): Response<JournalResponse>

}

/*References
*
* Simplified Coding, 2021. #12 Android Login/Signup with MVVM - user Logout [video online] [source code]. Available at: < https://youtu.be/q_5xrPYRoAg?si=gRbHfP1TazLTqbrY > [Accessed 19 September 2026
*
* */