package com.example.prjmetrix

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Collections.list

//data class for journal state values
data class JournalUiState(
    val entries: List<JournalResponse> = emptyList(),
    val isLoading: Boolean = false, // for the loading spinner
    val errorMessage: String? = null, //error messages
    val isSaving: Boolean = false //if the entry is being saved
)

//(Easy Tuto, 2024)
class JournalViewModel(private val repository: JournalRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    fun loadEntries() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getAll() //displays all the users entries in index list
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            entries = list
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { s ->
                        s.copy(
                            isLoading = false,
                            errorMessage = e.message
                        )
                    }
                }
        }
    }

    fun updateEntry(id: Int, mood: String, text: String, onDone: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            repository.update(id, mood, text) //calls the update function to update a journal entry
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSaving = false
                        )
                    }
                    loadEntries()
                    onDone()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            errorMessage = e.message
                        )
                    }
                }
        }
    }

    fun createEntry(mood: String, text: String, onDone: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            repository.create(text, mood) //calls create function where users enters their text and mood
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isSaving = false
                        )
                    }
                    loadEntries()
                    onDone()
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message
                        )
                    }
                }
        }
    }

    fun deleteEntry(id: Int) {
        viewModelScope.launch {
            repository.delete(id) //delete an specific entry chosen by user
                .onSuccess { loadEntries()}
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message
                        )
                    }
                }
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