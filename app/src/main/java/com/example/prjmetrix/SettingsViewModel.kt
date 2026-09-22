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

//(Easy Tuto, 2024)
class SettingsViewModel(
    private val repository: SettingsRepository
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

            repository.getSettings()
                .onSuccess { settings ->
                    _uiState.value = SettingsUiState(
                        settings = settings,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message
                            ?: "Could not load settings."
                    )
                }
        }
    }


    // Update push notifications
    fun updateNotifications(enabled: Boolean) {
        viewModelScope.launch {

            // Show the new value immediately
            _uiState.value = _uiState.value.copy(
                settings = _uiState.value.settings.copy(
                    pushNotifications = enabled
                ),
                errorMessage = null
            )

            repository.updateNotifications(enabled)
                .onSuccess { settings ->

                    _uiState.value = _uiState.value.copy(
                        settings = settings,
                        errorMessage = null
                    )
                }
                .onFailure { error ->

                    _uiState.value = _uiState.value.copy(
                        errorMessage = error.message
                            ?: "Could not update notifications."
                    )
                }
        }
    }


    // Update language
    fun updateLanguage(language: String) {
        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                errorMessage = null
            )

            repository.updateLanguage(language)
                .onSuccess { settings ->

                    _uiState.value = _uiState.value.copy(
                        settings = settings,
                        errorMessage = null
                    )
                }
                .onFailure { error ->

                    _uiState.value = _uiState.value.copy(
                        errorMessage = error.message
                            ?: "Could not update language."
                    )
                }
        }
    }


    // Update theme
    fun updateTheme(theme: String) {
        viewModelScope.launch {

            _uiState.value = _uiState.value.copy(
                settings = _uiState.value.settings.copy(
                    theme = theme
                ),

                errorMessage = null
            )

            repository.updateTheme(theme)
                .onSuccess { settings ->

                    _uiState.value = _uiState.value.copy(
                        settings = settings,
                        errorMessage = null
                    )
                }
                .onFailure { error ->

                    _uiState.value = _uiState.value.copy(
                        errorMessage = error.message
                            ?: "Could not update theme."
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

            _uiState.value = _uiState.value.copy(
                settings = _uiState.value.settings.copy(
                    fontSize = fontSize
                ),
                errorMessage = null
            )

            repository.updateAccessibility(
                fontSize = fontSize,
                highContrast = highContrast
            )
                .onSuccess { settings ->

                    _uiState.value = _uiState.value.copy(
                        settings = settings,
                        errorMessage = null
                    )
                }
                .onFailure { error ->

                    _uiState.value = _uiState.value.copy(
                        errorMessage = error.message
                            ?: "Could not update accessibility settings."
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

            _uiState.value = _uiState.value.copy(
                errorMessage = null
            )

            repository.submitBug(
                title = title,
                description = description
            )
                .onSuccess {

                    // Bug report submitted successfully.
                    _uiState.value = _uiState.value.copy(
                        errorMessage = null
                    )
                }
                .onFailure { error ->

                    _uiState.value = _uiState.value.copy(
                        errorMessage = error.message
                            ?: "Could not submit bug report."
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

            _uiState.value = _uiState.value.copy(
                errorMessage = null
            )

            repository.submitRating(
                rating = rating,
                comment = comment
            )
                .onSuccess {

                    // Rating submitted successfully.
                    _uiState.value = _uiState.value.copy(
                        errorMessage = null
                    )
                }
                .onFailure { error ->

                    _uiState.value = _uiState.value.copy(
                        errorMessage = error.message
                            ?: "Could not submit rating."
                    )
                }
        }
    }
    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}
/*References
*
* Easy Tuto, 2024. All about Firebase Authentication | Login & Signup | Jetpack Compose. [video online] [source code]. Available at: < https://youtu.be/KOnLpNZ4AFc?si=EzwUWn7z8ZZkb4aU > [Accessed 18 September 2026]
*
*
* */


