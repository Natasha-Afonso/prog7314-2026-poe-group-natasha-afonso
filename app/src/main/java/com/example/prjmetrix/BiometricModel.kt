package com.example.prjmetrix

//(Simplified Coding, 2021)
//data classes must match the DTOs in BiometricDto class in backend
data class BiometricStatus (
    val hasPin: Boolean,
    val biometricEnabled: Boolean
)

data class SavePinRequest (
    val newPin: String
)

data class VerifyPinRequest (
    val pin: String
)

data class BiometricRequest(
    val enabled: Boolean
)

//returned for saving and checking a PIN
data class BiometricResponse(
    val message: String? = null,
    val success: Boolean,
    val locked: Boolean = false,
    val retryAfterSeconds: Int = 0,
    val attemptsRemaining: Int = 0,
)
/*References
*
* Simplified Coding, 2021. #12 Android Login/Signup with MVVM - user Logout [video online] [source code]. Available at: < https://youtu.be/q_5xrPYRoAg?si=gRbHfP1TazLTqbrY > [Accessed 19 September 2026]
*
* */