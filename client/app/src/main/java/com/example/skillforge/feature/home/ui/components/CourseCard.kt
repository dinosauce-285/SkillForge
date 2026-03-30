package com.example.skillforge.feature.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.skillforge.core.designsystem.*
import com.example.skillforge.domain.model.CourseSummary

@Composable
fun CourseCard(
    course: CourseSummary, // Dùng model thật của bạn
    modifier: Modifier = Modifier
) {
    // Tự tính toán badge Best Seller dựa trên số lượng học viên
    val isBestSeller = course.studentCount > 1000

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = SkillforgeShapes.card,
        colors = skillforgeCardColors(),
        elevation = CardDefaults.cardElevation(defaultElevation = SkillforgeElevation.low)
    ) {
        Column {
            // Phần 1: Ảnh Thumbnail và Badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SkillforgeComponentSizes.thumbnailHeight)
                    .background(Color.LightGray)
            ) {
                // TODO: Load ảnh từ course.thumbnailUrl

                if (isBestSeller) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(SkillforgeSpacing.medium)
                            .clip(RoundedCornerShape(SkillforgeRadius.small))
                            .background(Color.White)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Best Seller",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // Phần 2: Nội dung chữ
            Column(
                modifier = Modifier.padding(SkillforgeLayout.cardContentPadding)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(SkillforgeSpacing.small))
                    Text(
                        text = "$${course.price}",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(SkillforgeSpacing.xSmall))

                Text(
                    text = "By ${course.instructorName}", // Cập nhật biến này
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(SkillforgeSpacing.small))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Star",
                            tint = Color(0xFFFFC107), // RatingStarColor
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(SkillforgeSpacing.xSmall))
                    Text(
                        text = "(${course.reviewCount} reviews)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}