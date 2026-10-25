package com.example.prjmetrix.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.prjmetrix.BiometricResponse

//(Guell, 2021)
private const val PIN_LENGTH = 4

//the 4 dots that fill up as the user types their PIN,
@Composable
fun PinDots(filled: Int, hasError: Boolean = false) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        repeat(PIN_LENGTH) { index ->

            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            hasError -> LogoutRed //Wrong pin entered and the dots turn red
                            index < filled -> SectionLabel
                            else -> MutedGray
                        }
                    )
            )
        }
    }
}


//number pad 0 to 9 with a delete button
@Composable
fun PinPad(
    enabled: Boolean,
    onDigit: (Char) -> Unit,
    onDelete: () -> Unit
) {

    //(Android Developer, 2026)
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("", "0", "\u232B")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        rows.forEach { row ->

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                row.forEach { key ->

                    val isDelete = key == "\u232B"

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .then(
                                if (key.isEmpty()) {
                                    Modifier
                                } else {
                                    Modifier
                                        .background(Color.White)
                                        .clickable(enabled = enabled) {
                                            if (isDelete) {
                                                onDelete()
                                            } else {
                                                onDigit(key[0])
                                            }
                                        }
                                }
                            )
                            .semantics {
                                if (isDelete) {
                                    contentDescription = "Delete"
                                }
                            }
                    ) {

                        Text(
                            text = key,
                            fontSize = 26.sp,
                            color = SettingsTitle
                        )
                    }
                }
            }
        }
    }
}

//(Vladmir, 2026)
@Composable
fun PinEntry(
    title: String,
    subtitle: String? = null,
    error: String? = null,
    enabled: Boolean = true,
    resetKey: Any? = null,
    onComplete: (String) -> Unit
) {

    var pin by remember(resetKey) {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = SettingsTitle
        )

        if (subtitle != null) {

            Text(
                text = subtitle,
                fontSize = 15.sp,
                color = SectionLabel,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(
                    top = 8.dp,
                    start = 24.dp,
                    end = 24.dp
                )
            )
        }


        Spacer(
            modifier = Modifier.height(28.dp)
        )

        PinDots(
            filled = pin.length,
            hasError = error != null
        )

        //error message under the dots
        Text(
            text = error ?: "",
            color = LogoutRed,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 12.dp)
                .height(20.dp)
        )

        Spacer(
            modifier = Modifier.height(24.dp)
        )

        PinPad(
            enabled = enabled,

            onDigit = { digit ->
                if (pin.length < PIN_LENGTH) {
                    pin += digit

                    //PIN is complete once 4 digits are entered
                    if (pin.length == PIN_LENGTH) {
                        val entered = pin
                        pin = ""
                        onComplete(entered)
                    }
                }
            },

            onDelete = {
                pin = pin.dropLast(1)
            }
        )
    }
}


//turns the API response into the message shown under the dots
fun pinErrorText(result: BiometricResponse): String? = when {
    result.success -> null
    result.locked -> "Too many attempts. Try again in ${(result.retryAfterSeconds + 59) / 60} min." //(Coddy, 2026)
    result.attemptsRemaining > 0 -> "${result.message ?: "Incorrect PIN."} ${result.attemptsRemaining} attempts left."
    else -> result.message ?: "Something went wrong."
}


// (Guell, 2021)
//full screen dialog where the user creates or changes their PIN, enter a new PIN then confirm it
@Composable
fun SetPinDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, (BiometricResponse) -> Unit) -> Unit
) {

    var confirming by remember {
        mutableStateOf(false)
    }

    var firstPin by remember {
        mutableStateOf("")
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    var busy by remember {
        mutableStateOf(false)
    }

    var resetKey by remember {
        mutableStateOf(0)
    }


    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {

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
                    title = if (confirming) {
                        "Confirm your PIN"
                    } else {
                        "Create a 4-digit PIN"
                    },

                    subtitle = if (!confirming) {
                        "You'll use this if your fingerprint or face can't be read."
                    } else {
                        null
                    },

                    error = error,
                    enabled = !busy,
                    resetKey = resetKey
                ) { pin ->

                    error = null

                    if (!confirming) {
                        //first PIN entered, now user must confirm it
                        firstPin = pin
                        confirming = true
                    } else if (pin != firstPin) {
                        //the two PINs dont match so they start again
                        error = "PINs didn't match. Try again."
                        firstPin = ""
                        confirming = false
                        resetKey++
                    } else {
                        //PINs match so it gets sent to the API to be hashed and saved
                        busy = true
                        onSubmit(pin) { result ->
                            busy = false
                            if (result.success) {
                                onDismiss()
                            } else {
                                error = pinErrorText(result)
                                firstPin = ""
                                confirming = false
                                resetKey++
                            }
                        }
                    }
                }


                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                TextButton(
                    onClick = onDismiss
                ) {
                    Text(
                        text = "Cancel",
                        color = SectionLabel
                    )
                }
            }
        }
    }
}

/*References
*
* Android Developer, 2026. Use collection in Kotlin. (Version 10.0) [source code]. Available at: < https://developer.android.com/codelabs/basic-android-kotlin-collections#2 > [Accessed 25 October 2026]
* Coddy, 2026. Arithmetic Operators. (Version 10.0) [source code]. Available at: < https://coddy.tech/learn/kotlin/fundamentals/arithmetic_operators > [Accessed 25 October 2026]
* Guell, F., V., 2021. Adding a PIN screen with biometric authentication in Jetpack Compose. [source code]. Available at: < https://fvilarino.medium.com/adding-a-pin-screen-with-biometric-authentication-in-jetpack-compose-a9bf7bd8acc9 > [Accessed 24 October 2026]
* Vladmir, K., 2026. Adnroid pin authentication. (Version 10.0) [source code]. Available at: < https://github.com/Khokhlinvladimir/android-pin-authentication > [Accessed 25 October 2026]
* */