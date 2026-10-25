package com.example.prjmetrix

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

// Provides functions to access and manage habit data.
@Dao
interface HabitDao {

    // Observes habits that are not waiting to be deleted.
    // Emits an updated list whenever the table changes.
    @Query("SELECT * FROM habits WHERE pending_delete = 0 ORDER BY localId ASC")
    fun observeAll(): Flow<List<HabitEntity>>

    // Gets a habit by its local ID.
    @Query("SELECT * FROM habits WHERE localId = :localId")
    suspend fun getById(localId: Int): HabitEntity?

    // Gets a habit by its server ID.
    @Query("SELECT * FROM habits WHERE server_id = :serverId")
    suspend fun getByServerId(serverId: Int): HabitEntity?

    // Gets new habits that still need to be uploaded.
    @Query("SELECT * FROM habits WHERE server_id IS NULL AND pending_delete = 0")
    suspend fun getNotYetUploaded(): List<HabitEntity>

    // Gets progress updates that still need to be synced.
    @Query("SELECT * FROM habits WHERE pending_sync = 1 AND server_id IS NOT NULL AND pending_delete = 0")
    suspend fun getPendingProgress(): List<HabitEntity>

    // Gets deleted habits that still need to be synced.
    @Query("SELECT * FROM habits WHERE pending_delete = 1 AND server_id IS NOT NULL")
    suspend fun getPendingDeletes(): List<HabitEntity>

    // Removes server habits that are no longer present on the server.
    @Query("DELETE FROM habits WHERE server_id IS NOT NULL AND pending_sync = 0 AND server_id NOT IN (:keepIds)")
    suspend fun deleteMissingFromServer(keepIds: List<Int>)

    // Adds a new habit.
    @Insert
    suspend fun insert(habit: HabitEntity): Long

    // Updates an existing habit.
    @Update
    suspend fun update(habit: HabitEntity)

    // Deletes a habit.
    @Delete
    suspend fun delete(habit: HabitEntity)
}

/*References
Gaur, A., 2025. Room Database in Android. (Version 10.0) [Source Code]. Available at: < https://medium.com/@anandgaur2207/room-database-in-android-d5f279d4648a > [Accessed 23 October 2026]
Android Developers, n.d. Write asynchronous DAO queries. (Version 10.0) [Source Code]. Available at: < https://developer.android.com/training/data-storage/room/async-queries > [Accessed 23 October 2026]
*/