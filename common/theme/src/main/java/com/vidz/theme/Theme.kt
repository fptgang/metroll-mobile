package com.vidz.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * HCMC Metro Flat Design Theme - Material Design 3 compliant
 * Updated for metro transit application with flat design principles
 *
 * Flat Design Principles Applied:
 * - Bold, vibrant colors without gradients
 * - High contrast for better readability
 * - Simplified color relationships
 * - Clean, geometric color application
 * - No shadows or 3D effects in color scheme
 *
 * Color Usage:
 * - primary: Flat Metro Navy - main brand color, bold and vibrant
 * - secondary: Flat Metro Blue - secondary actions, high contrast
 * - tertiary: Flat Metro Red - accent color, bold alerts
 * - background: Pure white/black for maximum contrast
 * - surface: Clean flat surfaces without elevation effects
 * - surfaceTint: Minimal tinting for flat appearance
 * - onSurface: High contrast text for flat design readability
 */

private val FlatLightColors = lightColorScheme(
    // Primary - Bold Navy Blue
    primary = FlatMetroNavy, // Deep navy - bold and vibrant
    onPrimary = FlatMetroWhite, // Pure white for maximum contrast
    primaryContainer = FlatBluePastel, // Light blue container - flat
    onPrimaryContainer = FlatTextPrimary, // Dark text for high contrast

    // Secondary - Vibrant Blue
    secondary = FlatMetroBlue, // Bright blue - bold secondary
    onSecondary = FlatTextOnColor, // White text on colored background
    secondaryContainer = FlatBlueLight, // Light blue container - flat
    onSecondaryContainer = FlatTextPrimary, // Dark text for contrast

    // Tertiary - Bold Red
    tertiary = FlatMetroRed, // Vibrant red - bold accent
    onTertiary = FlatTextOnColor, // White text on red
    tertiaryContainer = FlatRedPastel, // Light red container - flat
    onTertiaryContainer = FlatTextPrimary, // Dark text for contrast

    // Error - Consistent with brand red
    error = FlatError, // Bold red error
    onError = FlatTextOnColor, // White text on error
    errorContainer = FlatRedPastel, // Light error background
    onErrorContainer = FlatTextPrimary, // Dark text on error container

    // Surface Colors - Clean and Flat
    surface = FlatCardBackground, // Pure white surface
    surfaceDim = FlatGrayBackground, // Light gray - minimal variation
    surfaceBright = FlatMetroWhite, // Pure white - flat
    surfaceContainer = FlatGrayBackground, // Light gray container - flat
    surfaceContainerHigh = FlatMetroWhite, // Pure white - no elevation effect
    onSurface = FlatTextPrimary, // Dark text - high contrast
    onSurfaceVariant = FlatTextSecondary, // Medium gray - clear hierarchy

    // Background - Pure and Clean
    background = FlatMetroWhite, // Pure white background
    onBackground = FlatTextPrimary, // Dark text on white - maximum contrast

    // Outline - Clean and Bold
    outline = FlatGrayMedium, // Medium gray - bold outline
    outlineVariant = FlatGrayLight, // Light gray - subtle variant

    // Inverse Colors - High Contrast
    inverseSurface = FlatTextPrimary, // Dark surface for contrast
    inverseOnSurface = FlatMetroWhite, // White text on dark
    inversePrimary = FlatBlueLight, // Light blue for dark backgrounds

    // Surface Tint - Minimal for Flat Design
    surfaceTint = FlatMetroNavy // Navy tint - minimal application
)

