package com.example.prjmetrix.pages

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prjmetrix.R
import androidx.compose.foundation.clickable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.example.prjmetrix.AuthViewModel

// Colors matching the XML
val SettingsTitle = Color(0xFF16213E)
val SectionLabel = Color(0xFF43302E)
val MutedGray = Color(0xFFAAB6BF)
val DividerLight = Color(0xFFEEF1F3)
val LogoutRed = Color(0xFFE85B4E)

@Composable
fun SettingsScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    val uiState by authViewModel.uiState.collectAsState()

    Scaffold(
        containerColor = BgBlue
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            // Heading
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 20.dp, bottom = 12.dp)
            ) {
                IconButton(
                    onClick = { },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "Settings",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SettingsTitle,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Subheading
            Text(
                text = "Manage your personal details and update preferences below.",
                fontSize = 15.sp,
                color = Color.Black,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionLabel("ACCOUNT")

            // Account card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.account),
                        contentDescription = "Account",
                        colorFilter = ColorFilter.tint(SectionLabel),
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
                        painter = painterResource(id = R.drawable.next),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            SectionLabel("PREFERENCES")

            // Preferences card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    SettingsRow(
                        iconRes = R.drawable.language,
                        label = "Language",
                        trailingText = "English",
                        tintIcon = false
                    )
                    Divider(color = DividerLight, thickness = 1.dp)
                    SettingsRow(
                        iconRes = R.drawable.themeicon,
                        label = "Themes",
                        tintIcon = true
                    )
                    Divider(color = DividerLight, thickness = 1.dp)

                    // Notifications row with switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.notifications),
                            contentDescription = "Push Notifications",
                            colorFilter = ColorFilter.tint(SectionLabel),
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
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it },
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

            Spacer(modifier = Modifier.height(20.dp))
            SectionLabel("RESOURCES")

            // Resources card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    SettingsRow(
                        iconRes = R.drawable.access,
                        label = "Accessibility",
                        tintIcon = true
                    )
                    Divider(color = DividerLight, thickness = 1.dp)
                    SettingsRow(
                        iconRes = R.drawable.bug,
                        label = "Report Bug",
                        tintIcon = true
                    )
                    Divider(color = DividerLight, thickness = 1.dp)
                    SettingsRow(
                        iconRes = R.drawable.rate,
                        label = "Rate MeTrix",
                        tintIcon = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Log out button
            //(Easy Tuto, 2024)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable{authViewModel.signOut()}
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
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = SectionLabel,
        modifier = Modifier.padding(start = 20.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsRow(
    iconRes: Int,
    label: String,
    trailingText: String? = null,
    tintIcon: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            colorFilter = if (tintIcon) ColorFilter.tint(SectionLabel) else null,
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
                modifier = Modifier.padding(end = 4.dp)
            )
        }
        Image(
            painter = painterResource(id = R.drawable.next),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun SettingsScreenPreview() {
//    SettingsScreen()
//}

/*References
*
* Easy Tuto, 2024. All about Firebase Authentication | Login & Signup | Jetpack Compose. [video online] [source code]. Available at: < https://youtu.be/KOnLpNZ4AFc?si=EzwUWn7z8ZZkb4aU > [Accessed 18 September 2026]
*
*
* */