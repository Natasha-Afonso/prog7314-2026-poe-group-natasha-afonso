package com.example.prjmetrix.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.prjmetrix.BiometricPromptManager
import com.example.prjmetrix.BiometricResponse
import com.example.prjmetrix.BiometricViewModel
import com.example.prjmetrix.findFragmentActivity

//(Vilarino, 2021)
//wraps the whole app (the NavHost) in MainActivity
//when biometric login is ON and the app opens or comes back after 30 seconds the lock screen covers the app
//fingerprint or face is tried first and the 4 digit PIN is the fallback
//onForgotPin signs the user out and sends them to login (done in MainActivity)
@Composable
fun AppLockGate(
    biometricViewModel: BiometricViewModel,
    isLoggedIn: Boolean,
    onForgotPin: () -> Unit,
    content: @Composable () -> Unit
) {

    val state by biometricViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    //watches when the app goes to the background and comes back
    DisposableEffect(lifecycleOwner) {

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> biometricViewModel.onAppBackgrounded()
                Lifecycle.Event.ON_START -> biometricViewModel.onAppForegrounded()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        content()

        //lock screen only shows over the app when the user is logged in and locked
        if (isLoggedIn && state.isLocked) {

            AppLockScreen(
                biometricAvailable = state.biometricEnabled && BiometricPromptManager.isAvailable(context),

                onBiometricClick = {
                    context.findFragmentActivity()?.let { activity ->

                        BiometricPromptManager.prompt(
                            activity = activity,
                            title = "Unlock MeTrix",
                            subtitle = "Confirm it's you",
                            onSuccess = {
                                biometricViewModel.unlock()
                            },
                            onFallback = {
                                //PIN pad is already on the screen
                            }
                        )
                    }
                },

                onVerifyPin = { pin, onDone ->
                    biometricViewModel.verifyPin(pin, onDone)
                },

                onForgotPin = onForgotPin
            )
        }
    }
}


//the lock screen
@Composable
fun AppLockScreen(
    biometricAvailable: Boolean,
    onBiometricClick: () -> Unit,
    onVerifyPin: (String, (BiometricResponse) -> Unit) -> Unit,
    onForgotPin: () -> Unit
) {

    var error by remember {
        mutableStateOf<String?>(null)
    }

    var busy by remember {
        mutableStateOf(false)
    }

    var resetKey by remember {
        mutableStateOf(0)
    }

    //tries the fingerprint or face as soon as the lock screen appears
    LaunchedEffect(Unit) {
        if (biometricAvailable) {
            onBiometricClick()
        }
    }

    //Surface is solid and blocks touches so nothing underneath can be tapped
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = SettingsBgBlue
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),

            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            PinEntry(
                title = "Welcome back",
                subtitle = "Enter your 4-digit PIN",
                error = error,
                enabled = !busy,
                resetKey = resetKey
            ) { pin ->

                busy = true

                onVerifyPin(pin) { result ->
                    busy = false
                    error = pinErrorText(result)
                    resetKey++
                }
            }


            if (biometricAvailable) {

                TextButton(
                    onClick = onBiometricClick
                ) {
                    Text(
                        text = "Use fingerprint or face",
                        color = SectionLabel
                    )
                }
            }


            //forgot PIN signs the user out, they log in again then set a new PIN in settings
            TextButton(
                onClick = onForgotPin
            ) {
                Text(
                    text = "Forgot PIN?",
                    color = LogoutRed
                )
            }
        }
    }
}

/*References
*
* DeveloperJGV, 2024. BiometricAuth. (Version 10.0) [source code]. Available at: < https://github.com/DeveloperJGV/BiometricAuth > [Accessed 24 October 2026]
* Vilarino, F., 2021. Adding a PIN screen with biometric authentication in Jetpack Compose. [source code]. Available at: < https://fvilarino.medium.com/adding-a-pin-screen-with-biometric-authentication-in-jetpack-compose-a9bf7bd8acc9 > [Accessed 24 October 2026]
*
* */