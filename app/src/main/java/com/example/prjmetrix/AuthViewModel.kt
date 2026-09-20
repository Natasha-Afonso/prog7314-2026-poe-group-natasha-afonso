package com.example.prjmetrix

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

//data class for authentication view model
data class AuthUiState(
    val isLoading: Boolean = false, // for the loading spinner
    val errorMessage: String? = null, //error messages
    val isAuthenticated: Boolean = false, //check if user is authenticated or not
    val isCheckingAuth: Boolean = true //checks if user has a token to continue in the app home
)

//(Easy Tuto, 2024)
class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init { //checks if user is logged in
        checkAuthStatus()
    }
    //checking if the user has been logged in before- it will check for saved token
    private fun checkAuthStatus() {
        viewModelScope.launch {
            val loggedIn = repository.isLoggedIn()
            _uiState.update { it.copy(isCheckingAuth = false, isAuthenticated = loggedIn) }
        }
    }
    //function to sign in takes email and password, error message displays if password field is blank
    fun signIn(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please enter your email and password.") }
            return
        }
        //starts the login request to enter the app after sign in
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.signIn(email, password) //authenticates the users details
                .onSuccess { _uiState.update { s -> s.copy(isLoading = false, isAuthenticated = true) } }
                .onFailure { e -> _uiState.update { s -> s.copy(isLoading = false, errorMessage = e.message) } }
        }
    }

    //function for sign up
    fun signUp(email: String, password: String) {
        when { //if fields are empty then error messages appears
            email.isBlank() || password.isBlank() -> {
                _uiState.update { it.copy(errorMessage = "Please fill in every field.") }
                return
            }
            password.length < 6 -> { //password should be a minimum of 6 characters
                _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters.") }
                return
            }
        }
        //changing the authentication status on successfull sign up
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.signUp(email, password)
                .onSuccess { _uiState.update { s -> s.copy(isLoading = false, isAuthenticated = true) } }
                .onFailure { e -> _uiState.update { s -> s.copy(isLoading = false, errorMessage = e.message) } }
        }
    }

    //google SSO loads while third party authentication loads
    fun signInWithGoogle(webClientId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.signInWithGoogle(webClientId) //getting the google ID to send to firebase
                .onSuccess { _uiState.update { s -> s.copy(isLoading = false, isAuthenticated = true) } }
                .onFailure { e -> _uiState.update { s -> s.copy(isLoading = false, errorMessage = e.message) } }
        }
    }

    //logs out of firebase
    fun signOut() {
        viewModelScope.launch {
            repository.signOut() //authentication check goes back to false
            _uiState.update{it.copy(isAuthenticated = false, errorMessage = null, isLoading = false)}
        }
    }

    //clears the error messages
    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
}
/*References
*
* Easy Tuto, 2024. All about Firebase Authentication | Login & Signup | Jetpack Compose. [video online] [source code]. Available at: < https://youtu.be/KOnLpNZ4AFc?si=EzwUWn7z8ZZkb4aU > [Accessed 18 September 2026]
*
*
* */