package com.example.skillforge.core.designsystem.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.skillforge.core.designsystem.SkillforgeShapes

@Composable
fun SkillforgeCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardColors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    )
    val cardElevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    val shape = SkillforgeShapes.card // translated comment

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors = cardColors,
            elevation = cardElevation,
            content = content
        )
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = cardColors,
            elevation = cardElevation,
            content = content
        )
    }
}
