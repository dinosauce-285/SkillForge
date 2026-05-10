package com.example.skillforge.feature.home.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.skillforge.core.designsystem.SkillforgeLayout
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.components.CourseProgressCard
import com.example.skillforge.domain.model.ActiveCourse

@Composable
fun ActiveCourseList(
    courses: List<ActiveCourse>,
    onViewAllClick: () -> Unit,
    onCourseClick: (ActiveCourse) -> Unit,
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
            CourseProgressCard(
                thumbnailUrl = course.thumbnailUrl,
                title = course.title,
                instructorName = course.instructorName,
                progressPercent = course.percentage,
                lessonsLeft = course.totalLessons - course.completedLessons,
                onClick = { onCourseClick(course) },
                onContinueClick = { onCourseClick(course) },
                showRateAction = false,
                showContinueAction = true
            )
            Spacer(modifier = Modifier.height(SkillforgeSpacing.small))
        }
    }
}
