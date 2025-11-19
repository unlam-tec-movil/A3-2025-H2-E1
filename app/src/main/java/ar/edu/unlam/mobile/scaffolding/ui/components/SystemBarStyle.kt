package ar.edu.unlam.mobile.scaffolding.ui.components

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun SystemBarStyle(automaticStyle: Boolean = true) {
    val window = (LocalView.current.context as Activity).window
    if (automaticStyle) {
        val darkIcons = isSystemInDarkTheme()
        SideEffect {
            WindowInsetsControllerCompat(window, window.decorView)
                .isAppearanceLightStatusBars = !darkIcons
        }
    } else {
        SideEffect {
            WindowInsetsControllerCompat(window, window.decorView)
                .isAppearanceLightStatusBars = true
        }
    }
}
