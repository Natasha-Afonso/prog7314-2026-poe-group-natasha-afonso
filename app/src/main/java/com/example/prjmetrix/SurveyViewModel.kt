package com.example.prjmetrix

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class SurveyViewModel(private val profileApi: ProfileApi) : ViewModel() {

    var progress by mutableStateOf(1)
        private set
    val question get() = surveyQuestions[progress - 1]

    var isNextEnabled by mutableStateOf(false)
        private set

    // Answers
    var nickname by mutableStateOf("")
        private set
    var personality by mutableStateOf("")
        private set
    var birthday: LocalDate? by mutableStateOf(null)
        private set
    val formattedBirthday: String
        get() = birthday?.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
            ?: "Pick your birthday"

    // Saving state
    var isSaving by mutableStateOf(false)
        private set
    var errorMessage: String? by mutableStateOf(null)
        private set
    var isFinished by mutableStateOf(false)
        private set

    private fun checkIfNextEnabled(): Boolean {
        return when (question.options) {
            SurveyOptions.TextInput -> nickname.isNotBlank()
            is SurveyOptions.SingleChoice -> personality != ""
            SurveyOptions.DateChoice -> birthday != null
        }
    }

    fun nextQuestion() {
        progress += 1
        isNextEnabled = checkIfNextEnabled()
    }

    fun previousQuestion() {
        progress--
        isNextEnabled = checkIfNextEnabled()
    }

    fun onNicknameChange(value: String) {
        nickname = value.take(20)
        isNextEnabled = checkIfNextEnabled()
    }

    fun onPersonalityPicked(value: String) {
        personality = value
        isNextEnabled = checkIfNextEnabled()
    }

    fun onBirthdayPicked(date: LocalDate) {
        birthday = date
        isNextEnabled = checkIfNextEnabled()
    }

    fun clearError() {
        errorMessage = null
    }

    // Called by the "Done" button on the last question
    fun saveProfile() {
        if (isSaving) return
        viewModelScope.launch {
            isSaving = true
            errorMessage = null
            try {
                val response = profileApi.saveProfile(
                    ProfileItemDTO(
                        userName = nickname.trim(),
                        personality = personality,
                        birthday = birthday?.toString()
                    )
                )
                if (response.isSuccessful) {
                    isFinished = true
                } else {
                    errorMessage = "Could not save your profile (${response.code()})."
                }
            } catch (e: IOException) {
                errorMessage = "Cannot reach MeTrix. Check your connection and try again."
            } catch (e: Exception) {
                errorMessage = "Something went wrong: ${e.message}"
            } finally {
                isSaving = false
            }
        }
    }
}


class SurveyViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val profileApi = ProfileRepository(context.applicationContext).api
        return SurveyViewModel(profileApi) as T
    }
}