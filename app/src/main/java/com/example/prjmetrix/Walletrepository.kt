package com.example.prjmetrix

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Result of trying to buy something in the shop
enum class PurchaseResult { SUCCESS, NOT_ENOUGH_COINS, UNKNOWN_ITEM }

// What the mystery gift turned out to be
sealed interface GiftResult {
    data class Coins(val amount: Int) : GiftResult
    data class Furniture(val item: FurnitureDef) : GiftResult
}

// REPOSITORY
// Sits between the UI and the DAOs so screens never talk to the database directly (Gaur, 2025).
// This is the one place coins are changed: the home screen reward and the shop both use it.
class WalletRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context.applicationContext)
    private val walletDao = db.walletDao()
    private val furnitureDao = db.furnitureDao()

    companion object {
        const val STARTING_COINS = 1250
    }

    fun observeCoins(): Flow<Int> = walletDao.observeCoins().map { it ?: 0 }

    suspend fun ensureWallet() = walletDao.createIfMissing(STARTING_COINS)

    suspend fun addCoins(amount: Int) = walletDao.add(amount)

    // Opens the mystery gift (3 goals finished in one day): 100 coins, 500 coins, 1000 coins or a
    // piece of furniture from the shop, each equally likely.
    suspend fun openMysteryGift(): GiftResult {
        return when (kotlin.random.Random.nextInt(4)) {
            0 -> GiftResult.Coins(100).also { walletDao.add(100) }
            1 -> GiftResult.Coins(500).also { walletDao.add(500) }
            2 -> GiftResult.Coins(1000).also { walletDao.add(1000) }
            else -> {
                val gift = FurnitureCatalog.items.random()
                furnitureDao.insert(FurnitureEntity(itemKey = gift.key))
                GiftResult.Furniture(gift)
            }
        }
    }

    // Pays for a piece of furniture and puts it in the inventory (shown in edit mode)
    suspend fun buyFurniture(itemKey: String): PurchaseResult {
        val def = FurnitureCatalog.find(itemKey) ?: return PurchaseResult.UNKNOWN_ITEM
        if (walletDao.spend(def.price) == 0) return PurchaseResult.NOT_ENOUGH_COINS
        furnitureDao.insert(FurnitureEntity(itemKey = itemKey))
        return PurchaseResult.SUCCESS
    }
}

/*References
Gaur, A., 2025. Room Database in Android. (Version 10.0) [Source Code]. Available at: < https://medium.com/@anandgaur2207/room-database-in-android-d5f279d4648a > [Accessed 23 October 2026]
Android Developers, 2026. Save data in a local database using Room. (Version 10.0) [Source Code]. Available at: < https://developer.android.com/training/data-storage/room > [Accessed 23 October 2026]
 */