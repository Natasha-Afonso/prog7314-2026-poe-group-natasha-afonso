package com.example.prjmetrix

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

// ROOM ENTITY
// An entity represents a table in the database, and each row in the table corresponds to an
// object of the entity class (Gaur, 2025). The "wallet" table only ever holds one row (id = 1):
// the user's coin balance. Keeping the balance in Room means the home screen and the shop read
// the same number, and it survives restarts and works offline (Android Developers, 2026).
@Entity(tableName = "wallet")
data class WalletEntity(
    // Fixed id so there is exactly one wallet row
    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "coins") val coins: Int
)

/*References

Gaur, A., 2025. Room Database in Android. (Version 10.0) [Source Code]. Available at: < https://medium.com/@anandgaur2207/room-database-in-android-d5f279d4648a > [Accessed 23 October 2026]
Android Developers, 2026. Save data in a local database using Room. (Version 10.0) [Source Code]. Available at: < https://developer.android.com/training/data-storage/room > [Accessed 23 October 2026]

 */