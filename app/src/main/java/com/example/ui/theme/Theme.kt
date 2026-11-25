package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

enum class DesignStyle {
    NOTHING_OS,
    MATERIAL_YOU
}

val LocalDesignStyle = staticCompositionLocalOf { DesignStyle.NOTHING_OS }
val LocalIsDarkTheme = staticCompositionLocalOf { true }

private val NothingDarkColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    primaryContainer = Color.White,
    onPrimaryContainer = Color.Black,
    secondary = NothingTextSecondaryDark,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF1E1E1E),
    onSecondaryContainer = Color.White,
    background = NothingBlack,
    onBackground = NothingTextPrimaryDark,
    surface = NothingSurfaceDark,
    onSurface = NothingTextPrimaryDark,
    surfaceVariant = Color(0xFF222222),
    onSurfaceVariant = NothingTextSecondaryDark,
    surfaceContainerLow = NothingSurfaceLowDark,
    surfaceContainerHigh = NothingSurfaceHighDark,
    outline = NothingBorderDark,
    outlineVariant = Color(0xFF282828),
    error = SignalRed,
    onError = Color.White,
    errorContainer = NothingRedTintDark,
    onErrorContainer = Color(0xFFFFB4AB)
)

private val NothingLightColorScheme = lightColorScheme(
    primary = NothingTextPrimaryLight,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF181818),
    onPrimaryContainer = Color.White,
    secondary = NothingTextSecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDEDED),
    onSecondaryContainer = NothingTextPrimaryLight,
    background = NothingWhite,
    onBackground = NothingTextPrimaryLight,
    surface = NothingSurfaceLight,
    onSurface = NothingTextPrimaryLight,
    surfaceVariant = Color(0xFFEBEBEB),
    onSurfaceVariant = NothingTextSecondaryLight,
    surfaceContainerLow = NothingSurfaceLowLight,
    surfaceContainerHigh = NothingSurfaceHighLight,
    outline = NothingBorderLight,
    outlineVariant = Color(0xFFDCDCDC),
    error = SignalRed,
    onError = Color.White,
    errorContainer = NothingRedTintLight,
    onErrorContainer = Color(0xFF8C1D18)
)

private val FallbackMaterialDark = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE8DEF8),
    background = Color(0xFF141218),
    onBackground = Color(0xFFE6E0E9),
    surface = Color(0xFF141218),
    onSurface = Color(0xFFE6E0E9),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    surfaceContainerLow = Color(0xFF1D1B20),
    surfaceContainerHigh = Color(0xFF2B2930),
    tertiary = Color(0xFFEFB8C8),
    onTertiary = Color(0xFF492532),
    tertiaryContainer = Color(0xFF633B48),
    onTertiaryContainer = Color(0xFFFFD8E4),
    error = Color(0xFFF2B8B5),
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF8C1D18)
)

private val FallbackMaterialLight = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF625B71),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8DEF8),
    onSecondaryContainer = Color(0xFF1D192B),
    tertiary = Color(0xFF7D5260),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD8E4),
    onTertiaryContainer = Color(0xFF31111D),
    background = Color(0xFFFEF7FF),
    onBackground = Color(0xFF1D1B20),
    surface = Color(0xFFFEF7FF),
    onSurface = Color(0xFF1D1B20),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    surfaceContainerLow = Color(0xFFF7F2FA),
    surfaceContainerHigh = Color(0xFFECE6F0),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC)
)

@Composable
fun NothingNewsTheme(
    designStyle: DesignStyle = DesignStyle.NOTHING_OS,
    darkTheme: Boolean = isSystemInDarkTheme(),
    appFontScale: Float = 1.0f,
    hapticFeedbackEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val currentDensity = LocalDensity.current
    val clampedUserScale = appFontScale.coerceIn(1.0f, 1.5f)
    val scaledDensity = Density(
        density = currentDensity.density,
        fontScale = (currentDensity.fontScale.coerceIn(0.85f, 1.15f) * clampedUserScale).coerceIn(0.85f, 1.75f)
    )
    val hapticEngine = rememberAppHapticEngine(
        designStyle = designStyle,
        enabled = hapticFeedbackEnabled
    )

    val colorScheme: ColorScheme = when (designStyle) {
        DesignStyle.NOTHING_OS -> {
            if (darkTheme) NothingDarkColorScheme else NothingLightColorScheme
        }
        DesignStyle.MATERIAL_YOU -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                if (darkTheme) FallbackMaterialDark else FallbackMaterialLight
            }
        }
    }

    val typography = if (designStyle == DesignStyle.NOTHING_OS) NothingTypography else MaterialYouTypography
    val shapes = if (designStyle == DesignStyle.NOTHING_OS) NothingShapes else MaterialYouShapes

    CompositionLocalProvider(
        LocalDesignStyle provides designStyle,
        LocalIsDarkTheme provides darkTheme,
        LocalDensity provides scaledDensity,
        LocalHapticEngine provides hapticEngine
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}
