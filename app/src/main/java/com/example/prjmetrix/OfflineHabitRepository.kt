package com.example.prjmetrix

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

enum class SyncResult { OK, OFFLINE, SESSION_EXPIRED }

// Handles habit data locally and syncs changes with the API.
class OfflineHabitRepository(context: Context) {

    // Gets access to the local database tables.
    private val dao = AppDatabase.getDatabase(context).habitDao()
    private val walletDao = AppDatabase.getDatabase(context).walletDao()

    // Uses the existing API repository.
    private val api = HabitRepository(context).api

    private fun today() = LocalDate.now().toString()

    // Observes changes to the local habit list.
    fun observeHabits(): Flow<List<HabitEntity>> = dao.observeAll()

    // Saves a new habit locally for later syncing.
    suspend fun addHabit(label: String, targetCount: Int, iconKey: String, colorHex: String) {
        dao.insert(
            HabitEntity(
                label = label,
                iconKey = iconKey,
                colorHex = colorHex,
                targetCount = targetCount.coerceIn(1, MAX_DAILY_TARGET),
                progressDate = today(),
                pendingSync = true
            )
        )
    }

    // Saves progress locally and then tries to sync it.
    // Returns the number of coins earned.
    suspend fun setProgress(localId: Int, progress: Float): Int {
        val habit = dao.getById(localId) ?: return 0
        val newProgress = progress.coerceIn(0f, 1f)

        // Awards each reward only once per habit each day.
        var earned = 0
        var halfDate = habit.halfRewardedDate
        var fullDate = habit.fullRewardedDate
        if (newProgress >= 0.5f && halfDate != today()) {
            earned += HALF_GOAL_COINS
            halfDate = today()
        }
        if (newProgress >= 1f && fullDate != today()) {
            earned += GOAL_COINS
            fullDate = today()
        }

        val updated = habit.copy(
            progress = newProgress,
            progressDate = today(),
            pendingSync = true,
            halfRewardedDate = halfDate,
            fullRewardedDate = fullDate
        )
        dao.update(updated)
        if (earned > 0) walletDao.add(earned)
        pushProgress(updated)
        return earned
    }

    // Deletes a habit locally or marks it for syncing with the API.
    suspend fun deleteHabit(localId: Int) {
        val habit = dao.getById(localId) ?: return
        if (habit.serverId == null) {
            dao.delete(habit)
        } else {
            val flagged = habit.copy(pendingDelete = true, pendingSync = true)
            dao.update(flagged)
            pushDelete(flagged)
        }
    }

    // Deletes the habit from the API and local database.
    private suspend fun pushDelete(habit: HabitEntity): Boolean {
        val serverId = habit.serverId ?: return false
        return try {
            val response = api.deleteHabit(serverId)
            if (response.isSuccessful || response.code() == 404) {
                dao.delete(habit)
                true
            } else {
                Log.e("Habits", "Delete failed: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("Habits", "Could not delete habit", e)
            false
        }
    }

    // Sends progress updates to the API.
    private suspend fun pushProgress(habit: HabitEntity): Boolean {
        val serverId = habit.serverId ?: return false
        return try {
            val response = api.updateProgress(serverId, habit.progress, habit.progressDate)
            if (response.isSuccessful) {
                dao.update(habit.copy(pendingSync = false))
                true
            } else {
                Log.e("Habits", "Save failed: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("Habits", "Could not save progress", e)
            false
        }
    }

    // Syncs local changes and then updates the local list from the API.
    suspend fun sync(): SyncResult {
        try {
            // 0. Sync pending deletions.
            for (habit in dao.getPendingDeletes()) pushDelete(habit)

            // 1. Upload habits created offline.
            for (habit in dao.getNotYetUploaded()) {
                val response = api.createHabit(
                    HabitItemDto(
                        label = habit.label,
                        iconKey = habit.iconKey,
                        colorHex = habit.colorHex,
                        targetCount = habit.targetCount
                    )
                )
                val createdId = response.body()?.id
                if (response.isSuccessful && createdId != null) {
                    // Keeps the local habit and stores its server ID.
                    dao.update(habit.copy(serverId = createdId, pendingSync = true))
                } else if (response.isSuccessful) {
                    // Removes the local row if the response has no ID.
                    dao.delete(habit)
                } else {
                    Log.e("Habits", "Create failed: ${response.code()}")
                    if (response.code() == 401) return SyncResult.SESSION_EXPIRED
                }
            }

            // 2. Upload pending progress changes.
            for (habit in dao.getPendingProgress()) pushProgress(habit)

            // 3. Get today's habits from the API.
            val response = api.getHabits(today())
            if (!response.isSuccessful) {
                Log.e("Habits", "Load failed: ${response.code()}")
                return if (response.code() == 401) SyncResult.SESSION_EXPIRED else SyncResult.OFFLINE
            }
            val habits = response.body().orEmpty()
            for (dto in habits) {
                val existing = dao.getByServerId(dto.id)

                // Keep locally deleted habits hidden.
                if (existing?.pendingDelete == true) continue

                if (existing == null) {
                    dao.insert(
                        HabitEntity(
                            serverId = dto.id,
                            label = dto.label,
                            iconKey = dto.iconKey,
                            colorHex = dto.colorHex,
                            goldCoins = dto.goldCoins,
                            targetCount = dto.targetCount,
                            progress = dto.progress,
                            progressDate = today()
                        )
                    )
                } else if (!existing.pendingSync) {
                    // Updates only rows without pending local changes.
                    dao.update(
                        existing.copy(
                            label = dto.label,
                            iconKey = dto.iconKey,
                            colorHex = dto.colorHex,
                            goldCoins = dto.goldCoins,
                            targetCount = dto.targetCount,
                            progress = dto.progress,
                            progressDate = today()
                        )
                    )
                }
            }

            // Keeps the SQL query valid when the API returns an empty list.
            dao.deleteMissingFromServer(habits.map { it.id }.ifEmpty { listOf(-1) })
            return SyncResult.OK
        } catch (e: Exception) {
            // Keeps local data available when syncing fails.
            Log.e("Habits", "Could not reach the API", e)
            return SyncResult.OFFLINE
        }
    }
}

/*References
Gaur, A., 2025. Room Database in Android. (Version 10.0) [Source Code]. Available at: < https://medium.com/@anandgaur2207/room-database-in-android-d5f279d4648a > [Accessed 23 October 2026]
Android Developers, 2026. Save data in a local database using Room. (Version 10.0) [Source Code]. Available at: < https://developer.android.com/training/data-storage/room > [Accessed 23 October 2026]
*/