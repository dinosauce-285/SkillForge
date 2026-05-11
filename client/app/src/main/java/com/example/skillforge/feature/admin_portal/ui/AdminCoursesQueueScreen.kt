package com.example.skillforge.feature.admin_portal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.skillforge.core.designsystem.SkillforgeLayout
import com.example.skillforge.core.designsystem.SkillforgeShapes
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.components.SafeFlowRow
import com.example.skillforge.core.designsystem.components.SkillforgeCard
import com.example.skillforge.domain.model.Course
import com.example.skillforge.feature.admin_portal.viewmodel.AdminViewModel

@Composable
fun AdminCoursesQueueScreen(
    token: String,
    viewModel: AdminViewModel,
    onBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToCoupons: () -> Unit,
    onNavigateToFinance: () -> Unit,
    onNavigateToPreview: (String) -> Unit
) {
    val queue by viewModel.courseQueue.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchCourseQueue(token)
    }

    AdminScaffold(
        title = "Course Queue",
        selectedTab = AdminTab.Queue,
        onNavigateToDashboard = onNavigateToDashboard,
        onNavigateToUsers = onNavigateToUsers,
        onNavigateToQueue = onNavigateToQueue,
        onNavigateToCoupons = onNavigateToCoupons,
        onNavigateToFinance = onNavigateToFinance,
        onBack = onBack
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading && queue.isEmpty() -> {
                    CourseQueueLoadingCard(modifier = Modifier.align(Alignment.Center))
                }

                !error.isNullOrBlank() && queue.isEmpty() -> {
                    CourseQueueMessageCard(
                        title = "Unable to load course queue",
                        message = error.orEmpty(),
                        isError = true,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(SkillforgeLayout.screenHorizontalPadding)
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = SkillforgeLayout.screenHorizontalPadding,
                            vertical = SkillforgeLayout.screenVerticalPadding
                        ),
                        verticalArrangement = Arrangement.spacedBy(SkillforgeLayout.listItemGap)
                    ) {
                        item {
                            CourseQueueHeaderCard(queueSize = queue.size)
                        }

                        if (!error.isNullOrBlank()) {
                            item {
                                CourseQueueMessageCard(
                                    title = "Some queue data may be outdated",
                                    message = error.orEmpty(),
                                    isError = true
                                )
                            }
                        }

                        if (queue.isEmpty()) {
                            item {
                                CourseQueueMessageCard(
                                    title = "No courses pending approval",
                                    message = "Submitted courses will appear here when instructors request publication.",
                                    isError = false
                                )
                            }
                        } else {
                            item {
                                AdminSectionHeader(
                                    title = "Pending courses",
                                    subtitle = "Review submitted courses before they are published."
                                )
                            }

                            items(queue, key = { it.id }) { course ->
                                CourseQueueCard(
                                    course = course,
                                    onClick = { onNavigateToPreview(course.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseQueueHeaderCard(queueSize: Int) {
    SkillforgeCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(SkillforgeShapes.large)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(SkillforgeSpacing.medium))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Course review queue",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Review instructor submissions and open previews before making a decision.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            SafeFlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalSpacing = SkillforgeSpacing.small,
                verticalSpacing = SkillforgeSpacing.small
            ) {
                CourseMetadataChip(label = "Pending", value = queueSize.toString())
            }
        }
    }
}

@Composable
private fun CourseQueueCard(course: Course, onClick: () -> Unit) {
    SkillforgeCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)
                ) {
                    Text(
                        text = course.title.ifBlank { "Untitled course" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Instructor: ${course.instructor?.fullName?.ifBlank { "Unknown" } ?: "Unknown"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(SkillforgeSpacing.small))
                CourseStatusBadge(status = course.status)
            }

            SafeFlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalSpacing = SkillforgeSpacing.small,
                verticalSpacing = SkillforgeSpacing.small
            ) {
                CourseMetadataChip(
                    label = "Category",
                    value = course.category?.name?.ifBlank { "Unknown" } ?: "Unknown"
                )
                CourseMetadataChip(
                    label = "Level",
                    value = course.level.ifBlank { "Unknown" }.toDisplayLabel()
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onClick,
                    shape = SkillforgeShapes.button,
                    contentPadding = PaddingValues(
                        horizontal = SkillforgeSpacing.medium,
                        vertical = SkillforgeSpacing.small
                    )
                ) {
                    Text("Preview", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun CourseMetadataChip(label: String, value: String) {
    Surface(
        shape = SkillforgeShapes.chip,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f)
    ) {
        Text(
            text = "$label: $value",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CourseStatusBadge(status: String) {
    Surface(
        shape = SkillforgeShapes.chip,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    ) {
        Text(
            text = status.ifBlank { "Pending" }.toDisplayLabel(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AdminSectionHeader(
    title: String,
    subtitle: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CourseQueueLoadingCard(modifier: Modifier = Modifier) {
    SkillforgeCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(SkillforgeLayout.screenHorizontalPadding)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeLayout.cardContentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(SkillforgeSpacing.medium))
            Column {
                Text(
                    text = "Loading course queue",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Fetching submitted courses for review.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CourseQueueMessageCard(
    title: String,
    message: String,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    SkillforgeCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun String.toDisplayLabel(): String {
    return lowercase()
        .split("_", "-", " ")
        .filter { it.isNotBlank() }
        .joinToString(" ") { part ->
            part.replaceFirstChar { char -> char.uppercase() }
        }
}
