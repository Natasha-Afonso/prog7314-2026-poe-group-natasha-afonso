package com.example.prjmetrix
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ClothingViewFact(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(
                ClothingViewModel::class.java
            )
        ) {

            return ClothingViewModel(
                ClothingRepository(context)
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }

}