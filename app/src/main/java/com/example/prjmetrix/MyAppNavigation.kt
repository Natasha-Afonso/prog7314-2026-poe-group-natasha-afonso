package com.example.prjmetrix

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.prjmetrix.pages.AccessibilityScreen
import com.example.prjmetrix.pages.LanguageScreen
import com.example.prjmetrix.pages.LoginScreen
import com.example.prjmetrix.pages.SettingsScreen
import com.example.prjmetrix.pages.SignupScreen
import com.example.prjmetrix.pages.ThemesScreen

//(Easy Tuto, 2024)
@Composable
fun MyAppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    val context = LocalContext.current
    val authViewModel : AuthViewModel = viewModel(factory = AuthView2(context))
    val uiState by authViewModel.uiState.collectAsState() //getting the state so we user does not have to login everytime they open the app after login
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewFact(context)
    )
    val settingsState by settingsViewModel.uiState.collectAsState()
    val currentSettings = settingsState.settings

    //chacks if the user has a saved token to continue to home else they go to login
    if(uiState.isCheckingAuth) {
        Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center)
        {
            CircularProgressIndicator()
        }
        return
    }

    //handles the login and logout ui clash
    LaunchedEffect(uiState.isAuthenticated) {
        val target = if(uiState.isAuthenticated) "home" else "login"
        val current = navController.currentBackStackEntry?.destination?.route
        if (current != target)
        {
            navController.navigate(target) {
                popUpTo(0) {inclusive = true}
            }
        }
    }

    // Load the user's settings after they log in
    LaunchedEffect(uiState.isAuthenticated) {

        if (uiState.isAuthenticated) {
            settingsViewModel.loadSettings()
        }
    }


    //the starting screen of the app should be the home if not the user goes to login
    val startDestination =
    if(uiState.isAuthenticated) "home" else "login"

    // Apply the user's theme and accessibility settings to the whole app
    AppTheme(
        theme = currentSettings.theme,
        fontSize = currentSettings.fontSize,
        highContrast = currentSettings.highContrast
    ) {


//navigation code for each screen
    NavHost(navController = navController,
        startDestination = startDestination) {
        composable("login"){
            LoginScreen(modifier, navController, authViewModel)
        }
        composable("signup"){
            SignupScreen(modifier, navController, authViewModel)
        }
            composable("home") {
                HomeScreen(
                    onMoreClick = { navController.navigate("settings") },
                    onSessionExpired = {authViewModel.signOut() }
                )
            }

        // Settings
        composable("settings") {

            SettingsScreen(
                navController,
                authViewModel,
                settingsViewModel
            )
        }

        // Language
        composable("language") {

            LanguageScreen(
                navController
            )
        }

        // Themes
        composable("themes") {

            ThemesScreen(
                navController = navController,
                currentTheme = currentSettings.theme,
                onThemeChange = { newTheme ->

                    settingsViewModel.updateTheme(newTheme)
                }
            )
        }

        // Accessibility
        composable("accessibility") {

            AccessibilityScreen(
                navController = navController,
                fontSize = currentSettings.fontSize,
                highContrast = currentSettings.highContrast,

                onFontSizeChange = { newSize ->

                    settingsViewModel.updateAccessibility(
                        fontSize = newSize,
                        highContrast = currentSettings.highContrast
                    )
                },

                onHighContrastChange = { newContrast ->

                    settingsViewModel.updateAccessibility(
                        fontSize = currentSettings.fontSize,
                        highContrast = newContrast
                    )
                }
            )
        }
    }
    }
}

/*References
*
* Easy Tuto, 2024. All about Firebase Authentication | Login & Signup | Jetpack Compose. [video online] [source code]. Available at: < https://youtu.be/KOnLpNZ4AFc?si=EzwUWn7z8ZZkb4aU > [Accessed 18 September 2026]
*
*
* */