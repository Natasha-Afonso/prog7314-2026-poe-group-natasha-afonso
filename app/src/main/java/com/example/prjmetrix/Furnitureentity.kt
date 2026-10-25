package com.example.prjmetrix

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

// Represents the owned_furniture table in the Room database.
// Each row stores one piece of furniture the user owns.
@Entity(tableName = "owned_furniture")
data class FurnitureEntity(
    // Unique ID for each furniture item.
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,

    // Identifies the item in FurnitureCatalog.
    @ColumnInfo(name = "item_key") val itemKey: String,

    // True if placed in the room, false if still in the tray.
    val placed: Boolean = false,

    // Furniture position as a fraction of the available room space.
    @ColumnInfo(name = "pos_x") val posX: Float = 0.5f,
    @ColumnInfo(name = "pos_y") val posY: Float = 0.5f
)

/*References

Gaur, A., 2025. Room Database in Android. (Version 10.0) [Source Code]. Available at: < https://medium.com/@anandgaur2207/room-database-in-android-d5f279d4648a > [Accessed 23 October 2026]
Android Developers, n.d. Save data in a local database using Room. (Version 10.0) [Source Code]. Available at: < https://developer.android.com/training/data-storage/room > [Accessed 23 October 2026]

 */