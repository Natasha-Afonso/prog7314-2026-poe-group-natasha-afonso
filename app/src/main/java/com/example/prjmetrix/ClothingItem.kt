package com.example.prjmetrix
data class ClothingItem(
    val id: Int = 0,
    val userId: String = "",
    val name: String = "",
    val category: String = "",
    val imageUrl: String? = null,
    val isWishlist: Boolean = false
)