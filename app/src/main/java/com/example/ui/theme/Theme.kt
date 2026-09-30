package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Atelier / GirlyCycle Dark Scheme (Warm dark gray, never pure black)
val AtelierDarkBackground = DarkGrayBackground
val AtelierDarkSurface = DarkGraySurface
val AtelierDarkSurfaceCard = DarkGraySurfaceVariant
val AtelierDarkBorder = DarkGrayBorder

private val GirlyCycleDarkColorScheme = darkColorScheme(
    primary = TealLight,
    onPrimary = DarkGrayTextPrimary,
    primaryContainer = TealDark,
    onPrimaryContainer = TealContainer,
    secondary = CoralLight,
    onSecondary = DarkGrayBackground,
    secondaryContainer = CoralDark,
    onSecondaryContainer = CoralContainer,
    tertiary = SymptomEnergyGreen,
    onTertiary = DarkGrayBackground,
    tertiaryContainer = SageDark,
    onTertiaryContainer = SageContainer,
    background = DarkGrayBackground,
    onBackground = DarkGrayTextPrimary,
    surface = DarkGraySurface,
    onSurface = DarkGrayTextPrimary,
    surfaceVariant = DarkGraySurfaceVariant,
    onSurfaceVariant = DarkGrayTextSecondary,
    outline = DarkGrayBorder,
    outlineVariant = DarkGrayTextMuted
)

private val GirlyCycleLightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = WarmGraySurface,
    primaryContainer = TealContainer,
    onPrimaryContainer = OnTealContainer,
    secondary = CoralPrimary,
    onSecondary = WarmGraySurface,
    secondaryContainer = CoralContainer,
    onSecondaryContainer = OnCoralContainer,
    tertiary = SymptomEnergyGreen,
    onTertiary = WarmGraySurface,
    tertiaryContainer = SageContainer,
    onTertiaryContainer = OnSageContainer,
    background = WarmGrayCanvas,
    onBackground = WarmGrayTextPrimary,
    surface = WarmGraySurface,
    onSurface = WarmGrayTextPrimary,
    surfaceVariant = WarmGraySurfaceVariant,
    onSurfaceVariant = WarmGrayTextSecondary,
    outline = WarmGrayBorder,
    outlineVariant = WarmGrayBorderStrong
)

// High-Contrast variants for enhanced visibility
private val HighContrastLightColorScheme = GirlyCycleLightColorScheme.copy(
    onBackground = HighContrastTextLight,
    onSurface = HighContrastTextLight,
    outline = HighContrastBorderLight,
    outlineVariant = HighContrastBorderLight
)

private val HighContrastDarkColorScheme = GirlyCycleDarkColorScheme.copy(
    onBackground = HighContrastTextDark,
    onSurface = HighContrastTextDark,
    outline = HighContrastBorderDark,
    outlineVariant = HighContrastBorderDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    highContrast: Boolean = false,
    largeText: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme && highContrast -> HighContrastDarkColorScheme
        darkTheme -> GirlyCycleDarkColorScheme
        highContrast -> HighContrastLightColorScheme
        else -> GirlyCycleLightColorScheme
    }

    val typography = getGirlyCycleTypography(
        isLargeText = largeText,
        isHighContrast = highContrast
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
