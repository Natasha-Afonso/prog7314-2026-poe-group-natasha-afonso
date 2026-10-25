package com.example.prjmetrix

import android.content.Context
import androidx.annotation.DrawableRes
import kotlinx.coroutines.flow.Flow

// Stores the details of each furniture item.
data class FurnitureDef(
    val key: String,
    val name: String,
    @DrawableRes val drawableRes: Int,
    val widthDp: Int,
    val heightDp: Int,
    // Price of the item in coins.
    val price: Int = 100,
    // Category shown in the shop.
    val category: String = "Furniture"
)

object FurnitureCatalog {
    val items = listOf(
        FurnitureDef("blue_lamp", "Blue lamp", R.drawable.blue_lamp, 92, 354, price = 120),
        // Adjust these sizes to match the images.
        FurnitureDef("cupboard", "Cupboard", R.drawable.cupboard, 130, 190, price = 50),
        FurnitureDef("bed", "Bed", R.drawable.bed, 220, 140, price = 90)
    )

    // Finds a furniture item by its key.
    fun find(key: String): FurnitureDef? = items.firstOrNull { it.key == key }
}

// Handles furniture data between the UI and database.
class FurnitureRepository(context: Context) {
    private val appContext = context.applicationContext
    private val dao = AppDatabase.getDatabase(appContext).furnitureDao()

    // Observes changes to the furniture list.
    fun observeFurniture(): Flow<List<FurnitureEntity>> = dao.observeAll()

    // Gives a new user the starter lamp only once.
    suspend fun ensureStarterFurniture() {
        val prefs = appContext.getSharedPreferences("metrix_prefs", Context.MODE_PRIVATE)
        if (prefs.getBoolean("starter_furniture_given", false)) return
        if (dao.count() == 0) {
            dao.insert(FurnitureEntity(itemKey = "blue_lamp", placed = true, posX = 0.09f, posY = 0.72f))
        }
        prefs.edit().putBoolean("starter_furniture_given", true).apply()
    }

    // Places furniture in the room.
    suspend fun place(id: Int) {
        val item = dao.getById(id) ?: return
        dao.update(item.copy(placed = true, posX = 0.5f, posY = 0.6f))
    }

    // Moves furniture back to the tray.
    suspend fun pickUp(id: Int) {
        val item = dao.getById(id) ?: return
        dao.update(item.copy(placed = false))
    }

    // Updates the furniture position.
    suspend fun move(id: Int, x: Float, y: Float) {
        val item = dao.getById(id) ?: return
        dao.update(item.copy(posX = x.coerceIn(0f, 1f), posY = y.coerceIn(0f, 1f)))
    }

    // Adds furniture to the inventory.
    suspend fun add(itemKey: String) {
        dao.insert(FurnitureEntity(itemKey = itemKey))
    }
}

/*References
Gaur, A., 2025. Room Database in Android. (Version 10.0) [Source Code]. Available at: < https://medium.com/@anandgaur2207/room-database-in-android-d5f279d4648a > [Accessed 23 October 2026]
Android Developers, n.d. Save data in a local database using Room. (Version 10.0) [Source Code]. Available at: < https://developer.android.com/training/data-storage/room > [Accessed 23 October 2026]
*/