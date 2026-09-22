package com.example.prjmetrix.pages



import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.prjmetrix.AuthViewModel
import com.example.prjmetrix.R
import com.example.prjmetrix.SettingsUiState
import com.example.prjmetrix.SettingsViewModel
import kotlinx.coroutines.launch


// Colours used on the Settings screen
val SettingsTitle = Color(0xFF16213E)
val SectionLabel = Color(0xFF43302E)
val MutedGray = Color(0xFFAAB6BF)
val DividerLight = Color(0xFFEEF1F3)
val LogoutRed = Color(0xFFE85B4E)
val SettingsBgBlue = Color(0xFFF5F7FA)


// This version connects the screen to the ViewModel
//(Easy Tuto, 2024)
@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
    settingsViewModel: SettingsViewModel
) {
//(Dawe, 2024)
    val uiState by settingsViewModel.uiState.collectAsState()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()

    // Load the user's settings when the screen opens
    LaunchedEffect(Unit) {
        settingsViewModel.loadSettings()
    }

    SettingsContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,

        onBack = {
            navController.popBackStack()
        },

        onAccountClick = {
            navController.navigate("account")
        },

        onLanguageClick = {
            navController.navigate("language")
        },

        onThemeClick = {
            navController.navigate("themes")
        },

        onAccessibilityClick = {
            navController.navigate("accessibility")
        },

        onNotificationsToggle = { enabled ->
            settingsViewModel.updateNotifications(enabled)
        },

        onSubmitBug = { title, description ->

            settingsViewModel.submitBug(
                title,
                description
            )

            scope.launch {
                snackbarHostState.showSnackbar(
                    "Bug report submitted - thanks!"
                )
            }
        },

        onSubmitRating = { rating, comment ->

            settingsViewModel.submitRating(
                rating,
                comment
            )

            scope.launch {
                snackbarHostState.showSnackbar(
                    "Thanks for rating MeTrix!"
                )
            }
        },

        onLogOut = {
            authViewModel.signOut()
            navController.navigate("login")
            {
                popUpTo(navController.graph.id)
                {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    )
}


// Stateless Settings screen
@Composable
fun SettingsContent(
    uiState: SettingsUiState = SettingsUiState(),
    snackbarHostState: SnackbarHostState = remember {
        SnackbarHostState()
    },
    onBack: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onLanguageClick: () -> Unit = {},
    onThemeClick: () -> Unit = {},
    onAccessibilityClick: () -> Unit = {},
    onNotificationsToggle: (Boolean) -> Unit = {},
    onSubmitBug: (String, String) -> Unit = { _, _ -> },
    onSubmitRating: (Int, String?) -> Unit = { _, _ -> },
    onLogOut: () -> Unit = {}
) {

    var showBugDialog by remember {
        mutableStateOf(false)
    }

    var showRatingDialog by remember {
        mutableStateOf(false)
    }


    Scaffold(
        containerColor = SettingsBgBlue,

        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            ) {
                Snackbar(it)
            }
        }

    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(bottom = 24.dp)
        ) {

            // Heading
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(
                        top = 20.dp,
                        bottom = 12.dp
                    )
            ) {

                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(
                        Alignment.CenterStart
                    )
                ) {

                    Image(
                        painter = painterResource(
                            id = R.drawable.back_arrow
                        ),
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                }


                Text(
                    text = "Settings",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SettingsTitle,
                    modifier = Modifier.align(
                        Alignment.Center
                    )
                )
            }


            // Subheading
            Text(
                text = "Manage your personal details and update preferences below.",
                fontSize = 15.sp,
                color = Color.Black,
                modifier = Modifier.padding(
                    horizontal = 20.dp,
                    vertical = 4.dp
                )
            )


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            SectionLabel("ACCOUNT")


            // Account card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable {
                        onAccountClick()
                    }
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(
                            id = R.drawable.account
                        ),
                        contentDescription = "Account",
                        colorFilter = ColorFilter.tint(
                            SectionLabel
                        ),
                        modifier = Modifier.size(22.dp)
                    )


                    Text(
                        text = "Angela Natty",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SettingsTitle,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 14.dp)
                    )


                    Image(
                        painter = painterResource(
                            id = R.drawable.next
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            SectionLabel("PREFERENCES")


            // Preferences card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {

                Column {

                    SettingsRow(
                        iconRes = R.drawable.language,
                        label = "Language",
                        trailingText = uiState.settings.language,
                        tintIcon = false,
                        onClick = onLanguageClick
                    )


                    HorizontalDivider(
                        color = DividerLight,
                        thickness = 1.dp
                    )


                    SettingsRow(
                        iconRes = R.drawable.themeicon,
                        label = "Themes",
                        trailingText = uiState.settings.theme,
                        tintIcon = true,
                        onClick = onThemeClick
                    )


                    HorizontalDivider(
                        color = DividerLight,
                        thickness = 1.dp
                    )


                    // Push notification setting
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 18.dp,
                                vertical = 14.dp
                            ),

                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Image(
                            painter = painterResource(
                                id = R.drawable.notifications
                            ),
                            contentDescription = "Push Notifications",
                            colorFilter = ColorFilter.tint(
                                SectionLabel
                            ),
                            modifier = Modifier.size(22.dp)
                        )


                        Text(
                            text = "Push Notifications",
                            fontSize = 17.sp,
                            color = SettingsTitle,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 14.dp)
                        )


                        Switch(
                            checked = uiState.settings.pushNotifications,

                            onCheckedChange =
                                onNotificationsToggle,

                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SectionLabel,
                                uncheckedThumbColor = SectionLabel,
                                uncheckedTrackColor = DividerLight
                            )
                        )
                    }
                }
            }


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            SectionLabel("RESOURCES")


            // Resources card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {

                Column {

                    SettingsRow(
                        iconRes = R.drawable.access,
                        label = "Accessibility",
                        trailingText = uiState.settings.fontSize,
                        tintIcon = true,
                        onClick = onAccessibilityClick
                    )


                    HorizontalDivider(
                        color = DividerLight,
                        thickness = 1.dp
                    )


                    SettingsRow(
                        iconRes = R.drawable.bug,
                        label = "Report Bug",
                        tintIcon = true,
                        onClick = {
                            showBugDialog = true
                        }
                    )


                    HorizontalDivider(
                        color = DividerLight,
                        thickness = 1.dp
                    )


                    SettingsRow(
                        iconRes = R.drawable.rate,
                        label = "Rate MeTrix",
                        tintIcon = true,
                        onClick = {
                            showRatingDialog = true
                        }
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(20.dp)
            )


            // Log out card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable {
                        onLogOut()
                    }
            ) {

                Text(
                    text = "Log Out",
                    color = LogoutRed,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }


    // Report bug dialog
    if (showBugDialog) {

        ReportBugDialog(
            onDismiss = {
                showBugDialog = false
            },

            onSubmit = { title, description ->

                onSubmitBug(
                    title,
                    description
                )

                showBugDialog = false
            }
        )
    }


    // Rating dialog
    if (showRatingDialog) {

        RateAppDialog(
            onDismiss = {
                showRatingDialog = false
            },

            onSubmit = { rating, comment ->

                onSubmitRating(
                    rating,
                    comment
                )

                showRatingDialog = false
            }
        )
    }
}


