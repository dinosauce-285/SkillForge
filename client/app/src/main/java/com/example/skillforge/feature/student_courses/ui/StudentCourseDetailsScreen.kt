package com.example.skillforge.feature.student_courses.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.PlayLesson
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.skillforge.R
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.core.designsystem.PrimaryOrangeLight
import com.example.skillforge.core.designsystem.SkillforgeComponentSizes
import com.example.skillforge.core.designsystem.SkillforgeLayout
import com.example.skillforge.core.designsystem.SkillforgeShapes
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.SkillforgeTheme
import com.example.skillforge.core.designsystem.skillforgeElevatedCardColors
import com.example.skillforge.core.designsystem.skillforgePrimaryButtonColors
import com.example.skillforge.domain.model.CourseDetails
import com.example.skillforge.feature.student_courses.viewmodel.StudentCourseDetailsUiState
import com.example.skillforge.feature.student_courses.viewmodel.StudentCoursesViewModel

@Composable
fun StudentCourseDetailsRoute(
    courseId: String,
    viewModel: StudentCoursesViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.courseDetailsState.collectAsState()

    LaunchedEffect(courseId) {
        viewModel.loadCourseDetails(courseId)
    }

    StudentCourseDetailsScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = { viewModel.loadCourseDetails(courseId, forceReload = true) },
    )
}

@Composable
fun StudentCourseDetailsScreen(
    uiState: StudentCourseDetailsUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> LoadingDetailsState()
            uiState.errorMessage != null -> ErrorDetailsState(
                message = uiState.errorMessage,
                onBack = onBack,
                onRetry = onRetry,
            )

            uiState.course != null -> {
                val course = uiState.course
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentPadding = PaddingValues(
                        horizontal = SkillforgeLayout.screenHorizontalPadding,
                        vertical = SkillforgeLayout.screenVerticalPadding,
                    ),
                    verticalArrangement = Arrangement.spacedBy(SkillforgeLayout.sectionGap),
                ) {
                    item { CourseDetailsHero(course = course, onBack = onBack) }
                    item { CourseOverviewCard(course = course) }
                    if (course.tags.isNotEmpty()) {
                        item { CourseTagsCard(tags = course.tags) }
                    }
                    item { CourseCurriculumCard(course = course) }
                    item { InstructorCard(course = course) }
                }
            }
        }
    }
}

@Composable
private fun CourseDetailsHero(
    course: CourseDetails,
    onBack: () -> Unit,
) {
    ElevatedCard(
        shape = SkillforgeShapes.card,
        colors = CardDefaults.elevatedCardColors(containerColor = PrimaryOrangeLight),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = SkillforgeSpacing.small),
    ) {
        Column(
            modifier = Modifier.padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
                Text(
                    text = course.categoryName.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.84f),
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SkillforgeComponentSizes.thumbnailHeight)
                    .clip(SkillforgeShapes.card)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.18f)),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mock_course_thumbnail),
                    contentDescription = course.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.56f),
                                ),
                            ),
                        ),
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.align(Alignment.Center),
                )
            }

            Text(
                text = course.title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
            )
            if (!course.subtitle.isNullOrBlank()) {
                Text(
                    text = course.subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
                    Text(text = course.averageRating.formatRating(), color = MaterialTheme.colorScheme.onPrimary)
                }
                Text(text = prettyLevel(course.level), color = MaterialTheme.colorScheme.onPrimary)
                Text(text = course.displayPrice(), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}

@Composable
private fun CourseOverviewCard(course: CourseDetails) {
    ElevatedCard(shape = SkillforgeShapes.card, colors = skillforgeElevatedCardColors()) {
        Column(
            modifier = Modifier.padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium),
        ) {
            Text(text = "Overview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(
                text = course.summary ?: "No summary has been added for this course yet.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DetailsStat(title = "Students", value = course.studentCount.toString())
                DetailsStat(title = "Reviews", value = course.reviewCount.toString())
                DetailsStat(title = "Chapters", value = course.chapterCount.toString())
            }
            Button(
                onClick = {},
                colors = skillforgePrimaryButtonColors(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = if (course.isFree) "Start learning" else "Enroll now")
            }
        }
    }
}

@Composable
private fun CourseTagsCard(tags: List<String>) {
    ElevatedCard(shape = SkillforgeShapes.card, colors = skillforgeElevatedCardColors()) {
        Column(
            modifier = Modifier.padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium),
        ) {
            Text(text = "Skills you will touch", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small),
            ) {
                items(tags.size) { index ->
                    AssistChip(
                        onClick = {},
                        label = { Text(text = tags[index]) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    )
                }
            }
        }
    }
}

@Composable
private fun CourseCurriculumCard(course: CourseDetails) {
    ElevatedCard(shape = SkillforgeShapes.card, colors = skillforgeElevatedCardColors()) {
        Column(
            modifier = Modifier.padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium),
        ) {
            Text(text = "Curriculum", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            course.chapters.forEachIndexed { chapterIndex, chapter ->
                Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small)) {
                    Text(
                        text = "Chapter ${chapterIndex + 1} · ${chapter.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    chapter.lessonTitles.forEachIndexed { lessonIndex, lessonTitle ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayLesson,
                                contentDescription = null,
                                tint = PrimaryOrange,
                            )
                            Text(
                                text = "${lessonIndex + 1}. $lessonTitle",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InstructorCard(course: CourseDetails) {
    ElevatedCard(shape = SkillforgeShapes.card, colors = skillforgeElevatedCardColors()) {
        Column(
            modifier = Modifier.padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium),
        ) {
            Text(text = "Instructor", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Row(
                horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                        .padding(SkillforgeSpacing.medium)
                ) {
                    Text(
                        text = course.instructorName.take(1),
                        color = PrimaryOrange,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)) {
                    Text(
                        text = course.instructorName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (!course.instructorGoals.isNullOrBlank()) {
                        Text(text = course.instructorGoals, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            if (course.instructorSkills.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small),
                ) {
                    items(course.instructorSkills.size) { index ->
                        AssistChip(
                            onClick = {},
                            label = { Text(course.instructorSkills[index]) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            ),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailsStat(title: String, value: String) {
    Column {
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PrimaryOrange)
        Text(text = title, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun LoadingDetailsState() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SkillforgeLayout.screenHorizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            CircularProgressIndicator(color = PrimaryOrange)
            Spacer(modifier = Modifier.height(SkillforgeSpacing.medium))
            Text("Loading course details...")
        }
    }
}

@Composable
private fun ErrorDetailsState(
    message: String,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SkillforgeLayout.screenHorizontalPadding),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Unable to load course details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(SkillforgeSpacing.small))
            Text(text = message, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(SkillforgeSpacing.medium))
            Row(horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small)) {
                Button(onClick = onBack) { Text("Back") }
                Button(onClick = onRetry, colors = skillforgePrimaryButtonColors()) { Text("Try again") }
            }
        }
    }
}

private fun CourseDetails.displayPrice(): String {
    return if (isFree || price == 0.0) "Free" else "$" + "%,.0f".format(price)
}

private fun Float.formatRating(): String = String.format("%.1f", this)

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420")
@Composable
private fun StudentCourseDetailsPreview() {
    SkillforgeTheme(darkTheme = false, dynamicColor = false) {
        StudentCourseDetailsScreen(
            uiState = StudentCourseDetailsUiState(course = StudentCourseMockData.courseDetails),
            onBack = {},
            onRetry = {},
        )
    }
}
