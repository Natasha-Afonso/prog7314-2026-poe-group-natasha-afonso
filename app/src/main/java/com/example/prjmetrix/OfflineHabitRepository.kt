package com.example.prjmetrix

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

enum class SyncResult { OK, OFFLINE, SESSION_EXPIRED }

// Offline-first repository: Room supplies the data shown in the UI, so habits remain
// accessible offline. Changes are saved locally first and synced with the API afterwards.
// (Android Developers, 2026)
class OfflineHabitRepository(context: Context) {

    private val dao = AppDatabase.getDatabase(context).habitDao() // Gaur, 2025
    private val api = HabitRepository(context).api
    private fun today() = LocalDate.now().toString()

    // Emits an updated list whenever the habits table changes (Android Developers, n.d.).
    fun observeHabits(): Flow<List<HabitEntity>> = dao.observeAll()

    // New habits are saved locally and marked for upload.
    suspend fun addHabit(label: String) {
        dao.insert(HabitEntity(label = label, progressDate = today(), pendingSync = true))
    }

    // Saves progress locally first, then attempts to update the API.
    suspend fun setProgress(localId: Int, progress: Float) {
        val habit = dao.getById(localId) ?: return
        val updated = habit.copy(
            progress = progress.coerceIn(0f, 1f),
            progressDate = today(),
            pendingSync = true
        )
        dao.update(updated)
        pushProgress(updated)
    }

    // Returns false if the habit has not been uploaded or the API update fails.
    private suspend fun pushProgress(habit: HabitEntity): Boolean {
        val serverId = habit.serverId ?: return false // sync() uploads new habits first.

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

    // Uploads pending changes before fetching the latest habits from the API.
    suspend fun sync(): SyncResult {
        try {
            //  Upload habits created offline. Removing the local copy allows the server's
            // version to be downloaded below, but any progress made before upload resets to 0%.
            for (habit in dao.getNotYetUploaded()) {
                val response = api.createHabit(HabitItemDto(label = habit.label))
                if (response.isSuccessful) {
                    dao.delete(habit)
                } else {
                    Log.e("Habits", "Create failed: ${response.code()}")
                    if (response.code() == 401) return SyncResult.SESSION_EXPIRED
                }
            }

            //Upload progress changes that are still pending.
            for (habit in dao.getPendingProgress()) pushProgress(habit)

            //  Fetch today's habits and merge them into Room.
            val response = api.getHabits(today())
            if (!response.isSuccessful) {
                Log.e("Habits", "Load failed: ${response.code()}")
                return if (response.code() == 401) {
                    SyncResult.SESSION_EXPIRED
                } else {
                    SyncResult.OFFLINE
                }
            }

            val habits = response.body().orEmpty()
            for (dto in habits) {
                val existing = dao.getByServerId(dto.id)

                if (existing == null) {
                    // Add habits from the server that are not stored locally.
                    dao.insert(
                        HabitEntity(
                            serverId = dto.id,
                            label = dto.label,
                            iconKey = dto.iconKey,
                            colorHex = dto.colorHex,
                            goldCoins = dto.goldCoins,
                            progress = dto.progress,
                            progressDate = today()
                        )
                    )
                } else if (!existing.pendingSync) {
                    // Update synced habits without overwriting unsent offline changes.
                    dao.update(
                        existing.copy(
                            label = dto.label,
                            iconKey = dto.iconKey,
                            colorHex = dto.colorHex,
                            goldCoins = dto.goldCoins,
                            progress = dto.progress,
                            progressDate = today()
                        )
                    )
                }
            }

            // Remove local habits missing from the server.
            // valid when the server returns an empty list.
            dao.deleteMissingFromServer(habits.map { it.id }.ifEmpty { listOf(-1) })

            return SyncResult.OK
        } catch (e: Exception) {
            // Keep local data available when the network or server is unreachable.
            Log.e("Habits", "Could not reach the API", e)
            return SyncResult.OFFLINE
        }
    }
}

/*
References

Gaur, A., 2025. Room Database in Android. (Version 10.0) [Source Code].
Available at: https://medium.com/@anandgaur2207/room-database-in-android-d5f279d4648a
[Accessed 23 October 2026].
Android Developers, 2026. Save data in a local database using Room. (Version 10.0)
[Source Code]. Available at: https://developer.android.com/training/data-storage/room
[Accessed 23 October 2026].
*/