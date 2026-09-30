package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// GirlyCycle Design Blueprint: Deep Teal, Soft Coral, Warm Grays
// ============================================================================

// Dominant Primary: Deep Teal (Calm, grounding, clinical yet deeply human)
val TealDark = Color(0xFF0C3840)
val TealPrimary = Color(0xFF13505B)
val TealMedium = Color(0xFF1B6A78)
val TealLight = Color(0xFF2C8696)
val TealContainer = Color(0xFFD4EAEB)
val OnTealContainer = Color(0xFF08272D)

// Secondary: Soft Coral / Peach (Highlights, CTAs, warm human warmth)
val CoralPrimary = Color(0xFFE07A5F)
val CoralLight = Color(0xFFF28482)
val CoralDark = Color(0xFFC75D42)
val CoralContainer = Color(0xFFFDE8E3)
val OnCoralContainer = Color(0xFF4E1B10)
val PeachHighlight = Color(0xFFFFB4A2)

// Neutrals: Warm grays (not pure black/white)
// Light mode warm gray palette
val WarmGrayCanvas = Color(0xFFF8F6F2)
val WarmGraySurface = Color(0xFFFFFFFF)
val WarmGraySurfaceVariant = Color(0xFFEFEBE4)
val WarmGrayBorder = Color(0xFFDED8D0)
val WarmGrayBorderStrong = Color(0xFFC5BCB2)
val WarmGrayTextPrimary = Color(0xFF2B2E30)
val WarmGrayTextSecondary = Color(0xFF5F666D)
val WarmGrayTextMuted = Color(0xFF8B9296)

// Dark mode warm dark gray palette (never pure black)
val DarkGrayBackground = Color(0xFF1E2124)
val DarkGraySurface = Color(0xFF262A2E)
val DarkGraySurfaceVariant = Color(0xFF31363C)
val DarkGrayBorder = Color(0xFF40474F)
val DarkGrayTextPrimary = Color(0xFFECEFF1)
val DarkGrayTextSecondary = Color(0xFFB0B7BC)
val DarkGrayTextMuted = Color(0xFF82898E)

// High-Contrast mode helpers
val HighContrastBorderLight = Color(0xFF101214)
val HighContrastBorderDark = Color(0xFFE0E0E0)
val HighContrastTextLight = Color(0xFF0A0C0D)
val HighContrastTextDark = Color(0xFFFFFFFF)

// ============================================================================
// Symptom Category Colors (Strict Blueprint Mapping)
// blue = sleep, orange = mood, green = energy, purple = cognitive, red = physical pain
// ============================================================================
val SymptomSleepBlue = Color(0xFF3A86C8)       // Sleep
val SymptomMoodOrange = Color(0xFFE76F51)      // Mood
val SymptomEnergyGreen = Color(0xFF2A9D8F)     // Energy
val SymptomCognitivePurple = Color(0xFF8338EC) // Cognitive (Brain Fog)
val SymptomPainRed = Color(0xFFD90429)         // Physical Pain
val SymptomCycleRose = Color(0xFFC85A6A)       // Menstrual Cycle & Flow
val SymptomExtendedTeal = Color(0xFF208291)    // Digestion, Sexual Health, Skin/Hair

// Severity Accent Colors (1 to 5)
val Severity1Color = Color(0xFF2A9D8F) // Mild (Green)
val Severity2Color = Color(0xFF52B788) // Mild-Mod
val Severity3Color = Color(0xFFE76F51) // Moderate (Orange)
val Severity4Color = Color(0xFFD90429) // Severe (Red)
val Severity5Color = Color(0xFF8338EC) // Debilitating (Deep Purple)

// ============================================================================
// Legacy Aliases for seamless backward compatibility across existing screens
// ============================================================================
val PlumPrimary = TealPrimary
val PlumDark = TealDark
val PlumMedium = TealMedium
val PlumLight = TealLight
val PlumContainer = TealContainer
val OnPlumContainer = OnTealContainer

val Mustard = CoralPrimary
val MustardDark = CoralDark
val MustardLight = CoralLight
val MustardContainer = CoralContainer
val OnMustardContainer = OnCoralContainer

val BurntOrange = SymptomMoodOrange
val BurntOrangeDark = CoralDark
val BurntOrangeLight = CoralLight
val BurntOrangeContainer = CoralContainer
val OnBurntOrangeContainer = OnCoralContainer

val Sage = SymptomEnergyGreen
val SageDark = Color(0xFF1E756A)
val SageLight = Color(0xFF52B788)
val SageContainer = Color(0xFFE2F3F0)
val OnSageContainer = Color(0xFF0E3833)

val EditorialCanvas = WarmGrayCanvas
val EditorialSurface = WarmGraySurface
val EditorialSurfaceVariant = WarmGraySurfaceVariant
val EditorialBorder = WarmGrayBorder
val EditorialBorderStrong = WarmGrayBorderStrong
val EditorialInk = WarmGrayTextPrimary
val EditorialMuted = WarmGrayTextSecondary
val EditorialSubtle = WarmGrayTextMuted

val DarkBackground = DarkGrayBackground
val DarkSurface = DarkGraySurface
val DarkSurfaceVariant = DarkGraySurfaceVariant
val DarkBorder = DarkGrayBorder
val DarkTextPrimary = DarkGrayTextPrimary
val DarkTextSecondary = DarkGrayTextSecondary
