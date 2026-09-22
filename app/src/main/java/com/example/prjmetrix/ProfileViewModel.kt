package com.example.prjmetrix

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.LocalDate


// FIXED LISTS OF CHOICES


//  Personality -> what the avatar says on the home screen (fun facts, jokes...)
enum class Personality(val label: String) {
    CURIOUS("Curious"),
    FUNNY("Funny"),
    CALM("Calm"),
    AMBITIOUS("Ambitious"),
    CARING("Caring")
}

//  Aspiration  ->decides the 3 habits users start with
enum class Aspiration(val label: String) {
    ORGANISED("To be organised"),
    HEALTHY("To be healthy"),
    RICH("To be rich"),
    CREATIVE("To be creative"),
    RELAXED("To be relaxed")
}



// The default values are placeholders

data class ProfileState(
    val userName: String = "Natty",
    val personality: Personality = Personality.CURIOUS,
    val birthday: LocalDate = LocalDate.of(2003, 9, 15),
    val aspiration: Aspiration = Aspiration.RICH,

    val bodyRef: String = bodyOptions.first().ref,
    val hairRef: String = hairOptions.first().ref,
    val extensionRef: String = extensionOptions.first().ref,
    val eyeColor: String = DEFAULT_EYE_COLOR,
    val mouthRef: String = mouthOptions.first().ref,
    val lipstickRef: String = lipstickOptions.first().ref
)

// API text -> app types. Anything missing or unknown falls back to the default.
// Enum.entries lists (Kotlin, 2026).
fun ProfileItemDTO.toState(): ProfileState {
    val defaults = ProfileState()
    return ProfileState(
        userName = userName.ifBlank { defaults.userName },
        personality = Personality.entries.firstOrNull { it.name == personality } ?: defaults.personality,
        birthday = birthday?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: defaults.birthday,
        aspiration = Aspiration.entries.firstOrNull { it.name == aspiration } ?: defaults.aspiration,
        bodyRef = bodyRef,
        hairRef = hairRef,
        extensionRef = extensionRef,
        eyeColor = eyeColor,
        mouthRef = mouthRef,
        lipstickRef = lipstickRef
    )
}

// App types -> API text (what gets saved)
fun ProfileState.toDto() = ProfileItemDTO(
    userName = userName,
    personality = personality.name,
    birthday = birthday.toString(),          // LocalDate.toString() gives "2003-09-15"
    aspiration = aspiration.name,
    bodyRef = bodyRef,
    hairRef = hairRef,
    extensionRef = extensionRef,
    eyeColor = eyeColor,
    mouthRef = mouthRef,
    lipstickRef = lipstickRef
)

// The screen is always in exactly one of these three states (Android Developers, 2026).
sealed interface ProfileUiState {
    data object Loading : ProfileUiState                          // waiting for the API
    data class Ready(val profile: ProfileState) : ProfileUiState  // show the profile
    data object Error : ProfileUiState                            // could not load
}


class ProfileViewModel(private val profileApi: ProfileApi) : ViewModel() {


    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // saves run one at a time, so the newest choice is always the one that ends up saved
    private val saveMutex = Mutex()

    init {
        load()   // start loading as soon as the screen is opened
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            _uiState.value = try {
                val response = profileApi.getProfile()
                when {
                    // profile found on the server
                    response.isSuccessful -> ProfileUiState.Ready(response.body()?.toState() ?: ProfileState())
                    // 404 = nothing saved yet, so start from the defaults
                    response.code() == 404 -> ProfileUiState.Ready(ProfileState())
                    // anything else (401 expired token, 500 ...) counts as an error
                    else -> ProfileUiState.Error
                }
            } catch (e: Exception) {
                ProfileUiState.Error   // no network / API not running
            }
        }
    }

    // ----- one function per choice the user can make -----
    fun onBodySelected(ref: String) = changeProfile { it.copy(bodyRef = ref) }
    fun onHairSelected(ref: String) = changeProfile { it.copy(hairRef = ref) }
    fun onExtensionSelected(ref: String) = changeProfile { it.copy(extensionRef = ref) }
    fun onEyeColorSelected(hex: String) = changeProfile { it.copy(eyeColor = hex) }
    fun onMouthSelected(ref: String) = changeProfile { it.copy(mouthRef = ref) }
    fun onLipstickSelected(ref: String) = changeProfile { it.copy(lipstickRef = ref) }

    //  updates the state right away, so the avatar changes instantly
    // 2) save the new profile in the background
    private fun changeProfile(transform: (ProfileState) -> ProfileState) {
        _uiState.update { current ->
            if (current is ProfileUiState.Ready) current.copy(profile = transform(current.profile)) else current
        }
        saveProfile()
    }

    private fun saveProfile() {
        viewModelScope.launch {
            saveMutex.withLock {
                // read the state INSIDE the lock so a queued save always sends the latest choices
                val ready = _uiState.value as? ProfileUiState.Ready ?: return@withLock
                try {
                    val response = profileApi.saveProfile(ready.profile.toDto())
                    if (!response.isSuccessful) Log.e("Profile", "Save failed: ${response.code()}")
                } catch (e: Exception) {
                    // the avatar keeps the new choice on screen; the failed save is only logged for now
                    Log.e("Profile", "Could not save profile", e)
                }
            }
        }
    }
}


class ProfileViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val api = ProfileRepository(context.applicationContext).api
        return ProfileViewModel(api) as T
    }
}

/* References
Android Developers, 2026. Recommendations for Android architecture. [online] Available at: < https://developer.android.com/topic/architecture/recommendations > [Accessed 21 September 2026]
Kotlin, 2026. EnumEntries. [online] Available at: < https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.enums/-enum-entries/ > [Accessed 21 September 2026]
*/