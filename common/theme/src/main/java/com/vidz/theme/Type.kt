package com.vidz.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Created by Vidz on 08/01/2025.
 *
 * This file defines typography styles and utility functions for converting
 * between different measurement units in Android.
 */
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = fontFamily,
        fontSize = 63.81.sp, // Heading/3XL/Medium
        lineHeight = 66.94.sp,
        fontWeight = FontWeight.Medium,
    ),
    displayMedium = TextStyle(
        fontFamily = fontFamily,
        fontSize = 51.25.sp, // Heading/2XL/Medium
        lineHeight = 58.58.sp,
        fontWeight = FontWeight.Medium
    ),

    displaySmall = TextStyle(
        fontFamily = fontFamily,
        fontSize = 40.79.sp, // Heading/XL/Medium
        lineHeight = 50.21.sp,
        fontWeight = FontWeight.Medium
    ),
    headlineLarge = TextStyle(
        fontFamily = fontFamily,
        fontSize = 32.43.sp, // Heading/LG/Medium
        lineHeight = 41.84.sp,
        fontWeight = FontWeight.Medium
    ),
    headlineMedium = TextStyle(
        fontFamily = fontFamily,
        fontSize = 26.15.sp, // Heading/MD/Medium
        lineHeight = 33.47.sp,
        fontWeight = FontWeight.Medium
    ),
    headlineSmall = TextStyle(
        fontFamily = fontFamily,
        fontSize = 20.92.sp, // Heading/SM/Medium
        lineHeight = 25.10.sp,
        fontWeight = FontWeight.Medium
    ),
    titleLarge = TextStyle(
        fontFamily = fontFamily,
        fontSize = 16.74.sp, // Heading/XS/Medium
        lineHeight = 25.10.sp,
        fontWeight = FontWeight.Medium
    ),
    titleMedium = TextStyle(
        fontFamily = fontFamily,
        fontSize = 16.74.sp, // Body/M/Medium
        lineHeight = 25.10.sp,
        fontWeight = FontWeight.Medium
    ),
    titleSmall = TextStyle(
        fontFamily = fontFamily,
        fontSize = 14.64.sp, // Body/S/Medium
        lineHeight = 25.10.sp,
        fontWeight = FontWeight.Medium
    ),
    bodyLarge = TextStyle(
        fontFamily = fontFamily,
        fontSize = 16.74.sp, // Body/M/Regular
        lineHeight = 25.10.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = TextStyle(
        fontFamily = fontFamily,
        fontSize = 14.64.sp, // Body/S/Regular
        lineHeight = 25.10.sp,
        fontWeight = FontWeight.Normal
    ),
    bodySmall = TextStyle(
        fontFamily = fontFamily,
        fontSize = 11.51.sp, // Body/XS/Regular
        lineHeight = 16.74.sp,
        fontWeight = FontWeight.Normal
    ),
    labelLarge = TextStyle(
        fontFamily = fontFamily,
        fontSize = 11.51.sp, // Body/XS/Medium
        lineHeight = 16.74.sp,
        fontWeight = FontWeight.Medium
    ),
    labelMedium = TextStyle(
        fontFamily = fontFamily,
        fontSize = 11.51.sp, // Body/XS/Regular
        lineHeight = 16.74.sp,
        fontWeight = FontWeight.Medium
    ),
)

/**
 * Extension functions for Typography to provide easy access to text styles
 */

// Extension properties instead of functions
val Typography.heading3XL: TextStyle get() = displayLarge
val Typography.heading2XL: TextStyle get() = displayMedium
val Typography.headingXL: TextStyle get() = displaySmall
val Typography.headingLG: TextStyle get() = headlineLarge
val Typography.headingMD: TextStyle get() = headlineMedium
val Typography.headingSM: TextStyle get() = headlineSmall
val Typography.headingXS: TextStyle get() = titleLarge
val Typography.bodyLMedium: TextStyle get() = TextStyle(
    fontFamily = fontFamily,
    fontSize = 16.sp, // Body/XS/Regular
    lineHeight = 24.sp,
    fontWeight = FontWeight.Medium
)
val Typography.body12Medium: TextStyle get() = TextStyle(
    fontFamily = fontFamily,
    fontSize = 12.55.sp, // Body/XS/Regular
    lineHeight = 16.74.sp,
    fontWeight = FontWeight.W500
)
val Typography.body12Regular: TextStyle get() = TextStyle(
    fontFamily = fontFamily,
    fontSize = 12.55.sp, // Body/XS/Regular
    lineHeight = 16.74.sp,
    fontWeight = FontWeight.W400
)


val Typography.bodyMMedium: TextStyle get() = titleMedium
val Typography.bodySMedium: TextStyle get() = titleSmall

val Typography.bodyMRegular: TextStyle get() = bodyLarge
val Typography.bodySRegular: TextStyle get() = bodyMedium
val Typography.bodyXSRegular: TextStyle get() = bodySmall

val Typography.bodyXSMedium: TextStyle get() = labelLarge