package com.example.prjmetrix

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val settings: UserSettings = UserSettings(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SettingsViewModel(
    private val api: SettingsApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState


    // Load settings when the Settings screen opens
    fun loadSettings() {

        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {

                val response = api.getSettings()

                if (response.isSuccessful && response.body() != null) {

                    _uiState.value = SettingsUiState(
                        settings = response.body()!!,
                        isLoading = false
                    )

                } else {

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Could not load settings."
                    )
                }

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message
                )
            }
        }
    }


    // Update push notifications
    fun updateNotifications(enabled: Boolean) {

        viewModelScope.launch {

            try {

                val response = api.updateNotifications(enabled)

                if (response.isSuccessful && response.body() != null) {

                    _uiState.value = _uiState.value.copy(
                        settings = response.body()!!,
                        errorMessage = null
                    )

                } else {

                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Could not update notifications."
                    )
                }

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
        }
    }


    // Update language
    fun updateLanguage(language: String) {

        viewModelScope.launch {

            try {

                val response = api.updateLanguage(language)

                if (response.isSuccessful && response.body() != null) {

                    _uiState.value = _uiState.value.copy(
                        settings = response.body()!!,
                        errorMessage = null
                    )
                }

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
        }
    }


    // Update theme
    fun updateTheme(theme: String) {

        viewModelScope.launch {

            try {

                val response = api.updateTheme(theme)

                if (response.isSuccessful && response.body() != null) {

                    _uiState.value = _uiState.value.copy(
                        settings = response.body()!!,
                        errorMessage = null
                    )
                }

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
        }
    }


    // Update accessibility settings
    fun updateAccessibility(
        fontSize: String,
        highContrast: Boolean
    ) {

        viewModelScope.launch {

            try {

                val request = AccessibilitySettingsRequest(
                    fontSize = fontSize,
                    highContrast = highContrast
                )

                val response = api.updateAccessibility(request)

                if (response.isSuccessful && response.body() != null) {

                    _uiState.value = _uiState.value.copy(
                        settings = response.body()!!,
                        errorMessage = null
                    )
                }

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
        }
    }


    // Submit a bug report
    fun submitBug(
        title: String,
        description: String
    ) {

        viewModelScope.launch {

            try {

                val request = BugReportRequest(
                    title = title,
                    description = description
                )

                val response = api.submitBug(request)

                if (!response.isSuccessful) {

                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Could not submit bug report."
                    )
                }

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
        }
    }


    // Submit a rating
    fun submitRating(
        rating: Int,
        comment: String?
    ) {

        viewModelScope.launch {

            try {

                val request = RatingRequest(
                    rating = rating,
                    comment = comment
                )

                val response = api.submitRating(request)

                if (!response.isSuccessful) {

                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Could not submit rating."
                    )
                }

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message
                )
            }
        }
    }
}