// Report bug dialog
@Composable
private fun ReportBugDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, String) -> Unit
) {

    var title by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }


    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text("Report a bug")
        },

        text = {

            Column {

                OutlinedTextField(
                    value = title,

                    onValueChange = {
                        title = it
                    },

                    label = {
                        Text("What went wrong?")
                    },

                    modifier = Modifier.fillMaxWidth()
                )


                Spacer(
                    modifier = Modifier.height(8.dp)
                )


                OutlinedTextField(
                    value = description,

                    onValueChange = {
                        description = it
                    },

                    label = {
                        Text("Steps to reproduce (optional)")
                    },

                    modifier = Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = {
                    onSubmit(
                        title,
                        description
                    )
                },

                enabled = title.isNotBlank()
            ) {
                Text("Submit")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}


// Rate app dialog
@Composable
private fun RateAppDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int, String?) -> Unit
) {

    var rating by remember {
        mutableStateOf(5)
    }

    var comment by remember {
        mutableStateOf("")
    }


    AlertDialog(

        onDismissRequest = onDismiss,

        title = {
            Text("Rate MeTrix")
        },

        text = {

            Column {

                // The stars are centred using horizontalArrangement
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    (1..5).forEach { star ->

                        Text(
                            text = if (star <= rating) {
                                "\u2605"
                            } else {
                                "\u2606"
                            },

                            fontSize = 28.sp,

                            color = if (star <= rating) {
                                SectionLabel
                            } else {
                                MutedGray
                            },

                            modifier = Modifier
                                .clickable {
                                    rating = star
                                }
                                .padding(4.dp)
                        )
                    }
                }


                Spacer(
                    modifier = Modifier.height(12.dp)
                )


                OutlinedTextField(
                    value = comment,

                    onValueChange = {
                        comment = it
                    },

                    label = {
                        Text("Comments (optional)")
                    },

                    modifier = Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {

            TextButton(
                onClick = {
                    onSubmit(
                        rating,
                        comment.ifBlank {
                            null
                        }
                    )
                }
            ) {
                Text("Submit")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}


// Section heading
@Composable
fun SectionLabel(text: String) {

    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = SectionLabel,
        modifier = Modifier.padding(
            start = 20.dp,
            bottom = 8.dp
        )
    )
}


// Reusable settings row
@Composable
fun SettingsRow(
    iconRes: Int,
    label: String,
    trailingText: String? = null,
    tintIcon: Boolean = false,
    onClick: () -> Unit = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 18.dp,
                vertical = 16.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(
                id = iconRes
            ),

            contentDescription = label,

            colorFilter =
                if (tintIcon) {
                    ColorFilter.tint(SectionLabel)
                } else {
                    null
                },

            modifier = Modifier.size(22.dp)
        )


        Text(
            text = label,
            fontSize = 17.sp,
            color = SettingsTitle,

            modifier = Modifier
                .weight(1f)
                .padding(start = 14.dp)
        )


        if (trailingText != null) {

            Text(
                text = trailingText,
                fontSize = 16.sp,
                color = MutedGray,

                modifier = Modifier.padding(
                    end = 4.dp
                )
            )
        }


        Image(
            painter = painterResource(
                id = R.drawable.next
            ),

            contentDescription = null,

            modifier = Modifier.size(18.dp)
        )
    }
}


/*References
*Dawe, L. 2024.ViewModel Event Publishing in Jetpack Compose Using Flows. [source code]. Available at:<https://medium.com/@luke.dawe/ui-event-handling-in-jetpack-compose-using-flows-for-snackbars-30c3fb163278> [Accessed 19 September 2026]
* Easy Tuto, 2024. All about Firebase Authentication | Login & Signup | Jetpack Compose. [video online] [source code]. Available at: < https://youtu.be/KOnLpNZ4AFc?si=EzwUWn7z8ZZkb4aU > [Accessed 18 September 2026]
*
*
* */
