package com.example.prjmetrix

import android.content.Context

class MagazineRepository(context: Context) {

    private val api =
        NetworkRepository.create(
            context.applicationContext,
            MagazineApi::class.java
        )

    suspend fun weekly(): Result<MagazineDto> {
        val response = api.weekly()

        return if (response.isSuccessful) {

            val body = response.body()

            if (body != null) {
                Result.success(body)
            } else {
                Result.failure(
                    Exception("No magazine data.")
                )
            }

        } else {

            Result.failure(
                Exception(
                    "Could not load magazine (${response.code()})."
                )
            )
        }
    }
}