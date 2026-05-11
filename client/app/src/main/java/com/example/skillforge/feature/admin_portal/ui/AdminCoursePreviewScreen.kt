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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.skillforge.domain.model.CourseChapter
import com.example.skillforge.feature.admin_portal.viewmodel.AdminViewModel

@Composable
fun AdminCoursePreviewScreen(
    token: String,
    courseId: String,
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val courseStructure by viewModel.coursePreview.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedLevel by remember { mutableStateOf<String?>(null) }
    var showLevelDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(courseId) {
        viewModel.fetchCoursePreview(token, courseId)
    }

    LaunchedEffect(courseStructure) {
        if (selectedLevel == null && courseStructure != null) {
            selectedLevel = courseStructure?.course?.level
        }
    }

    AdminDetailScaffold(
        title = "Course Preview",
        onBack = onBack
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CoursePreviewLoadingCard(modifier = Modifier.align(Alignment.Center))
                }

                !error.isNullOrBlank() -> {
                    CoursePreviewMessageCard(
                        title = "Unable to load course preview",
                        message = error.orEmpty(),
                        isError = true,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(SkillforgeLayout.screenHorizontalPadding)
                    )
                }

                courseStructure != null -> {
                    val structure = courseStructure
                    val course = structure?.course

                    if (course == null) {
                        CoursePreviewMessageCard(
                            title = "Course data unavailable",
                            message = "The selected course could not be prepared for preview.",
                            isError = false,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(SkillforgeLayout.screenHorizontalPadding)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                horizontal = SkillforgeLayout.screenHorizontalPadding,
                                vertical = SkillforgeLayout.screenVerticalPadding
                            ),
                            verticalArrangement = Arrangement.spacedBy(SkillforgeLayout.listItemGap)
                        ) {
                            item {
                                CoursePreviewSummaryCard(course = course)
                            }

                            item {
                                CourseLevelOverrideCard(
                                    selectedLevel = selectedLevel,
                                    expanded = showLevelDropdown,
                                    onExpandedChange = { showLevelDropdown = it },
                                    onLevelSelected = { level ->
                                        selectedLevel = level
                                        showLevelDropdown = false
                                    }
                                )
                            }

                            item {
                                AdminPreviewSectionHeader(
                                    title = "Course content",
                                    subtitle = "Review chapters, lessons, and quizzes included in this submission."
                                )
                            }

                            val chapters = structure.chapters.orEmpty()
                            if (chapters.isEmpty()) {
                                item {
                                    CoursePreviewMessageCard(
                                        title = "No content added",
                                        message = "This course does not include chapters yet.",
                                        isError = false
                                    )
                                }
                            } else {
                                items(chapters, key = { it.id }) { chapter ->
                                    CourseChapterPreviewCard(chapter = chapter)
                                }
                            }

                            item {
                                CourseModerationActionsCard(
                                    onRequestChanges = {
                                        viewModel.moderateCourse(token, courseId, "DRAFT", selectedLevel)
                                        onBack()
                                    },
                                    onReject = {
                                        viewModel.moderateCourse(token, courseId, "REJECTED", selectedLevel)
                                        onBack()
                                    },
                                    onPublish = {
                                        viewModel.moderateCourse(token, courseId, "PUBLISHED", selectedLevel)
                                        onBack()
                                    }
                                )
                            }
                        }
                    }
                }

                else -> {
                    CoursePreviewMessageCard(
                        title = "Loading course data",
                        message = "Preparing the course preview.",
                        isError = false,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(SkillforgeLayout.screenHorizontalPadding)
                    )
                }
            }
        }
    }
}

@Composable
private fun CoursePreviewSummaryCard(course: Course) {
    SkillforgeCard(modifier = Modifier.fillMaxWidth()) {
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
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)
                ) {
                    Text(
                        text = course.title.ifBlank { "Untitled course" },
                        style = MaterialTheme.typography.titleLarge,
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
            }

            SafeFlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalSpacing = SkillforgeSpacing.small,
                verticalSpacing = SkillforgeSpacing.small
            ) {
                CoursePreviewStatusBadge(status = course.status)
                CoursePreviewMetadataChip(
                    label = "Category",
                    value = course.category?.name?.ifBlank { "Unknown" } ?: "Unknown"
                )
                CoursePreviewMetadataChip(
                    label = "Level",
                    value = course.level.ifBlank { "Unknown" }.toDisplayLabel()
                )
            }
        }
    }
}

@Composable
private fun CourseLevelOverrideCard(
    selectedLevel: String?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onLevelSelected: (String) -> Unit
) {
    SkillforgeCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)) {
                Text(
                    text = "Difficulty level",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Override the course level before publishing if the submitted value needs adjustment.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box {
                OutlinedButton(
                    onClick = { onExpandedChange(true) },
                    shape = SkillforgeShapes.button
                ) {
                    Text(selectedLevel?.toDisplayLabel() ?: "Select Level")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { onExpandedChange(false) }
                ) {
                    listOf("BEGINNER", "INTERMEDIATE", "ADVANCED", "ALL_LEVELS").forEach { level ->
                        DropdownMenuItem(
                            text = { Text(level.toDisplayLabel()) },
                            onClick = { onLevelSelected(level) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseChapterPreviewCard(chapter: CourseChapter) {
    SkillforgeCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)) {
                Text(
                    text = chapter.title.ifBlank { "Untitled chapter" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${chapter.lessons.size} lessons, ${chapter.quizzes.size} quizzes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            chapter.lessons.forEach { lesson ->
                CourseContentRow(label = "Lesson", title = lesson.title)
            }

            chapter.quizzes.forEach { quiz ->
                CourseContentRow(label = "Quiz", title = quiz.title)
            }
        }
    }
}

@Composable
private fun CourseContentRow(label: String, title: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = SkillforgeShapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = SkillforgeSpacing.medium,
                    vertical = SkillforgeSpacing.small
                ),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = label,
                modifier = Modifier.weight(0.28f),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(SkillforgeSpacing.small))
            Text(
                text = title.ifBlank { "Untitled item" },
                modifier = Modifier.weight(0.72f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CourseModerationActionsCard(
    onRequestChanges: () -> Unit,
    onReject: () -> Unit,
    onPublish: () -> Unit
) {
    SkillforgeCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)) {
                Text(
                    text = "Admin decision",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Choose the moderation outcome for this submitted course.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small)) {
                OutlinedButton(
                    onClick = onRequestChanges,
                    modifier = Modifier.fillMaxWidth(),
                    shape = SkillforgeShapes.button
                ) {
                    Text("Request Changes")
                }
                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = SkillforgeShapes.button
                ) {
                    Text("Reject")
                }
                Button(
                    onClick = onPublish,
                    modifier = Modifier.fillMaxWidth(),
                    shape = SkillforgeShapes.button
                ) {
                    Text("Publish")
                }
            }
        }
    }
}

@Composable
private fun CoursePreviewMetadataChip(label: String, value: String) {
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
private fun CoursePreviewStatusBadge(status: String) {
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
private fun AdminPreviewSectionHeader(
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
private fun CoursePreviewLoadingCard(modifier: Modifier = Modifier) {
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
                    text = "Loading course preview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Fetching course details and content.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CoursePreviewMessageCard(
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
