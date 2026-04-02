package com.example.skillforge.feature.student_courses.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.skillforge.core.designsystem.SkillforgeLayout
import com.example.skillforge.core.designsystem.SkillforgeShapes
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.SkillforgeTheme
import com.example.skillforge.core.designsystem.components.SkillforgeProgressBar
import com.example.skillforge.feature.home.ui.mock.ActiveCourse
import com.example.skillforge.feature.home.ui.mock.HomeMockData
import com.example.skillforge.feature.student_courses.ui.components.StudentBottomNavigationBar
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCoursesScreen(
    onNavigateBack: () -> Unit,
    onCourseClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Courses",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            StudentBottomNavigationBar(currentRoute = "Learning")
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        // For demonstration, we use the mock data from HomeMockData
        val myCourses = listOf(HomeMockData.mockMostRecentCourse) + HomeMockData.mockActiveCourses

        if (myCourses.isEmpty()) {
            EmptyMyCoursesState(
                modifier = Modifier.padding(paddingValues).fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(
                    start = SkillforgeLayout.screenHorizontalPadding,
                    end = SkillforgeLayout.screenHorizontalPadding,
                    top = SkillforgeSpacing.medium,
                    bottom = SkillforgeSpacing.large
                ),
                verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
            ) {
                items(myCourses, key = { it.id }) { course ->
                    MyCourseCard(
                        course = course,
                        onClick = { onCourseClick(course.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun MyCourseCard(
    course: ActiveCourse,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = SkillforgeShapes.card,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeSpacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            AsyncImage(
                model = com.example.skillforge.R.drawable.mock_course_thumbnail,
                placeholder = androidx.compose.ui.res.painterResource(id = com.example.skillforge.R.drawable.mock_course_thumbnail),
                contentDescription = "Course Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(SkillforgeShapes.medium)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            )

            Spacer(modifier = Modifier.width(SkillforgeSpacing.medium))

            // Info & Progress
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = course.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = course.instructorName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(SkillforgeSpacing.small))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (course.progressPercentage == 1f) "Completed" else "${(course.progressPercentage * 100).toInt()}% Done",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    if (course.remainingLessons > 0) {
                        Text(
                            text = "${course.remainingLessons} lessons left",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                SkillforgeProgressBar(progress = course.progressPercentage)
            }
            
            Spacer(modifier = Modifier.width(SkillforgeSpacing.small))
            
            // Action button
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircleOutline,
                    contentDescription = "Resume",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyMyCoursesState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "You haven't enrolled in any courses yet.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(name = "My Courses Screen", showBackground = true, showSystemUi = true, device = "id:pixel_6")
@Composable
fun MyCoursesScreenPreview() {
    SkillforgeTheme {
        MyCoursesScreen(
            onNavigateBack = {},
            onCourseClick = {}
        )
    }
}
