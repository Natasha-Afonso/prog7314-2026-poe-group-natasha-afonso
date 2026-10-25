package com.example.prjmetrix

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.prjmetrix.ui.theme.*
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Composable
//Pawar, 2026
fun AppTheme(
    theme: String,
    fontSize: String,
    highContrast: Boolean,
    colorTheme: String = "Default",
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


    val defaultColors = if (isDark) {
        darkColorScheme(
            primary = Color(0xFFB9D7E8),
            onPrimary = Color(0xFF1F3A4D),
            primaryContainer = Color(0xFF7A3E3E),
            onPrimaryContainer = Color(0xFFFAF6EC),
            secondary = Color(0xFFF3E4A8),
            onSecondary = Color(0xFF3A2A2A),
            secondaryContainer = Color(0xFF5B4A1F),
            onSecondaryContainer = Color(0xFFF3E4A8),
            tertiary = Color(0xFFD9A0A0),
            background = Color(0xFF1F1A1A),
            onBackground = Color(0xFFFAF6EC),
            surface = Color(0xFF1F1A1A),
            onSurface = Color(0xFFFAF6EC)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF7A3E3E),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFB9D7E8),
            onPrimaryContainer = Color(0xFF5B2A2A),
            secondary = Color(0xFFD9C173),
            onSecondary = Color(0xFF3A2A2A),
            secondaryContainer = Color(0xFFF3E4A8),
            onSecondaryContainer = Color(0xFF3A2A2A),
            tertiary = Color(0xFF5B2A2A),
            background = Color(0xFFFAF6EC),
            onBackground = Color(0xFF3A2A2A),
            surface = Color(0xFFFAF6EC),
            onSurface = Color(0xFF3A2A2A)
        )
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


    val normalColors = when (colorTheme) {
        "Ocean" -> makeScheme(isDark, Color(0xFF1E6091), Color(0xFFCFE6F5), Color(0xFF168AAD), Color(0xFFF2F8FC), Color(0xFF0F1A22))
        "Forest" -> makeScheme(isDark, Color(0xFF2D6A4F), Color(0xFFD4EAD9), Color(0xFF74A57F), Color(0xFFF3F8F3), Color(0xFF111A14))
        "Sunset" -> makeScheme(isDark, Color(0xFFD1495B), Color(0xFFFAD4D8), Color(0xFFEDAE49), Color(0xFFFFF6F0), Color(0xFF21161A))
        else -> defaultColors
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
private fun onColor(c: Color) =
    if (c.luminance() > 0.5f) Color(0xFF1A1A1A) else Color.White

private fun makeScheme(
    dark: Boolean,
    primary: Color,
    container: Color,
    secondary: Color,
    lightBg: Color,
    darkBg: Color
): ColorScheme {
    val p = if (dark) lerp(primary, Color.White, 0.4f) else primary
    val pc = if (dark) lerp(primary, Color.Black, 0.5f) else container
    val bg = if (dark) darkBg else lightBg
    val surf = if (dark) lerp(darkBg, Color.White, 0.08f) else Color.White
    val s = if (dark) lerp(secondary, Color.White, 0.3f) else secondary

    return if (dark) darkColorScheme(
        primary = p, onPrimary = onColor(p),
        primaryContainer = pc, onPrimaryContainer = onColor(pc),
        secondary = s, onSecondary = onColor(s),
        background = bg, onBackground = onColor(bg),
        surface = surf, onSurface = onColor(surf)
    ) else lightColorScheme(
        primary = p, onPrimary = onColor(p),
        primaryContainer = pc, onPrimaryContainer = onColor(pc),
        secondary = s, onSecondary = onColor(s),
        background = bg, onBackground = onColor(bg),
        surface = surf, onSurface = onColor(surf)
    )
}
// Remembers the chosen colour theme on this phone only
class ColorThemeStore(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("metrix_look", Context.MODE_PRIVATE)

    var colorTheme by mutableStateOf(
        prefs.getString("color_theme", "Default") ?: "Default"
    )
        private set

    fun select(name: String) {
        colorTheme = name
        prefs.edit().putString("color_theme", name).apply()
    }
}

/*Reference List:
*Pawar, A. 2026.How to Build a Consistent Custom App Theme in Jetpack Compose (Material 3). [source code] Available at: <https://medium.com/softaai-blogs/how-to-build-a-consistent-custom-app-theme-in-jetpack-compose-material-3-86053f9b8aa1> .[Accessed 20 September 2026].
*Stack Overflow. 2022. Jetpack Compose: How to change theme from light to dark mode programmatically onClick. [source code] Available at:<https://stackoverflow.com/questions/65192409/jetpack-compose-how-to-change-theme-from-light-to-dark-mode-programmatically-on>. [Accessed 20 September 2026].
* * */