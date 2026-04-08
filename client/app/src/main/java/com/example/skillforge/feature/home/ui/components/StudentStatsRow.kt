package com.example.skillforge.feature.home.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.skillforge.core.designsystem.SkillforgeLayout
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.components.SkillforgeCard

@Composable
fun StudentStatsRow(
    hoursSpent: Double,
    badgesEarned: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SkillforgeLayout.screenHorizontalPadding)
            .padding(vertical = SkillforgeSpacing.medium),
        horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
    ) {
        // translated comment
        StatCard(
            title = "Time Spent",
            value = "$hoursSpent hrs",
            modifier = Modifier.weight(1f)
        )

        // translated comment
        StatCard(
            title = "Badges Earned",
            value = "$badgesEarned",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    SkillforgeCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeSpacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(SkillforgeSpacing.xSmall))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
