package com.example.prjmetrix

import android.content.Context
import android.net.Uri

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

    suspend fun getClothing(): Result<List<ClothingItem>> {

        return try {

            val response =
                clothingApi.getClothing()

            if (response.isSuccessful) {

                Result.success(
                    response.body() ?: emptyList()
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

    suspend fun addClothing(
        name: String,
        category: String,
        imageUri: Uri?
    ): Result<ClothingItem> {

        return try {


            val imageUrl =
                if (imageUri != null) {

                    fileRepository
                        .uploadImage(imageUri)
                        .getOrElse {
                            return Result.failure(it)
                        }

                } else {
                    null
                }

            val item = ClothingItem(
                name = name,
                category = category,
                imageUrl = imageUrl
            )

            val response =
                clothingApi.addClothing(item)

            if (response.isSuccessful) {

                Result.success(
                    response.body()
                        ?: throw Exception(
                            "No clothing item returned."
                        )
                )

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