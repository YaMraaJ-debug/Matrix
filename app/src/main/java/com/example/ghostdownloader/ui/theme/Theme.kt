package com.example.ghostdownloader.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// 1. Matrix Neon Green (Signature Matrix-dlp Theme)
val MatrixNeonColorScheme = darkColorScheme(
    primary = MatrixNeonGreen,
    onPrimary = Color(0xFF04180A),
    primaryContainer = Color(0xFF0A3316),
    onPrimaryContainer = MatrixNeonGreenGlow,
    secondary = NeonCyan,
    onSecondary = Color(0xFF00252E),
    secondaryContainer = Color(0xFF083D3E),
    onSecondaryContainer = Color(0xFFE0FBFC),
    tertiary = MatrixNeonGreenGlow,
    background = MatrixNeonDark,
    onBackground = Color(0xFFE8F5E9),
    surface = MatrixNeonSurface,
    onSurface = Color(0xFFE8F5E9),
    surfaceVariant = MatrixNeonCard,
    onSurfaceVariant = Color(0xFF81C784),
    outline = MatrixNeonBorder
)

// 2. Neon Synthwave / Cyberpunk Violet
val SynthwaveColorScheme = darkColorScheme(
    primary = NeonViolet,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF38004D),
    onPrimaryContainer = Color(0xFFF3C4FF),
    secondary = NeonPink,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4A0025),
    onSecondaryContainer = Color(0xFFFFD8E6),
    tertiary = NeonCyan,
    background = SynthwaveDark,
    onBackground = Color(0xFFF1EBFB),
    surface = SynthwaveSurface,
    onSurface = Color(0xFFF1EBFB),
    surfaceVariant = SynthwaveCard,
    onSurfaceVariant = Color(0xFFB39DDB),
    outline = SynthwaveBorder
)

// 3. OLED Pure Black (Maximum Contrast & Battery Saving)
val OledColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF00363A),
    onPrimaryContainer = Color(0xFFB2EBF2),
    secondary = MatrixNeonGreen,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF003B16),
    onSecondaryContainer = Color(0xFFC8E6C9),
    tertiary = Color(0xFFFFD600),
    background = OledBg,
    onBackground = Color(0xFFEEEEEE),
    surface = OledSurface,
    onSurface = Color(0xFFEEEEEE),
    surfaceVariant = OledCard,
    onSurfaceVariant = Color(0xFFAAAAAA),
    outline = OledBorder
)

// 4. Cyber Blue Dark
val CyberBlueColorScheme = darkColorScheme(
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

// 5. Clean Minimal Light
val CleanLightColorScheme = lightColorScheme(
    primary = Color(0xFF008736),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1F2DD),
    onPrimaryContainer = Color(0xFF003814),
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF034A6E),
    tertiary = CyberPurple,
    background = LightBg,
    onBackground = Color(0xFF1E293B),
    surface = LightSurface,
    onSurface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B),
    outline = LightBorder
)

@Composable
fun MatrixDlpTheme(
    themeMode: String = "matrix_neon", // "matrix_neon", "synthwave", "oled", "cyber_blue", "clean_light"
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        "synthwave" -> SynthwaveColorScheme
        "oled" -> OledColorScheme
        "cyber_blue" -> CyberBlueColorScheme
        "clean_light" -> CleanLightColorScheme
        else -> MatrixNeonColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

@Composable
fun GhostDownloaderTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    themeMode: String = "matrix_neon",
    content: @Composable () -> Unit
) {
    MatrixDlpTheme(
        themeMode = themeMode,
        dynamicColor = dynamicColor,
        content = content
    )
}

