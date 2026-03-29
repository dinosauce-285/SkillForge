package com.example.skillforge.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light Color Scheme - Map các màu custom vào đây
private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    background = BackgroundColor,
    surface = SurfaceColor,
    onPrimary = Color.White, // Văn bản trên nền màu chính
    onBackground = TextPrimaryColor, // Văn bản trên nền chính
    onSurface = TextPrimaryColor,     // Văn bản trên bề mặt chính (trong thẻ trắng)
    onSurfaceVariant = TextSecondaryColor // Văn bản phụ trên bề mặt chính
    // Có thể map thêm secondary, error, errorContainer... tùy ý cho đồ án
)

// Có thể định nghĩa thêm DarkColorScheme nếu cần

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
        // typography = SkillforgeTypography, // (Nếu bạn có file Type.kt riêng)
        content = content
    )
}