private val FlatDarkColors = darkColorScheme(
    // Primary - Bright Blue for Dark Mode
    primary = FlatMetroBlue, // Bright blue - vibrant in dark
    onPrimary = FlatMetroWhite, // Pure white text
    primaryContainer = FlatNavyDark, // Dark navy container
    onPrimaryContainer = FlatBlueLight, // Light blue text on dark

    // Secondary - Light Blue Accent
    secondary = FlatNavyLight, // Light navy - good contrast
    onSecondary = FlatTextOnDark, // White text on secondary
    secondaryContainer = FlatNavyDark, // Dark navy container
    onSecondaryContainer = FlatBlueLight, // Light blue on dark container

    // Tertiary - Light Red for Dark
    tertiary = FlatRedLight, // Light red - visible in dark
    onTertiary = FlatTextOnDark, // White text on red
    tertiaryContainer = FlatRedDark, // Dark red container
    onTertiaryContainer = FlatRedLight, // Light red text on dark

    // Error - Light Red for Dark Mode
    error = FlatErrorLight, // Light red error
    onError = FlatTextOnDark, // White text on error
    errorContainer = FlatRedDark, // Dark error background
    onErrorContainer = FlatRedLight, // Light red text on dark error

    // Surface Colors - Pure Black for Flat Design
    surface = Color(0xFF000000), // Pure black surface - flat
    surfaceDim = Color(0xFF111111), // Very dark gray - minimal variation
    surfaceBright = FlatGrayDark, // Dark gray - brighter variant
    surfaceContainer = Color(0xFF111111), // Dark container - flat
    surfaceContainerHigh = FlatGrayDark, // Dark gray - no elevation effect
    onSurface = FlatMetroWhite, // White text - high contrast
    onSurfaceVariant = FlatGrayLight, // Light gray - clear hierarchy

    // Background - Pure Black
    background = Color(0xFF000000), // Pure black background
    onBackground = FlatMetroWhite, // White text on black - maximum contrast

    // Outline - Bold in Dark Mode
    outline = FlatGrayMedium, // Medium gray - visible outline
    outlineVariant = FlatGrayDark, // Dark gray - subtle variant

    // Inverse Colors - High Contrast
    inverseSurface = FlatMetroWhite, // White surface for contrast
    inverseOnSurface = FlatTextPrimary, // Dark text on white
    inversePrimary = FlatMetroNavy, // Navy for light backgrounds

    // Surface Tint - Minimal for Flat Design
    surfaceTint = FlatMetroBlue // Blue tint - minimal application
)
// Light Color Scheme
private val ShadCNLightColorScheme = lightColorScheme(
    primary = ShadCNLightColors.primary,
    onPrimary = ShadCNLightColors.primaryForeground,
    primaryContainer = ShadCNLightColors.primary.copy(alpha = 0.1f),
    onPrimaryContainer = ShadCNLightColors.foreground,

    secondary = ShadCNLightColors.secondary,
    onSecondary = ShadCNLightColors.secondaryForeground,
    secondaryContainer = ShadCNLightColors.secondary.copy(alpha = 0.1f),
    onSecondaryContainer = ShadCNLightColors.foreground,

    tertiary = ShadCNLightColors.accent,
    onTertiary = ShadCNLightColors.accentForeground,
    tertiaryContainer = ShadCNLightColors.accent.copy(alpha = 0.1f),
    onTertiaryContainer = ShadCNLightColors.foreground,

    error = ShadCNLightColors.destructive,
    onError = ShadCNLightColors.destructiveForeground,
    errorContainer = ShadCNLightColors.destructive.copy(alpha = 0.1f),
    onErrorContainer = ShadCNLightColors.foreground,

    background = ShadCNLightColors.background,
    onBackground = ShadCNLightColors.foreground,

    surface = ShadCNLightColors.card,
    onSurface = ShadCNLightColors.cardForeground,
    surfaceVariant = ShadCNLightColors.muted,
    onSurfaceVariant = ShadCNLightColors.mutedForeground,

    outline = ShadCNLightColors.border,
    outlineVariant = ShadCNLightColors.border.copy(alpha = 0.3f),

    scrim = Color.Black.copy(alpha = 0.32f),

    inverseSurface = ShadCNLightColors.foreground,
    inverseOnSurface = ShadCNLightColors.background,
    inversePrimary = ShadCNLightColors.primary,

    surfaceDim = ShadCNLightColors.muted,
    surfaceBright = ShadCNLightColors.background,
    surfaceContainer = ShadCNLightColors.card,
    surfaceContainerHigh = ShadCNLightColors.card,
    surfaceContainerHighest = ShadCNLightColors.card,
    surfaceContainerLow = ShadCNLightColors.card,
    surfaceContainerLowest = ShadCNLightColors.card,

    surfaceTint = ShadCNLightColors.primary
)

