package com.example.skillforge.feature.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skillforge.core.designsystem.SkillforgeLayout
import com.example.skillforge.core.designsystem.SkillforgeShapes
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.components.SkillforgeProgressBar
import com.example.skillforge.feature.home.ui.mock.ActiveCourse

@Composable
fun ActiveCourseList(
    courses: List<ActiveCourse>,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SkillforgeLayout.screenHorizontalPadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Active Courses",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "View All",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }

        Spacer(modifier = Modifier.height(SkillforgeSpacing.medium))

        courses.forEach { course ->
            ActiveCourseItem(course = course)
            Spacer(modifier = Modifier.height(SkillforgeSpacing.small))
        }
    }
}

@Composable
private fun ActiveCourseItem(course: ActiveCourse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SkillforgeShapes.card)
            .background(MaterialTheme.colorScheme.surface)
            .padding(SkillforgeSpacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(SkillforgeShapes.medium)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        )

        Spacer(modifier = Modifier.width(SkillforgeSpacing.medium))

        // Thông tin khóa học & Thanh tiến độ
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = course.title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${course.remainingLessons} lessons left",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(SkillforgeSpacing.small))
            SkillforgeProgressBar(progress = course.progressPercentage)
        }
    }
}