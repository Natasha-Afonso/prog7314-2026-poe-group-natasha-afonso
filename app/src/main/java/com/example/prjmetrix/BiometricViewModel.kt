package com.example.prjmetrix

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//data class for biometric state values
data class BiometricUiState(
    val hasPin: Boolean = false, //if the user has created a fallback PIN
    val biometricEnabled: Boolean = false, //on or off biometric authentication in settings
    val isLocked: Boolean = false, //true when the lock screen is showing
    val isSaving: Boolean = false, //if the PIN is being saved
    val errorMessage: String? = null //error messages
)

//(Easy Tuto, 2024)
class BiometricViewModel(private val repository: BiometricRepository) : ViewModel() {

    //(Guell, 2021)
    //if biometric was on when the app closed then the lock screen shows as soon as it opens again
    private val _uiState = MutableStateFlow(
        BiometricUiState(
            biometricEnabled = repository.cachedBiometricEnabled,
            isLocked = repository.cachedBiometricEnabled
        )
    )
    val uiState: StateFlow<BiometricUiState> = _uiState.asStateFlow()

    private var backgroundedAt = 0L //time the app went into the background

    //(Sharma, 2024)
    fun loadStatus() { //gets if the user has a PIN and if biometrics is on, called after login and when settings opens
        viewModelScope.launch {
            repository.getStatus()
                .onSuccess { status ->
                    repository.cachedBiometricEnabled = status.biometricEnabled
                    _uiState.update {
                        it.copy(
                            hasPin = status.hasPin,
                            biometricEnabled = status.biometricEnabled,
                            isLocked = it.isLocked && status.biometricEnabled
                        )
                    }
                }
                .onFailure {
                    //offline so the saved values on the phone are kept
                }
        }
    }

    //turns biometric login on or off, onDone tells the screen if it worked
    fun setBiometric(enabled: Boolean, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            repository.setBiometric(enabled) //calls the function to save the switch in the database
                .onSuccess { status ->
                    repository.cachedBiometricEnabled = status.biometricEnabled
                    _uiState.update {
                        it.copy(
                            hasPin = status.hasPin,
                            biometricEnabled = status.biometricEnabled
                        )
                    }
                    onDone(true)
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            errorMessage = e.message
                        )
                    }
                    onDone(false)
                }
        }
    }

    //(Alpasian, 2023)
    //creates or changes the PIN, errors are turned into a BiometricResponse so the PIN screen can display the message
    fun savePin(newPin: String, onDone: (BiometricResponse) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            repository.savePin(newPin)
                .onSuccess { result ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            hasPin = it.hasPin || result.success
                        )
                    }
                    onDone(result)
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = e.message
                        )
                    }
                    onDone(BiometricResponse(success = false, message = e.message))
                }
        }
    }

    //checks the PIN entered on the lock screen, the app unlocks if its correct
    fun verifyPin(pin: String, onDone: (BiometricResponse) -> Unit) {
        viewModelScope.launch {
            repository.verifyPin(pin)
                .onSuccess { result ->
                    if (result.success) {
                        unlock()
                    }
                    onDone(result)
                }
                .onFailure { e ->
                    onDone(BiometricResponse(success = false, message = e.message))
                }
        }
    }

    fun unlock() = _uiState.update { it.copy(isLocked = false) }

    //(Guell, 2021)
    //app locks again if it was in the background for 30 seconds or more
    fun onAppBackgrounded() {
        backgroundedAt = SystemClock.elapsedRealtime()
    }

    fun onAppForegrounded() {
        val away = SystemClock.elapsedRealtime() - backgroundedAt
        if (backgroundedAt != 0L && away >= LOCK_AFTER_MS && _uiState.value.biometricEnabled) {
            _uiState.update { it.copy(isLocked = true) }
        }
        backgroundedAt = 0L
    }

    //clears everything when the user logs out or taps forgot PIN
    fun onLoggedOut() {
        repository.clearCache()
        _uiState.value = BiometricUiState()
    }

    //clears the error messages
    fun clearError() = _uiState.update { it.copy(errorMessage = null) }

    private companion object {
        const val LOCK_AFTER_MS = 30_000L
    }
}

/*References
*
* Alpasian, E., 2023. DiaryApp. (Version 10.0) [Source code]. Available at: < https://github.com/erenalpaslan/DiaryApp > [Accessed 19 September 2026]
* Easy Tuto, 2024. All about Firebase Authentication | Login & Signup | Jetpack Compose. [video online] [source code]. Available at: < https://youtu.be/KOnLpNZ4AFc?si=EzwUWn7z8ZZkb4aU > [Accessed 18 September 2026]
* Sharma, S., 2024. Mood-Journal. (Version 10.0) [Source code]. Available at: < https://github.com/SakshamSharma2026/Mood-Journal > [Accessed 21 September 2026]
* Guell, F., V., 2021. Adding a PIN screen with biometric authentication in Jetpack Compose. [source code]. Available at: < https://fvilarino.medium.com/adding-a-pin-screen-with-biometric-authentication-in-jetpack-compose-a9bf7bd8acc9 > [Accessed 24 October 2026]
*
*/