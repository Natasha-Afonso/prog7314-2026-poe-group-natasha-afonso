package com.example.prjmetrix

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

// Contains the database functions for furniture.
@Dao
interface FurnitureDao {

    // Updates the UI when the furniture list changes.
    @Query("SELECT * FROM owned_furniture ORDER BY localId")
    fun observeAll(): Flow<List<FurnitureEntity>>

    // Gets a furniture item by its local ID.
    @Query("SELECT * FROM owned_furniture WHERE localId = :id")
    suspend fun getById(id: Int): FurnitureEntity?

    // Counts the furniture items in the table.
    @Query("SELECT COUNT(*) FROM owned_furniture")
    suspend fun count(): Int

    // Adds a furniture item.
    @Insert
    suspend fun insert(item: FurnitureEntity): Long

    // Updates a furniture item.
    @Update
    suspend fun update(item: FurnitureEntity)

    // Deletes a furniture item.
    @Delete
    suspend fun delete(item: FurnitureEntity)
}

/*References
Gaur, A., 2025. Room Database in Android. (Version 10.0) [Source Code]. Available at: <https://medium.com/@anandgaur2207/room-database-in-android-d5f279d4648a> [Accessed 23 October 2026]
Android Developers, 2026. Save data in a local database using Room. (Version 10.0) [Source Code]. Available at: <https://developer.android.com/training/data-storage/room> [Accessed 23 October 2026]
*/