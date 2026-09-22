package com.example.prjmetrix

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
//Pawar, 2026
fun AppTheme(
    theme: String,
    fontSize: String,
    highContrast: Boolean,
    content: @Composable () -> Unit
) {

    // Changes the size of all the text styles
    val fontScale = when (fontSize) {
        "Small" -> 0.85f
        "Large" -> 1.25f
        else -> 1f
    }


    val defaultTypography = Typography()

    val appTypography = Typography(
        displayLarge = defaultTypography.displayLarge.copy(
            fontSize = defaultTypography.displayLarge.fontSize * fontScale
        ),
        displayMedium = defaultTypography.displayMedium.copy(
            fontSize = defaultTypography.displayMedium.fontSize * fontScale
        ),
        displaySmall = defaultTypography.displaySmall.copy(
            fontSize = defaultTypography.displaySmall.fontSize * fontScale
        ),
        headlineLarge = defaultTypography.headlineLarge.copy(
            fontSize = defaultTypography.headlineLarge.fontSize * fontScale
        ),
        headlineMedium = defaultTypography.headlineMedium.copy(
            fontSize = defaultTypography.headlineMedium.fontSize * fontScale
        ),
        headlineSmall = defaultTypography.headlineSmall.copy(
            fontSize = defaultTypography.headlineSmall.fontSize * fontScale
        ),
        titleLarge = defaultTypography.titleLarge.copy(
            fontSize = defaultTypography.titleLarge.fontSize * fontScale
        ),
        titleMedium = defaultTypography.titleMedium.copy(
            fontSize = defaultTypography.titleMedium.fontSize * fontScale
        ),
        titleSmall = defaultTypography.titleSmall.copy(
            fontSize = defaultTypography.titleSmall.fontSize * fontScale
        ),
        bodyLarge = defaultTypography.bodyLarge.copy(
            fontSize = defaultTypography.bodyLarge.fontSize * fontScale
        ),
        bodyMedium = defaultTypography.bodyMedium.copy(
            fontSize = defaultTypography.bodyMedium.fontSize * fontScale
        ),
        bodySmall = defaultTypography.bodySmall.copy(
            fontSize = defaultTypography.bodySmall.fontSize * fontScale
        ),
        labelLarge = defaultTypography.labelLarge.copy(
            fontSize = defaultTypography.labelLarge.fontSize * fontScale
        ),
        labelMedium = defaultTypography.labelMedium.copy(
            fontSize = defaultTypography.labelMedium.fontSize * fontScale
        ),
        labelSmall = defaultTypography.labelSmall.copy(
            fontSize = defaultTypography.labelSmall.fontSize * fontScale
        )
    )

//Stack Overflow,2022
    val isDark = when (theme) {
        "Dark" -> true
        "Light" -> false
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }


    val normalColors = if (isDark) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

    val contrastColors = if (isDark) {
        darkColorScheme(
            primary = Color.White,
            onPrimary = Color.Black,
            background = Color.Black,
            onBackground = Color.White,
            surface = Color.Black,
            onSurface = Color.White
        )
    } else {
        lightColorScheme(
            primary = Color.Black,
            onPrimary = Color.White,
            background = Color.White,
            onBackground = Color.Black,
            surface = Color.White,
            onSurface = Color.Black
        )
    }

    MaterialTheme(
        colorScheme = if (highContrast) {
            contrastColors
        } else {
            normalColors
        },
        typography = appTypography,
        content = content
    )
}
/*Reference List:
*Pawar, A. 2026.How to Build a Consistent Custom App Theme in Jetpack Compose (Material 3). [source code] Available at: <https://medium.com/softaai-blogs/how-to-build-a-consistent-custom-app-theme-in-jetpack-compose-material-3-86053f9b8aa1> .[Accessed 20 September 2026].
*Stack Overflow. 2022. Jetpack Compose: How to change theme from light to dark mode programmatically onClick. [source code] Available at:<https://stackoverflow.com/questions/65192409/jetpack-compose-how-to-change-theme-from-light-to-dark-mode-programmatically-on>. [Accessed 20 September 2026].
* * */