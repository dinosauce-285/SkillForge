package com.example.skillforge.core.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun SafeFlowRow(
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = 8.dp,
    verticalSpacing: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints) }

        var currentX = 0
        var currentY = 0
        var rowHeight = 0
        val positions = mutableListOf<Pair<Int, Int>>()

        placeables.forEach { placeable ->
            // If the item doesn't fit horizontally, move it to the next line
            if (currentX + placeable.width > constraints.maxWidth && currentX > 0) {
                currentX = 0
                currentY += rowHeight + verticalSpacing.roundToPx()
                rowHeight = 0
            }
            positions.add(Pair(currentX, currentY))
            currentX += placeable.width + horizontalSpacing.roundToPx()
            rowHeight = max(rowHeight, placeable.height)
        }

        val totalHeight = if (placeables.isEmpty()) 0 else currentY + rowHeight

        layout(constraints.maxWidth, totalHeight) {
            placeables.forEachIndexed { index, placeable ->
                val (x, y) = positions[index]
                placeable.placeRelative(x, y)
            }
        }
    }
}