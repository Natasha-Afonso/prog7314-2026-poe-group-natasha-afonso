package com.example.prjmetrix

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

// DAO: contains the database functions used to read and change habits.
// It lets the app work with the data without writing SQL for every operation (Gaur, 2025).
@Dao
interface HabitDao {

    // Flow updates the habit list whenever the table changes, so the home screen
    // can show locally saved changes straight away (Android Developers, 2026).
    @Query("SELECT * FROM habits ORDER BY localId ASC")
    fun observeAll(): Flow<List<HabitEntity>>

    // suspend keeps one-time database reads from running on the main thread.
    @Query("SELECT * FROM habits WHERE localId = :localId")
    suspend fun getById(localId: Int): HabitEntity?

    @Query("SELECT * FROM habits WHERE server_id = :serverId")
    suspend fun getByServerId(serverId: Int): HabitEntity?

    // Finds habits created offline that have not been uploaded yet.
    @Query("SELECT * FROM habits WHERE server_id IS NULL")
    suspend fun getNotYetUploaded(): List<HabitEntity>

    // Finds progress changes that still need to be sent to the API.
    @Query("SELECT * FROM habits WHERE pending_sync = 1 AND server_id IS NOT NULL")
    suspend fun getPendingProgress(): List<HabitEntity>

    // Removes synced habits that no longer exist on the server.
    @Query("DELETE FROM habits WHERE server_id IS NOT NULL AND pending_sync = 0 AND server_id NOT IN (:keepIds)")
    suspend fun deleteMissingFromServer(keepIds: List<Int>)

    // Room annotations handle the basic database operations (Gaur, 2025).
    @Insert
    suspend fun insert(habit: HabitEntity): Long

    @Update
    suspend fun update(habit: HabitEntity)

    @Delete
    suspend fun delete(habit: HabitEntity)
}

/* References

Gaur, A., 2025. Room Database in Android. (Version 10.0) [Source Code].
Available at: https://medium.com/@anandgaur2207/room-database-in-android-d5f279d4648a
[Accessed 23 October 2026].
Android Developers, 2026. Write asynchronous DAO queries. (Version 10.0) [Source Code].
Available at: https://developer.android.com/training/data-storage/room/async-queries
[Accessed 23 October 2026].

*/