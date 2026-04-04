package com.example.skillforge.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// translated comment
private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    background = BackgroundColor,
    surface = SurfaceColor,
    onPrimary = Color.White, // translated comment
    onBackground = TextPrimaryColor, // translated comment
    onSurface = TextPrimaryColor,     // translated comment
    onSurfaceVariant = TextSecondaryColor // translated comment
    // translated comment
)

// translated comment

@Composable
fun SkillforgeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        LightColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        // translated comment
        content = content
    )
}
