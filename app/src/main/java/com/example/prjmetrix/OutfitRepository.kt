package com.example.prjmetrix

import android.content.Context

class OutfitRepository(context: Context) {

    private val api = NetworkRepository.create(context.applicationContext, OutfitApi::class.java)

    suspend fun getOutfits() = api.getOutfits().bodyOrError()
    suspend fun create(name: String, ids: List<Int>) =
        api.create(CreateOutfitRequest(name, ids)).bodyOrError()

    suspend fun schedule(outfitId: Int, date: String): Result<Unit> =
        api.schedule(ScheduleOutfitRequest(outfitId, date)).unitResult()

    suspend fun schedule(from: String, to: String) = api.schedule(from, to).bodyOrError()
    suspend fun wearLog(outfitId: Int, accepted: Boolean, date: String? = null) =
        api.wearLog(WearOutfitRequest(outfitId, accepted, date)).unitResult()
}

private suspend fun <T> retrofit2.Response<T>.bodyOrError(): Result<T> =
    if (isSuccessful) body()?.let { Result.success(it) }
        ?: Result.failure(Exception("Empty server response."))
    else Result.failure(Exception("Request failed (${code()})."))

private fun retrofit2.Response<Unit>.unitResult(): Result<Unit> =
    if (isSuccessful) Result.success(Unit) else Result.failure(Exception("Request failed (${code()})."))
