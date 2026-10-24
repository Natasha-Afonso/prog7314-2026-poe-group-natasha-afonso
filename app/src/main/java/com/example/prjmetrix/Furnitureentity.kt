package com.example.prjmetrix

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

// ROOM ENTITY
// An entity represents a table in the database, and each row in the table corresponds to an
// object of the entity class (Gaur, 2025). Each instance of this class is one row in the
// "owned_furniture" table (Android Developers, n.d.).
// One row = one piece of furniture the user owns. The shop will add rows here later; edit mode
// flips `placed` and moves the position.
@Entity(tableName = "owned_furniture")
data class FurnitureEntity(
    // Every table needs a unique identifier, marked with @PrimaryKey; autoGenerate allows
    // Room to automatically assign unique IDs (Gaur, 2025). This lets the user own the same
    // item more than once.
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,

    // Which catalog item this is (see FurnitureCatalog)
    @ColumnInfo(name = "item_key") val itemKey: String,

    // true = standing in the room, false = still in the inventory tray
    val placed: Boolean = false,

    // Position in the room as a fraction (0..1) of the free space, so it fits every screen size
    @ColumnInfo(name = "pos_x") val posX: Float = 0.5f,
    @ColumnInfo(name = "pos_y") val posY: Float = 0.5f
)

/*References

Gaur, A., 2025. Room Database in Android. (Version 10.0) [Source Code]. Available at: < https://medium.com/@anandgaur2207/room-database-in-android-d5f279d4648a > [Accessed 23 October 2026]
Android Developers, n.d. Save data in a local database using Room. (Version 10.0) [Source Code]. Available at: < https://developer.android.com/training/data-storage/room > [Accessed 23 October 2026]

 */