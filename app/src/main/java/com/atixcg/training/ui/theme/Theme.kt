package com.atixcg.training.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// The source design is a single fixed warm-paper look, so we pin one scheme
// rather than following the system light/dark or dynamic color.
private val PaperColorScheme = lightColorScheme(
    primary = Ink,
    onPrimary = OnInk,
    secondary = Ink,
    onSecondary = OnInk,
    background = Paper,
    onBackground = Ink,
    surface = CardBg,
    onSurface = Ink,
    surfaceVariant = ThumbBg,
    onSurfaceVariant = SubText,
    outline = CardBorder,
)

@Composable
fun TrainingTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Paper background is light -> use dark status/nav bar icons.
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = true
            controller.isAppearanceLightNavigationBars = true
        }
    }

    MaterialTheme(
        colorScheme = PaperColorScheme,
        typography = Typography,
        content = content
    )
}
