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

    //POST request for the user to create a new journal entry
    @POST("api/journal")
    //takes the object with text, mood, time & id into JSON request body
    suspend fun create(@Body body: JournalRequest): Response<JournalResponse>

    //GET request for the user to get a specific entry already created
    @GET("api/journal/{id}")
    //returns the entry in JSON request body
    suspend fun getById(@Path("id") id: Int): Response<JournalResponse>

    //GET request for getting all the journal entries
    @GET("api/journal")
    //returns all the entries the user has created in a list form in index
    suspend fun getAll(): Response<List<JournalResponse>>

    //DELETE request to delete specific entry
    @DELETE("api/journal/{id}")
    suspend fun delete(@Path("id") id: Int): Response<Unit> //delete the selected entry from db

    //PUT updates a journal entry
    @PUT("api/journal/{id}")
    suspend fun update(@Path("id") id: Int, @Body body: JournalRequest): Response<JournalResponse> //JSON response body returns with updated details

}

/*References
*
* Simplified Coding, 2021. #12 Android Login/Signup with MVVM - user Logout [video online] [source code]. Available at: < https://youtu.be/q_5xrPYRoAg?si=gRbHfP1TazLTqbrY > [Accessed 19 September 2026
*
* */