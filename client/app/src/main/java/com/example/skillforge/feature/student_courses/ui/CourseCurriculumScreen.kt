package com.example.skillforge.feature.student_courses.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayLesson
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.core.designsystem.SkillforgeLayout
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.SkillforgeTheme
import com.example.skillforge.domain.model.CourseChapter
import com.example.skillforge.domain.model.CourseDetails
import com.example.skillforge.domain.model.CourseLesson
import com.example.skillforge.feature.student_courses.viewmodel.StudentCoursesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseCurriculumRoute(
    courseId: String,
    token: String,
    viewModel: StudentCoursesViewModel,
    onLessonSelected: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.courseDetailsState.collectAsState()

    LaunchedEffect(courseId, token) {
        viewModel.loadCourseDetails(courseId, token)
    }

    CourseCurriculumScreen(
        course = uiState.course,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        completedLessonIds = uiState.completedLessonIds,
        onLessonSelected = onLessonSelected,
        onNavigateBack = onNavigateBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseCurriculumScreen(
    course: CourseDetails?,
    isLoading: Boolean,
    errorMessage: String?,
    completedLessonIds: List<String> = emptyList(),
    onLessonSelected: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    var expandedChapterIds by rememberSaveable(course?.id) { mutableStateOf(setOf<String>()) }

    LaunchedEffect(course?.id) {
        course?.let {
            expandedChapterIds = it.chapters.map { chapter -> chapter.id }.toSet()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = course?.title ?: "Curriculum",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        when {
            isLoading && course == null -> CurriculumLoadingState(modifier = Modifier.padding(paddingValues))
            errorMessage != null && course == null -> CurriculumErrorState(
                message = errorMessage,
                modifier = Modifier.padding(paddingValues),
            )
            course != null -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = SkillforgeLayout.screenHorizontalPadding,
                        vertical = SkillforgeSpacing.medium,
                    ),
                    verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium),
                ) {
                    item {
                        Text(
                            text = "Course Curriculum",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    item {
                        Text(
                            text = "${course.chapters.size} chapters • ${course.chapters.sumOf { it.lessons.size }} lessons",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    items(course.chapters.size) { index ->
                        val chapter = course.chapters[index]
                        CurriculumChapter(
                            chapter = chapter,
                            chapterNumber = index + 1,
                            expanded = chapter.id in expandedChapterIds,
                            completedLessonIds = completedLessonIds,
                            onToggle = {
                                expandedChapterIds = if (chapter.id in expandedChapterIds) {
                                    expandedChapterIds - chapter.id
                                } else {
                                    expandedChapterIds + chapter.id
                                }
                            },
                            onLessonSelected = onLessonSelected,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CurriculumChapter(
    chapter: CourseChapter,
    chapterNumber: Int,
    expanded: Boolean,
    completedLessonIds: List<String>,
    onToggle: () -> Unit,
    onLessonSelected: (String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(10.dp)
                    .background(PrimaryOrange, RoundedCornerShape(99.dp)),
            )
            Spacer(modifier = Modifier.width(2.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Chapter $chapterNumber",
                        color = PrimaryOrange,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "${chapter.lessons.size} lessons",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (expanded) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .width(2.dp)
                        .height((chapter.lessons.size * 58).dp)
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
                )
                Column(
                    modifier = Modifier
                        .padding(start = 22.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    chapter.lessons.forEachIndexed { lessonIndex, lesson ->
                        CurriculumLessonRow(
                            lesson = lesson,
                            lessonNumber = lessonIndex + 1,
                            isCompleted = lesson.id in completedLessonIds,
                            onClick = { onLessonSelected(lesson.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CurriculumLessonRow(
    lesson: CourseLesson,
    lessonNumber: Int,
    isCompleted: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Transparent)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(12.dp)
                .height(2.dp)
                .background(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(99.dp),
                ),
        )
        Icon(
            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.PlayLesson,
            contentDescription = null,
            tint = if (isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Lesson $lessonNumber",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = lesson.title,
                fontWeight = FontWeight.Medium,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun CurriculumLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = PrimaryOrange)
    }
}

@Composable
private fun CurriculumErrorState(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_6")
@Composable
private fun CourseCurriculumScreenPreview() {
    SkillforgeTheme {
        CourseCurriculumScreen(
            course = StudentCourseMockData.courseDetails,
            isLoading = false,
            errorMessage = null,
            completedLessonIds = emptyList(),
            onLessonSelected = {},
            onNavigateBack = {},
        )
    }
}
