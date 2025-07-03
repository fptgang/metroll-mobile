package com.vidz.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * HCMC Metro Theme - Material Design 3 compliant
 * Updated for metro transit application with professional color scheme
 * 
 * Color Usage:
 * - primary: Metro Navy Blue - main brand color for headers, primary actions
 * - secondary: Metro Light Blue - secondary actions, links, highlights  
 * - tertiary: Metro Red - accent color, alerts, important indicators
 * - background: Clean white/dark surfaces for metro app
 * - surface: Card backgrounds, dialogs, app bars
 * - surfaceTint: Tinting for layered components
 * - onSurface: Text and icons on surface colors
 */

private val LightColors = lightColorScheme(
    primary = LightPrimaryColor, // Metro Navy Blue
    onPrimary = LightOnPrimaryColor, // White text on navy
    primaryContainer = LightPrimaryContainerColor, // Light blue container
    onPrimaryContainer = LightOnPrimaryContainerColor, // Dark text on light blue
    secondary = LightSecondaryColor, // Metro Light Blue
    onSecondary = LightOnSecondaryColor, // White text on light blue
    secondaryContainer = LightSecondaryContainerColor, // Very light blue
    onSecondaryContainer = LightOnSecondaryContainerColor, // Dark text on light container
    tertiary = LightTertiaryColor, // Metro Red accent
    onTertiary = LightOnTertiaryColor, // White text on red
    tertiaryContainer = LightTertiaryContainerColor, // Light red container
    onTertiaryContainer = LightOnTertiaryContainerColor, // Dark text on light red
    error = LightErrorColor, // Metro red for errors
    onError = LightOnErrorColor, // White text on error
    errorContainer = LightErrorContainerColor, // Light error background
    onErrorContainer = LightOnErrorContainerColor, // Dark text on error container
    inverseSurface = LightInverseSurfaceColor, // Dark surface for contrast
    inverseOnSurface = LightOnInverseSurfaceColor, // Light text on dark surface
    inversePrimary = LightInversePrimaryColor, // Light primary for dark backgrounds
    outline = LightOutlineColor, // Metro gray outlines
    outlineVariant = LightOutlineVariantColor, // Lighter outline variant
    surface = LightSurfaceColor, // Metro white surface
    surfaceDim = LightSurfaceDimColor, // Slightly dimmed surface
    surfaceBright = LightSurfaceBrightColor, // Bright white surface
    surfaceContainer = LightSurfaceContainerColor, // Metro light gray container
    surfaceContainerHigh = LightSurfaceContainerHighColor, // High emphasis container
    onSurface = LightOnSurfaceColor, // Dark text on light surfaces
    onSurfaceVariant = LightOnSurfaceVariantColor, // Medium gray text
    background = LightSurfaceColor, // Clean white background
    onBackground = LightOnSurfaceColor, // Dark text on white background
    surfaceTint = LightPrimaryColor // Navy blue tint for surfaces
)

private val DarkColors = darkColorScheme(
    primary = DarkPrimaryColor, // Metro Light Blue for dark mode
    onPrimary = DarkOnPrimaryColor, // Dark navy text on light blue
    primaryContainer = DarkPrimaryContainerColor, // Dark blue container
    onPrimaryContainer = DarkOnPrimaryContainerColor, // Light text on dark container
    secondary = DarkSecondaryColor, // Metro blue accent for dark
    onSecondary = DarkOnSecondaryColor, // White text on secondary
    secondaryContainer = DarkSecondaryContainerColor, // Dark blue container
    onSecondaryContainer = DarkOnSecondaryContainerColor, // Light text on dark secondary
    tertiary = DarkTertiaryColor, // Metro red light for dark mode
    onTertiary = DarkOnTertiaryColor, // White text on red
    tertiaryContainer = DarkTertiaryContainerColor, // Dark red container
    onTertiaryContainer = DarkOnTertiaryContainerColor, // Light text on dark red
    error = DarkErrorColor, // Light red for dark mode errors
    onError = DarkOnErrorColor, // White text on error
    errorContainer = DarkErrorContainerColor, // Dark error background
    onErrorContainer = DarkOnErrorContainerColor, // Light text on error container
    inverseSurface = DarkInverseSurfaceColor, // Light surface for contrast
    inverseOnSurface = DarkOnInverseSurfaceColor, // Dark text on light surface
    inversePrimary = DarkInversePrimaryColor, // Navy for light backgrounds
    outline = DarkOutlineColor, // Medium gray outlines
    outlineVariant = DarkOutlineVariantColor, // Darker outline variant
    surface = DarkSurfaceColor, // Dark navy surface
    surfaceDim = DarkSurfaceDimColor, // Very dark surface
    surfaceBright = DarkSurfaceBrightColor, // Brighter dark surface
    surfaceContainer = DarkSurfaceContainerColor, // Dark container
    surfaceContainerHigh = DarkSurfaceContainerHighColor, // High emphasis dark container
    onSurface = DarkOnSurfaceColor, // Light text on dark surfaces
    onSurfaceVariant = DarkOnSurfaceVariantColor, // Light gray text
    background = DarkSurfaceColor, // Dark navy background
    onBackground = DarkOnSurfaceColor, // Light text on dark background
    surfaceTint = DarkPrimaryColor // Light blue tint for dark surfaces
)

@Composable
fun MetrollTheme(
    darkTheme: Boolean = false, // Default to light theme for metro app
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColors
    } else {
        LightColors
    }

    MaterialTheme(
        colorScheme = colors,
        content = content,
        typography = Typography,
    )
}
