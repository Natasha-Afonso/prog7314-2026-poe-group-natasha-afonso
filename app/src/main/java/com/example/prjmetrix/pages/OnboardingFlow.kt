package com.example.prjmetrix.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.prjmetrix.ProfileRepository
import com.example.prjmetrix.pages.BgBlue
import com.example.prjmetrix.pages.BrandBrown
import com.example.prjmetrix.pages.DividerGray

object OnboardingDestinations {
    const val WELCOME_ROUTE = "welcome"
    const val SURVEY_ROUTE = "survey"
}

// Same look as your sign-in / sign-up screens
@Composable
fun OnboardingTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = BrandBrown,
            onPrimary = Color.White,
            primaryContainer = BrandBrown.copy(alpha = 0.15f),
            onPrimaryContainer = BrandBrown,
            background = BgBlue,
            onBackground = Color.Black,
            surface = Color.White,
            onSurface = Color.Black,
            outline = DividerGray,
            inverseOnSurface = Color.White
        ),
        shapes = Shapes(small = RoundedCornerShape(12.dp)),
        content = content
    )
}

@Composable
fun OnboardingApp(
    onFinished: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    OnboardingTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavHost(
                navController = navController,
                startDestination = OnboardingDestinations.WELCOME_ROUTE
            ) {
                composable(OnboardingDestinations.WELCOME_ROUTE) {
                    WelcomeScreen(onGetStarted = {
                        navController.navigate(OnboardingDestinations.SURVEY_ROUTE)
                    })
                }
                composable(OnboardingDestinations.SURVEY_ROUTE) {
                    SurveyScaffold(
                        onNavigateUp = { navController.navigateUp() },
                        onFinished = onFinished
                    )
                }
            }
        }
    }
}

private enum class GateState { LOADING, NEEDS_ONBOARDING, READY, ERROR }

@Composable
fun PostSignInGate(
    onSessionExpired: () -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val profileApi = remember { ProfileRepository(context).api }
    var gate by remember { mutableStateOf(GateState.LOADING) }
    var attempt by remember { mutableIntStateOf(0) }

    LaunchedEffect(attempt) {
        gate = GateState.LOADING
        gate = try {
            val response = profileApi.getProfile()
            when {
                response.isSuccessful -> GateState.READY
                response.code() == 404 -> GateState.NEEDS_ONBOARDING   // no profile yet
                response.code() == 401 -> {
                    onSessionExpired()          // saved token has expired: back to login
                    GateState.LOADING
                }
                else -> GateState.ERROR
            }
        } catch (e: Exception) {
            GateState.ERROR
        }
    }

    when (gate) {
        GateState.LOADING -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        GateState.NEEDS_ONBOARDING -> OnboardingApp(onFinished = { gate = GateState.READY })
        GateState.READY -> content()
        GateState.ERROR -> Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Couldn't load your profile.")
            Button(onClick = { attempt++ }) { Text("Try again") }
        }
    }
}

/*References

Teergarden, C., 2023. Mastering Lazy Lists in Jetpack Compose with Data Classes and MVI. (Version 10.0) [Source Code]. Available at: < https://proandroiddev.com/mastering-lazy-lists-in-jetpack-compose-with-data-classes-and-mvi-da240800c6e9 > [Accessed 21 September 2026]
Kavi, R., 2026. Mastering High-Performance Custom Canvas animations in jetpack compose: building a premium circular progress indicator. (Version 10.0) [Source Code]. Available at: < https://raghunandan2005.medium.com/mastering-high-performance-custom-canvas-animations-in-jetpack-compose-building-a-premium-circular-b9f9e465b118 > [Accessed 21 September 2026]
Malla, M., 2023. Jetsurvey. (Version 10.0) [Source Code]. Available at: < https://github.com/ManasMalla/Jetsurvey > [Accessed 21 September 2026]
Easy Tuto, 2024. Simple to do App jetpackcompose androidtutorial. (Version 10.0) [Source Code]. Available at: < https://youtu.be/P3xQdINdrWY?si=0Z52xSOqN96XhODE > [Accessed 21 September 2026]
Samsung Developer., 2025. Build a health app with steps from Samsung Health and its connected wearables. (Version 10.0) [Source Code]. Available at: < https://youtu.be/kEe0bRqhx-g?si=tn83CXqncnYI_ska > [Accessed 21 September 2026]

 */