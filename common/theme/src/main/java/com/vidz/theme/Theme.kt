package com.vidz.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


// - primary: The main theme color, typically representing the brand identity.
// - onPrimary: The color of content displayed on the primary color (e.g., text, icons).
// - secondary: A supplementary color used for secondary UI elements or highlights.
// - onSecondary: The color of content displayed on the secondary color.
// - background: The primary background color of the app in Dark Mode.
// - onBackground: The color of content displayed on the background (e.g., text, icons).
// - surface: The background color for UI components like cards, dialogs, and app bars.
// - surfaceTint: Used to add a tint to components layered on the surface color.
// - onSurface: The color of content displayed on the surface color.

private val LightColors = lightColorScheme(
    primary = LightPrimaryColor,
    onPrimary = LightOnPrimaryColor,
    primaryContainer = LightPrimaryContainerColor,
    onPrimaryContainer = LightOnPrimaryContainerColor,
    secondary = LightSecondaryColor,
    onSecondary = LightOnSecondaryColor,
    secondaryContainer = LightSecondaryContainerColor,
    onSecondaryContainer = LightOnSecondaryContainerColor,
    tertiary = LightTertiaryColor,
    onTertiary = LightOnTertiaryColor,
    tertiaryContainer = LightTertiaryContainerColor,
    onTertiaryContainer = LightOnTertiaryContainerColor,
    error = LightErrorColor,
    onError = LightOnErrorColor,
    errorContainer = LightErrorContainerColor,
    onErrorContainer = LightOnErrorContainerColor,
    inverseSurface = LightInverseSurfaceColor,
    inverseOnSurface = LightOnInverseSurfaceColor,
    inversePrimary = LightInversePrimaryColor,
    outline = LightOutlineColor,
    outlineVariant = LightOutlineVariantColor,
    surface = LightSurfaceColor,
    surfaceDim = LightSurfaceDimColor,
    surfaceBright = LightSurfaceBrightColor,
    surfaceContainer = LightSurfaceContainerColor,
    surfaceContainerHigh = LightSurfaceContainerHighColor,
    onSurface = LightOnSurfaceColor,
    onSurfaceVariant = LightOnSurfaceVariantColor,
    background = LightSurfaceContainerHighColor
)

private val DarkColors = darkColorScheme(
    primary = DarkPrimaryColor,
    onPrimary = DarkOnPrimaryColor,
    primaryContainer = DarkPrimaryContainerColor,
    onPrimaryContainer = DarkOnPrimaryContainerColor,
    secondary = DarkSecondaryColor,
    onSecondary = DarkOnSecondaryColor,
    secondaryContainer = DarkSecondaryContainerColor,
    onSecondaryContainer = DarkOnSecondaryContainerColor,
    tertiary = DarkTertiaryColor,
    onTertiary = DarkOnTertiaryColor,
    tertiaryContainer = DarkTertiaryContainerColor,
    onTertiaryContainer = DarkOnTertiaryContainerColor,
    error = DarkErrorColor,
    onError = DarkOnErrorColor,
    errorContainer = DarkErrorContainerColor,
    onErrorContainer = DarkOnErrorContainerColor,
    inverseSurface = DarkInverseSurfaceColor,
    inverseOnSurface = DarkOnInverseSurfaceColor,
    inversePrimary = DarkInversePrimaryColor,
    outline = DarkOutlineColor,
    outlineVariant = DarkOutlineVariantColor,
    surface = DarkSurfaceColor,
    surfaceDim = DarkSurfaceDimColor,
    surfaceBright = DarkSurfaceBrightColor,
    surfaceContainer = DarkSurfaceContainerColor,
    surfaceContainerHigh = DarkSurfaceContainerHighColor,
    onSurface = DarkOnSurfaceColor,
    onSurfaceVariant = DarkOnSurfaceVariantColor,
    background = DarkSurfaceContainerHighColor
)

@Composable
fun MetrollTheme(
    darkTheme: Boolean = true,
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
