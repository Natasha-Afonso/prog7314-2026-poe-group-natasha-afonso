package com.example.prjmetrix

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ClothingUiState(
    val clothing: List<ClothingItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
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

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

            repository.getClothing()

                .onSuccess { clothing ->

                    _uiState.value =
                        ClothingUiState(
                            clothing = clothing,
                            isLoading = false
                        )
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

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )

            repository.addClothing(
                name,
                category,
                imageUri
            )

                .onSuccess {

                    loadClothing()
                }

                .onFailure { error ->

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            errorMessage =
                                error.message
                                    ?: "Could not add clothing."
                        )
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

                    _uiState.value =
                        _uiState.value.copy(
                            errorMessage =
                                error.message
                                    ?: "Could not delete clothing."
                        )
                }
        }
    }

    fun clearError() {

        _uiState.value =
            _uiState.value.copy(
                errorMessage = null
            )
    }
}