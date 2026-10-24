package com.example.prjmetrix

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver

// Connects the habit table to its DAO and manages the Room database (Gaur, 2025).
@Database(entities = [HabitEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao

    companion object {
        // Keeps one shared database instance available across the app.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Creates the database only when it does not already exist.
        // applicationContext helps avoid keeping an Activity context alive.
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder<AppDatabase>(
                    context.applicationContext,
                    "habit_database"
                )
                    .setDriver(AndroidSQLiteDriver())
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}

/* References

Gaur, A., 2025. Room Database in Android. (Version 10.0) [Source Code].
Available at: https://medium.com/@anandgaur2207/room-database-in-android-d5f279d4648a
[Accessed 23 October 2026].
Android Developers, 2026. Save data in a local database using Room. (Version 10.0)
[Source Code]. Available at: https://developer.android.com/training/data-storage/room
[Accessed 23 October 2026].

*/