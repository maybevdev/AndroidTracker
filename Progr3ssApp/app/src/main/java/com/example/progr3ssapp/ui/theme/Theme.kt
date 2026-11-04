package com.example.progr3ssapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val Progr3ssColorScheme = darkColorScheme(
    primary = Pink,
    onPrimary = Night,
    primaryContainer = Blush,
    onPrimaryContainer = Night,
    secondary = SkyBlue,
    onSecondary = Night,
    secondaryContainer = BabyBlue,
    onSecondaryContainer = Night,
    tertiary = Lavender,
    onTertiary = Night,
    tertiaryContainer = Lilac,
    onTertiaryContainer = Night,
    background = Night,
    onBackground = TextLight,
    surface = Night,
    onSurface = TextLight,
    surfaceVariant = NightSurfaceHigh,
    onSurfaceVariant = TextMuted,
    surfaceContainerLow = NightSurface,
    surfaceContainer = NightSurface,
    surfaceContainerHigh = NightSurfaceHigh,
    surfaceContainerHighest = NightSurfaceHighest,
    outline = Outline,
)

@Composable
fun Progr3ssAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = Progr3ssColorScheme,
        typography = Typography,
        content = content
    )
}