package ar.edu.unlam.mobile.scaffolding.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val DarkColorScheme =
    darkColorScheme(
        primary = Color(0xFF8FCF84),
        onPrimary = Color(0xFF0D3709),
        primaryContainer = Color(0xFF456B3B),
        onPrimaryContainer = Color(0xFFC9F0BE),
        secondary = Color(0xFFD7DB8E),
        onSecondary = Color(0xFF34360A),
        secondaryContainer = Color(0xFF4D4F24),
        onSecondaryContainer = Color(0xFFEFEFB6),
        tertiary = Color(0xFF5BB28A),
        onTertiary = Color(0xFF003823),
        tertiaryContainer = Color(0xFF1B5039),
        onTertiaryContainer = Color(0xFF9EEBC2),
        error = Color(0xFFCF6679),
        onError = Color.Black,
        errorContainer = Color(0xFF8C1D18),
        onErrorContainer = Color(0xFFF9DEDC),
        background = Color(0xFF121312),
        onBackground = Color(0xFFE1E3E0),
        surface = Color(0xFF1B1C1A),
        onSurface = Color(0xFFE1E3E0),
        surfaceVariant = Color(0xFF2D322B),
        onSurfaceVariant = Color(0xFFC3C8BE),
        outline = Color(0xFF484E45),
        outlineVariant = Color(0xFF2E332C),
        inverseSurface = Color(0xFFE1E3E0),
        inverseOnSurface = Color(0xFF121312),
        inversePrimary = Color(0xFF3A6931),
        surfaceTint = Color(0xFF8FCF84),
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Green,
        onPrimary = Color.White,
        primaryContainer = LightGreen,
        onPrimaryContainer = DarkGreen,
        secondary = Olive,
        onSecondary = DarkGreen,
        secondaryContainer = LightOlive,
        onSecondaryContainer = Color.Black,
        tertiary = DarkGreen,
        onTertiary = Color.White,
        background = Color.White,
        onBackground = DarkGreen,
        surface = LightOlive,
        onSurface = DarkGreen,
    )

@Composable
fun ScaffoldingV2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
