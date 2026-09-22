package com.example.prjmetrix

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ClothingUiState(
    val clothing: List<ClothingItem> = emptyList(),
    val wishlist: List<ClothingItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ClothingViewModel(
    private val repository: ClothingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClothingUiState())
    val uiState: StateFlow<ClothingUiState> = _uiState

    fun load() = viewModelScope.launch {

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )

        val clothing = repository.getClothing()
        val wishlist = repository.getWishlist()

        if (clothing.isSuccess && wishlist.isSuccess) {

            _uiState.value = ClothingUiState(
                clothing = clothing.getOrNull() ?: emptyList(),
                wishlist = wishlist.getOrNull() ?: emptyList(),
                isLoading = false
            )

        } else {

            _uiState.value = ClothingUiState(
                clothing = clothing.getOrNull() ?: emptyList(),
                wishlist = wishlist.getOrNull() ?: emptyList(),
                isLoading = false,
                errorMessage =
                    clothing.exceptionOrNull()?.message
                        ?: wishlist.exceptionOrNull()?.message
            )
        }
    }

    fun addClothing(
        name: String,
        category: String,
        uri: Uri?,
        removeBackground: Boolean
    ) = viewModelScope.launch {

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )

        val result = repository.addClothing(
            name = name,
            category = category,
            imageUri = uri,
            removeBackground = removeBackground
        )

        if (result.isSuccess) {
            load()
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = result.exceptionOrNull()?.message
            )
        }
    }

    fun addWishlist(
        name: String,
        uri: Uri?
    ) = viewModelScope.launch {

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )

        val result = repository.addWishlist(
            name = name,
            imageUri = uri
        )

        if (result.isSuccess) {
            load()
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = result.exceptionOrNull()?.message
            )
        }
    }

    fun delete(id: Int) = viewModelScope.launch {

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )

        val result = repository.delete(id)

        if (result.isSuccess) {
            load()
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = result.exceptionOrNull()?.message
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}