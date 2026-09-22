package com.example.prjmetrix
import android.content.Context

class SelfCareRepository(context: Context) {
    private val api = NetworkRepository.create(context.applicationContext, SelfCareApi::class.java)
    suspend fun save(score: Int, reflection: String): Result<Unit> =
        api.save(SelfCareQuizRequest(score, reflection))
            .let { if (it.isSuccessful) Result.success(Unit)

            else Result.failure(Exception("Could not save quiz.")) }

}