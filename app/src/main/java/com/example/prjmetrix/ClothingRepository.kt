package com.example.prjmetrix



import android.content.Context
import android.net.Uri

class ClothingRepository(private val context: Context) {

    private val api =
        NetworkRepository.create(
            context.applicationContext,
            ClothingApi::class.java
        )

    private val files =
        FileRepository(context.applicationContext)

    suspend fun getClothing(): Result<List<ClothingItem>> =
        api.getClothing().let { response ->

            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(
                    Exception("Could not load wardrobe.")
                )
            }
        }

    suspend fun getWishlist(): Result<List<ClothingItem>> =
        api.getWishlist().let { response ->

            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(
                    Exception("Could not load wishlist.")
                )
            }
        }

    suspend fun addClothing(
        name: String,
        category: String,
        imageUri: Uri?,
        removeBackground: Boolean
    ): Result<ClothingItem> {

        val imageUrl =
            if (imageUri != null) {
                files.uploadImage(
                    imageUri,
                    removeBackground
                ).getOrElse {
                    return Result.failure(it)
                }
            } else {
                null
            }

        return api.addClothing(
            ClothingItem(
                name = name,
                category = category,
                imageUrl = imageUrl
            )
        ).let { response ->

            if (response.isSuccessful) {

                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(
                    Exception("No item returned.")
                )

            } else {

                Result.failure(
                    Exception("Could not add clothing.")
                )
            }
        }
    }

    suspend fun addWishlist(
        name: String,
        imageUri: Uri?
    ): Result<ClothingItem> {

        val imageUrl =
            if (imageUri != null) {
                files.uploadImage(
                    imageUri,
                    false
                ).getOrElse {
                    return Result.failure(it)
                }
            } else {
                null
            }

        return api.addWishlist(
            ClothingItem(
                name = name,
                category = "Wishlist",
                imageUrl = imageUrl,
                isWishlist = true
            )
        ).let { response ->

            if (response.isSuccessful) {

                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(
                    Exception("No item returned.")
                )

            } else {

                Result.failure(
                    Exception("Could not add wishlist item.")
                )
            }
        }
    }

    suspend fun delete(id: Int): Result<Unit> =
        api.deleteClothing(id).let { response ->

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(
                    Exception("Could not delete item.")
                )
            }
        }
}

