package com.example.prjmetrix

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClothingUiState(
    val clothing: List<ClothingItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val wishlist: List<ClothingItem> = emptyList(),
    val isSaving: Boolean = false, //uploading/removing background
    val infoMessage: String? = null
)

class ClothingViewModel(
    private val repository: ClothingRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(ClothingUiState())

    val uiState: StateFlow<ClothingUiState> =
        _uiState

    fun loadClothing() {

        viewModelScope.launch {

            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

                val wardrobe = repository.getClothing()
                val wishlist = repository.getWishlist().getOrDefault(_uiState.value.wishlist)

                    wardrobe
                        .onSuccess { clothing ->
                            _uiState.update {
                                it.copy(clothing = clothing, wishlist = wishlist, isLoading = false)
                            }
                        }



                .onFailure { error ->

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            errorMessage =
                                error.message
                                    ?: "Could not load wardrobe."
                        )
                }
        }
    }

    fun addClothing(
        name: String,
        category: String,
        imageUri: Uri?
    ) {

        viewModelScope.launch {

            _uiState.update {
                it.copy(isSaving = true, errorMessage = null, infoMessage = null)
            }

            repository.addClothing(
                name,
                category,
                imageUri
            )

                .onSuccess { added ->
                    _uiState.update {
                        it.copy(isSaving = false, infoMessage = if(imageUri != null && !added.backgroundRemoved)
                        {
                            "Added with the original photo. ${added.note ?: ""}".trim()
                        }else null
                        )
                    }
                    loadClothing()
                }

                .onFailure { error ->

                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage =
                                error.message
                                    ?: "Could not add clothing."
                        )
                    }
                }
        }
    }

    fun addWishlist(name: String, category: String, imageUri: Uri?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, infoMessage = null) }

            repository.addWishlist(name, category, imageUri)
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false) }
                    loadClothing()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = error.message ?: "Could not add to wishlist."
                        )
                    }
                }
        }
    }

    fun deleteClothing(
        id: Int
    ) {

        viewModelScope.launch {

            repository.deleteClothing(id)

                .onSuccess {

                    loadClothing()
                }

                .onFailure { error ->

                        _uiState.update {
                            it.copy(

                                errorMessage =
                                    error.message
                                        ?: "Could not delete clothing."
                            )
                        }
                }
        }
    }

    fun clearError() {
        _uiState.update {
            it.copy(errorMessage = null)
        }
    }

    fun clearInfo() {
        _uiState.update {
            it.copy(infoMessage = null)
        }
    }
}