package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// GirlyCycle Typography System:
// Friendly, warm, editorial headings paired with highly legible, accessible body text.
val EditorialSerif = FontFamily.Serif
val EditorialSans = FontFamily.Default

fun getGirlyCycleTypography(
    isLargeText: Boolean = false,
    isHighContrast: Boolean = false
): Typography {
    // Scalable body text: minimum 16sp, scales up to 20sp when toggle is enabled
    val bodyLargeSize = if (isLargeText) 20.sp else 17.sp
    val bodyLargeLineHeight = if (isLargeText) 28.sp else 24.sp

    val bodyMediumSize = if (isLargeText) 18.sp else 16.sp // Strict 16sp minimum!
    val bodyMediumLineHeight = if (isLargeText) 26.sp else 22.sp

    val bodySmallSize = if (isLargeText) 16.sp else 14.sp
    val bodySmallLineHeight = if (isLargeText) 22.sp else 18.sp

    val bodyWeight = if (isHighContrast) FontWeight.Medium else FontWeight.Normal

    return Typography(
        displayLarge = TextStyle(
            fontFamily = EditorialSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 38.sp,
            letterSpacing = (-0.5).sp
        ),
        displayMedium = TextStyle(
            fontFamily = EditorialSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            letterSpacing = (-0.25).sp
        ),
        displaySmall = TextStyle(
            fontFamily = EditorialSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            lineHeight = 30.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = EditorialSerif,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 30.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = EditorialSerif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 21.sp,
            lineHeight = 27.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = EditorialSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 19.sp,
            lineHeight = 25.sp
        ),
        titleLarge = TextStyle(
            fontFamily = EditorialSans,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            lineHeight = 25.sp,
            letterSpacing = 0.2.sp
        ),
        titleMedium = TextStyle(
            fontFamily = EditorialSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            lineHeight = 23.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = EditorialSans,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            lineHeight = 21.sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = EditorialSans,
            fontWeight = bodyWeight,
            fontSize = bodyLargeSize,
            lineHeight = bodyLargeLineHeight,
            letterSpacing = 0.25.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = EditorialSans,
            fontWeight = bodyWeight,
            fontSize = bodyMediumSize,
            lineHeight = bodyMediumLineHeight,
            letterSpacing = 0.2.sp
        ),
        bodySmall = TextStyle(
            fontFamily = EditorialSans,
            fontWeight = bodyWeight,
            fontSize = bodySmallSize,
            lineHeight = bodySmallLineHeight,
            letterSpacing = 0.3.sp
        ),
        labelLarge = TextStyle(
            fontFamily = EditorialSans,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.5.sp
        ),
        labelMedium = TextStyle(
            fontFamily = EditorialSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),
        labelSmall = TextStyle(
            fontFamily = EditorialSans,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            letterSpacing = 0.3.sp
        )
    )
}

val Typography = getGirlyCycleTypography(isLargeText = false, isHighContrast = false)
