package com.example.ghostdownloader.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = CyberBlueLight,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF004578),
    onPrimaryContainer = Color.White,
    secondary = CyberTeal,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF004B50),
    onSecondaryContainer = Color.White,
    tertiary = CyberPurple,
    background = DarkBg,
    onBackground = Color(0xFFE6EDF3),
    surface = DarkSurface,
    onSurface = Color(0xFFE6EDF3),
    surfaceVariant = DarkCard,
    onSurfaceVariant = Color(0xFF8B949E),
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = CyberBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCCE4F7),
    onPrimaryContainer = Color(0xFF003355),
    secondary = CyberTeal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCCF1F3),
    onSecondaryContainer = Color(0xFF00383C),
    tertiary = CyberPurple,
    background = LightBg,
    onBackground = Color(0xFF1F2328),
    surface = LightSurface,
    onSurface = Color(0xFF1F2328),
    surfaceVariant = Color(0xFFF6F8FA),
    onSurfaceVariant = Color(0xFF57606A),
    outline = LightBorder
)

@Composable
fun GhostDownloaderTheme(
    darkTheme: Boolean = true, // Default to dark aesthetic like desktop Ghost Downloader
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
