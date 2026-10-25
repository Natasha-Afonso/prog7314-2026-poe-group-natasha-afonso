package com.example.prjmetrix

import androidx.room3.Dao
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

// DAO
// The DAO provides the methods the rest of the app uses to read and write the table; Room
// generates the implementation (Gaur, 2025; Android Developers, 2026).
@Dao
interface WalletDao {

    // Observable query: returning a Flow means every screen showing the balance is updated as
    // soon as the row changes (Android Developers, 2026).
    @Query("SELECT coins FROM wallet WHERE id = 1")
    fun observeCoins(): Flow<Int?>

    // Creates the wallet the first time only; does nothing if the row already exists
    @Query("INSERT OR IGNORE INTO wallet (id, coins) VALUES (1, :startingCoins)")
    suspend fun createIfMissing(startingCoins: Int)

    // Adds coins (habit rewards, daily quest reward)
    @Query("UPDATE wallet SET coins = coins + :amount WHERE id = 1")
    suspend fun add(amount: Int)

    // Takes coins only if the user can afford it. The check and the subtraction happen in one
    // SQL statement, so the balance can never go below zero. Returns how many rows changed:
    // 1 = paid, 0 = not enough coins.
    @Query("UPDATE wallet SET coins = coins - :price WHERE id = 1 AND coins >= :price")
    suspend fun spend(price: Int): Int
}

/*References

Gaur, A., 2025. Room Database in Android. (Version 10.0) [Source Code]. Available at: < https://medium.com/@anandgaur2207/room-database-in-android-d5f279d4648a > [Accessed 23 October 2026]
Android Developers, 2026. Save data in a local database using Room. (Version 10.0) [Source Code]. Available at: < https://developer.android.com/training/data-storage/room > [Accessed 23 October 2026]

 */