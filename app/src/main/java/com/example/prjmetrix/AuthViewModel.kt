package com.example.prjmetrix

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your email and password.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.signIn(email, password)
                .onSuccess { _uiState.update { s -> s.copy(isLoading = false, isAuthenticated = true) } }
                .onFailure { e -> _uiState.update { s -> s.copy(isLoading = false, errorMessage = e.message) } }
        }
    }

    fun signUp(email: String, password: String) {
        when {
            email.isBlank() || password.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Please fill in every field.") }
                return
            }
            password.length < 6 -> {
                _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters.") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.signUp(email, password)
                .onSuccess { _uiState.update { s -> s.copy(isLoading = false, isAuthenticated = true) } }
                .onFailure { e -> _uiState.update { s -> s.copy(isLoading = false, errorMessage = e.message) } }
        }
    }

    fun signInWithGoogle(webClientId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.signInWithGoogle(webClientId)
                .onSuccess { _uiState.update { s -> s.copy(isLoading = false, isAuthenticated = true) } }
                .onFailure { e -> _uiState.update { s -> s.copy(isLoading = false, errorMessage = e.message) } }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            _uiState.value = AuthUiState()
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
}