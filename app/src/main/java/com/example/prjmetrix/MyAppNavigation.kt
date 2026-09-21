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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.prjmetrix.pages.JournalCover
import com.example.prjmetrix.pages.JournalEntry
import com.example.prjmetrix.pages.JournalList
import com.example.prjmetrix.pages.LoginScreen
import com.example.prjmetrix.pages.SettingsScreen
import com.example.prjmetrix.pages.SignupScreen

//(Easy Tuto, 2024)
@Composable
fun MyAppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(factory = AuthView2(context))
    val uiState by authViewModel.uiState.collectAsState() //getting the state so we user does not have to login everytime they open the app after login

    //chacks if the user has a saved token to continue to home else they go to login
    if (uiState.isCheckingAuth) {
        Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
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


    //the starting screen of the app should be the home if not the user goes to login
    val startDestination =
    if(uiState.isAuthenticated) "home" else "login"


    //navigation code for each screen
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(modifier, navController, authViewModel)
        }
        composable("signup") {
            SignupScreen(modifier, navController, authViewModel)
        }
            composable("home") {
                HomeScreen(
                    onMoreClick = { navController.navigate("settings") },
                    onSessionExpired = {authViewModel.signOut() }
                )
            }
        composable("settings"){
            SettingsScreen(navController, authViewModel)
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
}
/*References
*
* Easy Tuto, 2024. All about Firebase Authentication | Login & Signup | Jetpack Compose. [video online] [source code]. Available at: < https://youtu.be/KOnLpNZ4AFc?si=EzwUWn7z8ZZkb4aU > [Accessed 18 September 2026]
*
*
* */