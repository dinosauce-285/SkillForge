package com.example.skillforge.feature.home.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage // NHỚ IMPORT COIL VÀO ĐỂ LOAD HÌNH
import com.example.skillforge.core.designsystem.SkillforgeLayout
import com.example.skillforge.core.designsystem.SkillforgeShapes
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.components.SkillforgeCard
import com.example.skillforge.core.designsystem.components.SkillforgeProgressBar
import com.example.skillforge.feature.home.ui.mock.ActiveCourse

@Composable
fun ContinueLearningCard(
    course: ActiveCourse,
    modifier: Modifier = Modifier
) {
    SkillforgeCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = SkillforgeSpacing.medium)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = course.thumbnailUrl,
                    contentDescription = "Course Thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(percent = 50),
                    modifier = Modifier
                        .padding(SkillforgeSpacing.medium)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = "MOST RECENT",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(SkillforgeSpacing.medium)
            ) {
                Text(
                    text = "CONTINUE LEARNING",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = course.instructorName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(SkillforgeSpacing.large))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        SkillforgeProgressBar(progress = course.progressPercentage)
                    }
                    Spacer(modifier = Modifier.width(SkillforgeSpacing.medium))
                    Text(
                        text = "${(course.progressPercentage * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(SkillforgeSpacing.large))

                Button(
                    onClick = { /* TODO: Navigate to lesson */ },
                    shape = SkillforgeShapes.button,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    Text("Resume Lesson", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}