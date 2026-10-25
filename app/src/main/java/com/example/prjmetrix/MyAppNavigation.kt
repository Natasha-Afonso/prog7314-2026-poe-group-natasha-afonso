package com.example.prjmetrix

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prjmetrix.pages.AccessibilityScreen
import com.example.prjmetrix.pages.AppLockGate
import com.example.prjmetrix.pages.LanguageScreen
import com.example.prjmetrix.pages.LoginScreen
import com.example.prjmetrix.pages.SettingsScreen
import com.example.prjmetrix.pages.SignupScreen
import com.example.prjmetrix.pages.ThemesScreen
import com.example.prjmetrix.pages.WardrobeScreen
import com.example.prjmetrix.pages.JournalCover
import com.example.prjmetrix.pages.JournalEntry
import com.example.prjmetrix.pages.JournalList
import com.example.prjmetrix.pages.ProfileScreen
import com.example.prjmetrix.pages.SurveyScaffold
import com.example.prjmetrix.pages.WelcomeScreen
import androidx.navigation.compose.currentBackStackEntryAsState
//(Easy Tuto, 2024)
@Composable
fun MyAppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current
    val profileViewModel : ProfileViewModel = viewModel(factory = ProfileViewModelFactory(context))
    val authViewModel : AuthViewModel = viewModel(factory = AuthView2(context))
    val uiState by authViewModel.uiState.collectAsState() //getting the state so we user does not have to login everytime they open the app after login
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewFact(context)
    )
    val settingsState by settingsViewModel.uiState.collectAsState()
    val currentSettings = settingsState.settings

    //(Saeed, 2019)
    //viewModel() is tied to the activity so the lock screen and the settings screen share the same biometric state
    val biometricViewModel: BiometricViewModel = viewModel(factory = BiometricView2(context))

    //chacks if the user has a saved token to continue to home else they go to login
    if(uiState.isCheckingAuth) {
        Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center)
        {
            CircularProgressIndicator()
        }
        return
    }

    //handles the login and logout ui clash
    LaunchedEffect(uiState.isAuthenticated,uiState.isNewUser) {
        val target = when {
            !uiState.isAuthenticated -> "login"
            uiState.isNewUser -> "welcome"
            else -> "home"
        }
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
            biometricViewModel.loadStatus() //gets if the user has a PIN and if biometric is on from the database
        }
    }


    //the starting screen of the app should be the home if not the user goes to login
//    val startDestination =
//    if(uiState.isAuthenticated) "journalcover" else "login"


    val startDestination = when {
        !uiState.isAuthenticated -> "login"
        uiState.isNewUser -> "welcome"
        else -> "home"
    }

    // Apply the user's theme and accessibility settings to the whole app
    AppTheme(
        theme = currentSettings.theme,
        fontSize = currentSettings.fontSize,
        highContrast = currentSettings.highContrast
    ) {

        //(Vilarino, 2021)
        AppLockGate(
            biometricViewModel = biometricViewModel,
            isLoggedIn = uiState.isAuthenticated,

            //forgot PIN signs the user out, the LaunchedEffect above then sends them to login
            //after they log in they go to settings and create a new PIN
            onForgotPin = {
                biometricViewModel.onLoggedOut()
                authViewModel.signOut()
            }
        ) {

//navigation code for each screen
            Box(modifier = Modifier.fillMaxSize()) {
                NavHost(
                    navController = navController,
                    startDestination = startDestination,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("login"){
                        LoginScreen(modifier, navController, authViewModel)
                    }
                    composable("signup"){
                        SignupScreen(modifier, navController, authViewModel)
                    }
                    composable("home") {
                        HomeScreen(
                            onSettingsClick = { navController.navigate("settings") },
                            onSessionExpired = {
                                biometricViewModel.onLoggedOut()
                                authViewModel.signOut()
                            }
                        )
                    }

                    //Onboarding: welcome intro, then the profile-setup questions
                    composable  ("welcome") {
                        WelcomeScreen ( onGetStarted = { navController.navigate("survey")} )
                    }

                    composable ("survey") {
                        SurveyScaffold(
                            //onboarding happens for new uses, so going back signs them out again
                            onNavigateUp = { authViewModel.signOut() },
                            onFinished =  {
                                //after onboarding the user is taken to their profile page
                                navController.navigate("profile") {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }


                    // Profile
                    composable("profile") {

                        ProfileScreen(
                            profileViewModel)
                    }

                    //Wardrobe
                    composable("wardrobe") {
                        WardrobeScreen (
                            onPlannerClick = {
                                navController.navigate("planner")
                            }
                        )
                    }

                    // Settings
                    composable("settings") {

                        SettingsScreen(
                            navController,
                            authViewModel,
                            settingsViewModel,
                            biometricViewModel
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
                    composable("journalcover") {
                        JournalCover(onOpenJournal = { navController.navigate("journallist") })
                    }
                    composable("journallist") {
                        JournalList(
                            onBack = { navController.popBackStack() },
                            onWriteEntry = { navController.navigate("journalentry") },
                            onEditEntry = { id -> navController.navigate("journalentry?entryId=$id") }
                        )
                    }
                    composable(
                        route = "journalentry?entryId={entryId}",
                        arguments = listOf(navArgument("entryId") {
                            type = NavType.IntType
                            defaultValue = -1
                        })
                    ) { backStackEntry ->
                        val idArg = backStackEntry.arguments?.getInt("entryId") ?: -1
                        JournalEntry(
                            entryId = if (idArg == -1) null else idArg,
                            onDone = { navController.popBackStack() }
                        )
                    }
                }

                if (
                    currentRoute == "home" ||
                    currentRoute == "wardrobe" ||
                    currentRoute == "journalcover" ||
                    currentRoute == "profile"
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 40.dp)
                    ) {
                        BottomNavigation(
                            currentRoute = currentRoute,
                            onNavigate = { route ->
                                navController.navigate(route) {
                                    popUpTo("home") {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/*References
*
* Easy Tuto, 2024. All about Firebase Authentication | Login & Signup | Jetpack Compose. [video online] [source code]. Available at: < https://youtu.be/KOnLpNZ4AFc?si=EzwUWn7z8ZZkb4aU > [Accessed 18 September 2026]
* Saeed, M., 2019. ViewModel with ViewModelProvider.Factpry: The creator of ViewModel. (Version 10.0) [source code]. Available at: < https://medium.com/koderlabs/viewmodel-with-viewmodelprovider-factory-the-creator-of-viewmodel-8fabfec1aa4f > [Accessed 19 September 2026]
* Vilarino, F., 2021. Adding a PIN screen with biometric authentication in Jetpack Compose. [source code]. Available at: < https://fvilarino.medium.com/adding-a-pin-screen-with-biometric-authentication-in-jetpack-compose-a9bf7bd8acc9 > [Accessed 24 October 2026]
*
* */