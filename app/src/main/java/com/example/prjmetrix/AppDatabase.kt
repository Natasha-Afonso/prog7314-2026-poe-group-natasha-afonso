package com.example.prjmetrix

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.AndroidSQLiteDriver
import androidx.sqlite.execSQL

// DATABASE
// This is the abstract class that connects the entities and DAOs; it also provides a
// singleton instance so only one database instance is created (Gaur, 2025).
// The class must be annotated with @Database listing every entity, be abstract, extend
// RoomDatabase, and expose an abstract no-argument function for each DAO
// (Android Developers, n.d.).
// Version 2 adds the owned_furniture table.
@Database(entities = [HabitEntity::class, FurnitureEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun furnitureDao(): FurnitureDao

    companion object {
        // Volatile keyword ensures thread safety for the database instance (Gaur, 2025)
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // MIGRATION
        // When the schema changes, the version number goes up and a Migration tells Room how to
        // get from the old schema to the new one; without a migration path Room throws an
        // IllegalStateException, and fallbackToDestructiveMigration() would permanently delete
        // the user's data (Android Developers, 2026). The habits table is untouched, so only
        // the new table is created.
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

        // Each RoomDatabase instance is fairly expensive and an app running in a single
        // process rarely needs more than one, so the singleton design pattern is followed
        // (Android Developers, n.d.).
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder<AppDatabase>(
                    context.applicationContext,
                    "habit_database"
                )
                    .setDriver(AndroidSQLiteDriver())
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

/*References
Gaur, A., 2025. Room Database in Android. (Version 10.0) [Source Code]. Available at: < https://medium.com/@anandgaur2207/room-database-in-android-d5f279d4648a > [Accessed 23 October 2026]
Android Developers, n.d. Save data in a local database using Room. (Version 10.0) [Source Code]. Available at: < https://developer.android.com/training/data-storage/room > [Accessed 23 October 2026]
Android Developers, 2026. Migrate your Room database. Available at: < https://developer.android.com/training/data-storage/room/migrating-db-versions > [Accessed 24 October 2026]
 */