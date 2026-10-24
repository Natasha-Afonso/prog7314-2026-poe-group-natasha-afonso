package com.example.prjmetrix

import android.content.Context
import androidx.annotation.DrawableRes
import kotlinx.coroutines.flow.Flow

// CATALOG
// Describes every piece of furniture that exists in the app. The database only stores the
// `key`; the image and size come from here. The shop will list these same items.
data class FurnitureDef(
    val key: String,
    val name: String,
    @DrawableRes val drawableRes: Int,
    val widthDp: Int,
    val heightDp: Int
)

object FurnitureCatalog {
    val items = listOf(
        FurnitureDef("blue_lamp", "Blue lamp", R.drawable.blue_lamp, 92, 354)
    )

    fun find(key: String): FurnitureDef? = items.firstOrNull { it.key == key }
}

// REPOSITORY
// Sits between the UI and the DAO so screens never talk to the database directly (Gaur, 2025).
class FurnitureRepository(context: Context) {
    private val appContext = context.applicationContext
    private val dao = AppDatabase.getDatabase(appContext).furnitureDao()

    fun observeFurniture(): Flow<List<FurnitureEntity>> = dao.observeAll()

    // Gives a new user the lamp that used to be hard-coded in the room, once only.
    // The flag stops it coming back after the user has picked it up or sold it.
    suspend fun ensureStarterFurniture() {
        val prefs = appContext.getSharedPreferences("metrix_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("starter_furniture_given", false)) return
        if (dao.count() == 0) {
            dao.insert(FurnitureEntity(itemKey = "blue_lamp", placed = true, posX = 0.09f, posY = 0.72f))
        }
        prefs.edit().putBoolean("starter_furniture_given", true).apply()
    }

    // Tray -> room (centre of the room until the user drags it)
    suspend fun place(id: Int) {
        val item = dao.getById(id) ?: return
        dao.update(item.copy(placed = true, posX = 0.5f, posY = 0.6f))
    }

    // Room -> tray
    suspend fun pickUp(id: Int) {
        val item = dao.getById(id) ?: return
        dao.update(item.copy(placed = false))
    }

    suspend fun move(id: Int, x: Float, y: Float) {
        val item = dao.getById(id) ?: return
        dao.update(item.copy(posX = x.coerceIn(0f, 1f), posY = y.coerceIn(0f, 1f)))
    }

    // Used by the shop later: buying an item adds a row to the inventory
    suspend fun add(itemKey: String) {
        dao.insert(FurnitureEntity(itemKey = itemKey))
    }
}

/*References

Gaur, A., 2025. Room Database in Android. (Version 10.0) [Source Code]. Available at: < https://medium.com/@anandgaur2207/room-database-in-android-d5f279d4648a > [Accessed 23 October 2026]
Android Developers, n.d. Save data in a local database using Room. (Version 10.0) [Source Code]. Available at: < https://developer.android.com/training/data-storage/room > [Accessed 23 October 2026]

 */