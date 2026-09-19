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
import com.example.prjmetrix.pages.LoginScreen
import com.example.prjmetrix.pages.SettingsScreen
import com.example.prjmetrix.pages.SignupScreen

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    val context = LocalContext.current
    val authViewModel : AuthViewModel = viewModel(factory = AuthView2(context))
    val uiState by authViewModel.uiState.collectAsState() //getting the state so we user does not have to login everytime they open the app after login

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
        val target = if(uiState.isAuthenticated) "settings" else "login"
        val current = navController.currentBackStackEntry?.destination?.route
        if (current != target)
        {
            navController.navigate(target) {
                popUpTo(0) {inclusive = true}
            }
        }
    }


    val startDestination =
    if(uiState.isAuthenticated) "settings" else "login"



    NavHost(navController = navController,
        startDestination , builder = {
        composable("login"){
            LoginScreen(modifier, navController, authViewModel)
        }
        composable("signup"){
            SignupScreen(modifier, navController, authViewModel)
        }
        composable("settings"){
            SettingsScreen(navController, authViewModel)
        }
    })
}