// Dark Color Scheme
private val ShadCNDarkColorScheme = darkColorScheme(
    primary = ShadCNDarkColors.primary,
    onPrimary = ShadCNDarkColors.primaryForeground,
    primaryContainer = ShadCNDarkColors.primary.copy(alpha = 0.1f),
    onPrimaryContainer = ShadCNDarkColors.foreground,

    secondary = ShadCNDarkColors.secondary,
    onSecondary = ShadCNDarkColors.secondaryForeground,
    secondaryContainer = ShadCNDarkColors.secondary.copy(alpha = 0.1f),
    onSecondaryContainer = ShadCNDarkColors.foreground,

    tertiary = ShadCNDarkColors.accent,
    onTertiary = ShadCNDarkColors.accentForeground,
    tertiaryContainer = ShadCNDarkColors.accent.copy(alpha = 0.1f),
    onTertiaryContainer = ShadCNDarkColors.foreground,

    error = ShadCNDarkColors.destructive,
    onError = ShadCNDarkColors.destructiveForeground,
    errorContainer = ShadCNDarkColors.destructive.copy(alpha = 0.1f),
    onErrorContainer = ShadCNDarkColors.foreground,

    background = ShadCNDarkColors.background,
    onBackground = ShadCNDarkColors.foreground,

    surface = ShadCNDarkColors.card,
    onSurface = ShadCNDarkColors.cardForeground,
    surfaceVariant = ShadCNDarkColors.muted,
    onSurfaceVariant = ShadCNDarkColors.mutedForeground,

    outline = ShadCNDarkColors.border,
    outlineVariant = ShadCNDarkColors.border.copy(alpha = 0.3f),

    scrim = Color.Black.copy(alpha = 0.32f),

    inverseSurface = ShadCNDarkColors.foreground,
    inverseOnSurface = ShadCNDarkColors.background,
    inversePrimary = ShadCNDarkColors.primary,

    surfaceDim = ShadCNDarkColors.muted,
    surfaceBright = ShadCNDarkColors.background,
    surfaceContainer = ShadCNDarkColors.card,
    surfaceContainerHigh = ShadCNDarkColors.card,
    surfaceContainerHighest = ShadCNDarkColors.card,
    surfaceContainerLow = ShadCNDarkColors.card,
    surfaceContainerLowest = ShadCNDarkColors.card,

    surfaceTint = ShadCNDarkColors.primary
)
@Composable
fun MetrollFlatTheme(
    darkTheme: Boolean = false, // Default to light theme for metro app
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        ShadCNDarkColorScheme
    } else {
        ShadCNLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ShadCNTypography,
        shapes = ShadCNShapes,
        content = content
    )
}

// Legacy theme compatibility - uses flat design colors
@Composable
fun MetrollTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MetrollFlatTheme(
        darkTheme = darkTheme,
        content = content
    )
}

/*
FLAT DESIGN THEME IMPLEMENTATION NOTES:

1. Color Boldness:
   - Used FlatMetroNavy, FlatMetroBlue, FlatMetroRed for bold primary colors
   - Pure white (#FFFFFF) and black (#000000) for maximum contrast
   - No subtle color variations - clear, distinct colors only

2. Surface Treatment:
   - Pure white surfaces in light mode
   - Pure black surfaces in dark mode
   - Minimal surfaceTint to avoid elevation effects
   - Clean, flat containers without visual depth

3. Text Contrast:
   - FlatTextPrimary for high contrast text
   - FlatTextOnColor for text on colored backgrounds
   - Clear hierarchy with FlatTextSecondary

4. Flat Design Benefits:
   - Better performance (no shadow/gradient calculations)
   - Cleaner, more readable interface
   - Consistent with modern flat design trends
   - Better accessibility with high contrast

5. Metro Transit Optimization:
   - Bold colors for quick recognition
   - High contrast for visibility in various lighting
   - Clean interface for easy navigation
   - Consistent color meaning across the app

6. Material Design 3 Compliance:
   - Uses Material Design 3 color scheme structure
   - Implements flat design within MD3 guidelines
   - Maintains semantic color roles
   - Supports both light and dark themes

Usage Example:
```kotlin
@Composable
fun MyApp() {
    MetrollFlatTheme {
        // Your app content here
        // All colors will use flat design principles
    }
}
```
*/
