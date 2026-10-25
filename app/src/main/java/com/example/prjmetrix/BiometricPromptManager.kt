package com.example.prjmetrix

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

//(Gonzalez, 2024)
//helper that calls the phones fingerprint or face scanner so the screens only call two functions
object BiometricPromptManager {

    private const val AUTHENTICATORS = BiometricManager.Authenticators.BIOMETRIC_WEAK //lets face unlock work on phones that only class it as Class 2

    //true when the phone has a scanner and the user has added a fingerprint or face
    fun isAvailable(context: Context): Boolean =
        BiometricManager.from(context).canAuthenticate(AUTHENTICATORS) == BiometricManager.BIOMETRIC_SUCCESS

    //(Lath, 2024)
    //shows the phones biometric dialog
    fun prompt(
        activity: FragmentActivity,
        title: String,
        subtitle: String,
        negativeText: String = "Use PIN",
        onSuccess: () -> Unit,
        onFallback: (String?) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccess() //finger is recognized
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onFallback(errString.toString()) //when the user taps Use PIN, cancels or the scanner is locked out, then the PIN pad is shown
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(AUTHENTICATORS)
            .setNegativeButtonText(negativeText) //needed when DEVICE_CREDENTIAL is not allowed
            .build()

        BiometricPrompt(activity, executor, callback).authenticate(promptInfo)
    }
}

//BiometricPrompt needs a FragmentActivity but in compose the context can be wrapped so this unwraps it
fun Context.findFragmentActivity(): FragmentActivity? {
    var activity: Context? = this
    while (activity is ContextWrapper) {
        if (activity is FragmentActivity) return activity
        activity = activity.baseContext
    }
    return null
}

/*References
*
*  Gonzalez, J., 2024. BiometricAuth. (Version 10.0) [source code]. Available at: < https://github.com/DeveloperJGV/BiometricAuth/blob/main/app/src/main/java/com/aviva/biometricauth/BiometricPromptManager.kt > [Accessed 24 October 2026]
* Lath, M., 2024. How to Implement Biometric Authentication with Jetpack Compose and AES Encryption. (Version 10.0) [source code]. Available at: < https://blog.canopas.com/how-to-implement-biometric-authentication-with-jetpack-compose-and-aes-encryption-1b72cfccf4d4 > [Accessed 24 October 2026]
* */