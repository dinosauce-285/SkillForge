package com.example.skillforge.core.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.example.skillforge.core.designsystem.SkillforgeShapes

@Composable
fun SkillforgeProgressBar(
    progress: Float, // Giá trị từ 0.0f đến 1.0f
    modifier: Modifier = Modifier
) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp) // Độ dày chuẩn của thanh progress
            .clip(SkillforgeShapes.pill), // Bo tròn tuyệt đối 2 đầu thanh
        color = MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
        strokeCap = StrokeCap.Round
    )
}