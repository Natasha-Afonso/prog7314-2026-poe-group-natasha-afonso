package com.example.prjmetrix

import android.content.Context
import android.net.Uri

data class  AddedClothing(
    val item: ClothingItem,
    val backgroundRemoved: Boolean,
    val note: String? = null
)
class ClothingRepository(
    private val context: Context
) {

    private val clothingApi =
        NetworkRepository.create(
            context,
            ClothingApi::class.java
        )

    private val fileRepository =
        FileRepository(context)

    private val backgroundRemover =
        BackgroundRemover(context)

    suspend fun getClothing(): Result<List<ClothingItem>> {

        return try {

            val response =
                clothingApi.getClothing()

            if (response.isSuccessful) {

                Result.success(
                    response.body().orEmpty().filterNot {
                        it.isWishlist
                    }
                )

            } else {

                Result.failure(
                    Exception("Could not load wardrobe.")
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun getWishlist(): Result<List<ClothingItem>> {
        return try {
            val response = clothingApi.getWishlist()
            if (response.isSuccessful) {
                Result.success(response.body().orEmpty())
            } else {
                Result.failure(Exception("Could not load wishlist."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }




    suspend fun addClothing(
        name: String,
        category: String,
        imageUri: Uri?
    ): Result<AddedClothing> {

        return try {

            var backgroundRemoved = false
            var note: String? = null

            val imageUrl: String? = if (imageUri != null) {
                val removed = backgroundRemover.removeBackground(imageUri)
                if (removed.isSuccess) {
                    backgroundRemoved = true
                    removed.getOrNull()
                } else {
                    //The remover or the PNG upload failed, keep the original photo
                    note = removed.exceptionOrNull()?.message

                    fileRepository.uploadImage(imageUri).getOrElse { return Result.failure(it) }
                }
            } else {
                null
            }

            val response = clothingApi.addClothing(
                ClothingItem(name = name, category = category, imageUrl = imageUrl)
            )
            if (response.isSuccessful) {
                val item = response.body() ?: throw Exception("No clothing item returned.")
                Result.success(AddedClothing(item, backgroundRemoved, note))

            } else {

                Result.failure(
                    Exception(
                        "Could not add clothing."
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }


    suspend fun addWishlist(
        name: String,
        category: String,
        imageUri: Uri?
    ): Result<ClothingItem> {
        return try {
            val imageUrl = if (imageUri != null) {
                fileRepository.uploadImage(imageUri).getOrElse { return Result.failure(it) }
            } else {
                null
            }
            val response = clothingApi.addWishlist(
                ClothingItem(name = name, category = category, imageUrl = imageUrl, isWishlist = true)
            )
            if (response.isSuccessful) {
                Result.success(response.body() ?: throw Exception("No wishlist item returned."))
            } else {
                Result.failure(Exception("Could not add to wishlist."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }




    suspend fun deleteClothing(
        id: Int
    ): Result<Unit> {

        return try {

            val response =
                clothingApi.deleteClothing(id)

            if (response.isSuccessful) {

                Result.success(Unit)

            } else {

                Result.failure(
                    Exception(
                        "Could not delete clothing."
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}