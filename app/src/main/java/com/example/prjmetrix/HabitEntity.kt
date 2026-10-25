
package com.example.prjmetrix

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

// Represents the habits table in the Room database.
// Stores habits locally so they remain available offline.
@Entity(tableName = "habits")
data class HabitEntity(
    // Unique ID assigned by the local database.
    @PrimaryKey(autoGenerate = true) val localId: Int = 0,

    // ID assigned by the API, null until the habit is uploaded.
    @ColumnInfo(name = "server_id") val serverId: Int? = null,
    val label: String,
    @ColumnInfo(name = "icon_key") val iconKey: String = "check",
    @ColumnInfo(name = "color_hex") val colorHex: String = "#29B6F6",
    @ColumnInfo(name = "gold_coins") val goldCoins: Int = 5,

    // Number of times the habit should be completed each day.
    @ColumnInfo(name = "target_count", defaultValue = "1") val targetCount: Int = 1,
    val progress: Float = 0f,

    // Date the current progress belongs to.
    @ColumnInfo(name = "progress_date") val progressDate: String,

    // True when a change still needs to be synced with the API.
    @ColumnInfo(name = "pending_sync") val pendingSync: Boolean = false,

    // Dates when half-goal and full-goal rewards were last given.
    @ColumnInfo(name = "half_rewarded_date") val halfRewardedDate: String? = null,
    @ColumnInfo(name = "full_rewarded_date") val fullRewardedDate: String? = null,

    // Hides deleted habits until the deletion is synced with the API.
    @ColumnInfo(name = "pending_delete", defaultValue = "0") val pendingDelete: Boolean = false
)

/*References
Gaur, A., 2025. Room Database in Android. (Version 10.0) [Source Code]. Available at: < https://medium.com/@anandgaur2207/room-database-in-android-d5f279d4648a > [Accessed 23 October 2026]
Android Developers, n.d. Save data in a local database using Room. (Version 10.0) [Source Code]. Available at: < https://developer.android.com/training/data-storage/room > [Accessed 23 October 2026]
*/
