package com.example.prjmetrix

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.sqlite.execSQL

// Connects the database tables to their DAOs.
@Database(
    entities = [HabitEntity::class, FurnitureEntity::class, WalletEntity::class],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun furnitureDao(): FurnitureDao
    abstract fun walletDao(): WalletDao

    companion object {
        // Keeps one shared database instance.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Version 1 -> 2: creates the furniture table.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override suspend fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                    "CREATE TABLE IF NOT EXISTS `owned_furniture` (" +
                            "`localId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`item_key` TEXT NOT NULL, " +
                            "`placed` INTEGER NOT NULL, " +
                            "`pos_x` REAL NOT NULL, " +
                            "`pos_y` REAL NOT NULL)"
                )
            }
        }

        // Version 2 -> 3: creates the wallet table with 1250 starting coins.
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override suspend fun migrate(connection: SQLiteConnection) {
                connection.execSQL(
                    "CREATE TABLE IF NOT EXISTS `wallet` (" +
                            "`id` INTEGER PRIMARY KEY NOT NULL, " +
                            "`coins` INTEGER NOT NULL)"
                )
                connection.execSQL("INSERT OR IGNORE INTO wallet (id, coins) VALUES (1, 1250)")
            }
        }

        // Version 3 -> 4: adds daily targets and reward dates to habits.
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override suspend fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE habits ADD COLUMN target_count INTEGER NOT NULL DEFAULT 1")
                connection.execSQL("ALTER TABLE habits ADD COLUMN half_rewarded_date TEXT")
                connection.execSQL("ALTER TABLE habits ADD COLUMN full_rewarded_date TEXT")
            }
        }

        // Version 4 -> 5: adds support for deleting habits while offline.
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override suspend fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE habits ADD COLUMN pending_delete INTEGER NOT NULL DEFAULT 0")
            }
        }

        // Creates the database if it does not already exist.
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder<AppDatabase>(
                    context.applicationContext,
                    "habit_database"
                )
                    .setDriver(AndroidSQLiteDriver())
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/*References
Gaur, A., 2025. Room Database in Android. (Version 10.0) [Source Code]. Available at: <https://medium.com/@anandgaur2207/room-database-in-android-d5f279d4648a> [Accessed 23 October 2026]
Android Developers, 2026. Save data in a local database using Room. (Version 10.0) [Source Code]. Available at: <https://developer.android.com/training/data-storage/room> [Accessed 23 October 2026]
Android Developers, 2026. Migrate your Room database. Available at: <https://developer.android.com/training/data-storage/room/migrating-db-versions> [Accessed 24 October 2026]
*/
