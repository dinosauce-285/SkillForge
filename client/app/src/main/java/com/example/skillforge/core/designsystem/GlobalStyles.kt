package com.example.skillforge.core.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

/**
 * File nay dong vai tro giong global.css trong web:
 * giu cac token UI dung chung cho toan app.
 */
object SkillforgeSpacing {
    val xSmall = 4.dp
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val xLarge = 32.dp
    val xxLarge = 40.dp
}

object SkillforgeRadius {
    val small = 8.dp
    val medium = 12.dp
    val large = 20.dp
    val xLarge = 24.dp
    val pill = 999.dp
}

object SkillforgeElevation {
    val low = 2.dp
    val medium = 8.dp
    val high = 16.dp
}

object SkillforgeShapes {
    val input = RoundedCornerShape(SkillforgeRadius.medium)
    val button = RoundedCornerShape(SkillforgeRadius.medium)
    val card = RoundedCornerShape(SkillforgeRadius.xLarge)
    val sheet = RoundedCornerShape(
        topStart = SkillforgeRadius.xLarge,
        topEnd = SkillforgeRadius.xLarge,
    )
    val chip = RoundedCornerShape(SkillforgeRadius.pill)
}

object SkillforgeLayout {
    val screenHorizontalPadding = SkillforgeSpacing.large
    val screenVerticalPadding = SkillforgeSpacing.large
    val sectionGap = SkillforgeSpacing.large
    val cardContentPadding = SkillforgeSpacing.large
    val listItemGap = SkillforgeSpacing.medium
}

object SkillforgeComponentSizes {
    val buttonHeight = 56.dp
    val textFieldHeight = 56.dp
    val topBarHeight = 64.dp
    val thumbnailHeight = 180.dp
}

@Composable
fun skillforgeCardColors() = CardDefaults.cardColors(
    containerColor = MaterialTheme.colorScheme.surface,
)

@Composable
fun skillforgeElevatedCardColors() = CardDefaults.elevatedCardColors(
    containerColor = MaterialTheme.colorScheme.surface,
)

@Composable
fun skillforgePrimaryButtonColors() = ButtonDefaults.buttonColors(
    containerColor = MaterialTheme.colorScheme.primary,
    contentColor = MaterialTheme.colorScheme.onPrimary,
)

@Composable
fun skillforgeSecondaryButtonColors() = OutlinedButtonDefaults.colors(
    contentColor = MaterialTheme.colorScheme.onSurface,
)
