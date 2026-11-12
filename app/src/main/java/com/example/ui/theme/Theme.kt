package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val JokerDarkColorScheme = darkColorScheme(
  primary = JokerPurplePrimary,
  onPrimary = TextPrimary,
  primaryContainer = JokerPurpleDark,
  onPrimaryContainer = TextPrimary,
  secondary = JokerCyanAccent,
  onSecondary = JokerBackground,
  tertiary = JokerGold,
  onTertiary = JokerBackground,
  background = JokerBackground,
  onBackground = TextPrimary,
  surface = JokerSurface,
  onSurface = TextPrimary,
  surfaceVariant = JokerSurfaceVariant,
  onSurfaceVariant = TextSecondary,
  outline = BorderSubtle,
)

@Composable
fun JokerTvTheme(
  content: @Composable () -> Unit,
) {
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as? Activity)?.window
      if (window != null) {
        window.statusBarColor = JokerBackground.toArgb()
        window.navigationBarColor = JokerBackground.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
      }
    }
  }

  MaterialTheme(
    colorScheme = JokerDarkColorScheme,
    typography = Typography,
    content = content
  )
}
