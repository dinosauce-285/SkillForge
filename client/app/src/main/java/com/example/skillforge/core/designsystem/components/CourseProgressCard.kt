package com.example.skillforge.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.skillforge.R
import com.example.skillforge.core.designsystem.RatingStarColor
import com.example.skillforge.core.designsystem.SkillforgeShapes
import com.example.skillforge.core.designsystem.SkillforgeSpacing

@Composable
fun CourseProgressCard(
    thumbnailUrl: String?,
    title: String,
    instructorName: String?,
    progressPercent: Int,
    lessonsLeft: Int?,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    showRateAction: Boolean = false,
    onRateClick: (() -> Unit)? = null,
    showContinueAction: Boolean = true,
    onContinueClick: (() -> Unit)? = null,
    showCertificateAction: Boolean = false,
    onCertificateClick: (() -> Unit)? = null,
) {
    val safeTitle = title.ifBlank { "Untitled course" }
    val safeInstructor = instructorName?.takeIf { it.isNotBlank() }
    val clampedProgressPercent = progressPercent.coerceIn(0, 100)
    val progress = clampedProgressPercent / 100f
    val safeLessonsLeft = lessonsLeft?.coerceAtLeast(0)

    ElevatedCard(
        onClick = onClick ?: {},
        enabled = onClick != null,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = SkillforgeShapes.card,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CardContentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = thumbnailUrl,
                placeholder = painterResource(id = R.drawable.mock_course_thumbnail),
                error = painterResource(id = R.drawable.mock_course_thumbnail),
                contentDescription = "Course Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(ThumbnailSize)
                    .clip(SkillforgeShapes.medium)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            )

            Spacer(modifier = Modifier.width(SkillforgeSpacing.medium))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = safeTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (safeInstructor != null) {
                    Text(
                        text = safeInstructor,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (clampedProgressPercent == 100) {
                            "Completed"
                        } else {
                            "$clampedProgressPercent% Done"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    safeLessonsLeft?.let { remaining ->
                        Text(
                            text = if (remaining == 0) {
                                "All lessons completed"
                            } else {
                                "$remaining lessons left"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = if (remaining == 0) CompletedGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                SkillforgeProgressBar(progress = progress)
            }

            CourseProgressActions(
                showRateAction = showRateAction,
                onRateClick = onRateClick,
                showContinueAction = showContinueAction,
                onContinueClick = onContinueClick ?: onClick,
                showCertificateAction = showCertificateAction,
                onCertificateClick = onCertificateClick
            )
        }
    }
}

@Composable
private fun CourseProgressActions(
    showRateAction: Boolean,
    onRateClick: (() -> Unit)?,
    showContinueAction: Boolean,
    onContinueClick: (() -> Unit)?,
    showCertificateAction: Boolean,
    onCertificateClick: (() -> Unit)?,
) {
    if (!showRateAction && !showContinueAction && !showCertificateAction) return

    Spacer(modifier = Modifier.width(SkillforgeSpacing.small))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small)
    ) {
        if (showRateAction) {
            IconButton(
                onClick = { onRateClick?.invoke() },
                enabled = onRateClick != null,
                modifier = Modifier.size(ActionSize)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rate Course",
                    tint = RatingStarColor,
                    modifier = Modifier.size(ActionIconSize)
                )
            }
        } else if (showContinueAction || showCertificateAction) {
            Box(modifier = Modifier.size(ActionSize))
        }

        if (showCertificateAction) {
            IconButton(
                onClick = { onCertificateClick?.invoke() },
                enabled = onCertificateClick != null,
                modifier = Modifier.size(ActionSize)
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Preview Certificate",
                    tint = CompletedGreen,
                    modifier = Modifier.size(ActionIconSize)
                )
            }
        } else if (showContinueAction) {
            IconButton(
                onClick = { onContinueClick?.invoke() },
                enabled = onContinueClick != null,
                modifier = Modifier.size(ActionSize)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircleOutline,
                    contentDescription = "Resume",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(ActionIconSize)
                )
            }
        }
    }
}

private val ThumbnailSize = 80.dp
private val ActionSize = 36.dp
private val ActionIconSize = 24.dp
private val CardContentPadding = PaddingValues(SkillforgeSpacing.medium)
private val CompletedGreen = Color(0xFF4CAF50)
