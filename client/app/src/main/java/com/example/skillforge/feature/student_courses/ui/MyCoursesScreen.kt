package com.example.skillforge.feature.student_courses.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
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
import androidx.compose.ui.graphics.Color
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
import com.example.skillforge.domain.model.ActiveCourse
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skillforge.feature.home.viewmodel.HomeViewModel
import com.example.skillforge.feature.home.viewmodel.HomeViewModelFactory
import androidx.compose.ui.platform.LocalContext
import com.example.skillforge.feature.student_courses.viewmodel.ReviewState
import com.example.skillforge.feature.student_courses.viewmodel.ReviewViewModel

sealed interface MyCoursesState {
    data object Loading : MyCoursesState
    data class Success(val courses: List<ActiveCourse>) : MyCoursesState
    data class Error(val message: String) : MyCoursesState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyCoursesScreen(
    token: String = "",
    reviewViewModel: ReviewViewModel,
    onNavigateBack: () -> Unit,
    onCourseClick: (String) -> Unit,
) {

    val context = LocalContext.current
    val appContainer = (context.applicationContext as? com.example.skillforge.SkillforgeApplication)?.container

    if (appContainer == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error loading courses", color = MaterialTheme.colorScheme.error)
        }
        return
    }

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(appContainer.progressRepository)
    )
    val homeUiState by homeViewModel.uiState.collectAsState()

    val reviewState by reviewViewModel.uiState.collectAsState()
    var courseToReview by remember { mutableStateOf<ActiveCourse?>(null) }

    LaunchedEffect(reviewState) {
        if (reviewState is ReviewState.Success) {
            Toast.makeText(context, "Review submitted successfully!", Toast.LENGTH_SHORT).show()
            courseToReview = null // Đóng Dialog
            reviewViewModel.resetState() // Reset để lần sau rate tiếp
        } else if (reviewState is ReviewState.Error) {
            val msg = (reviewState as ReviewState.Error).message
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
            reviewViewModel.resetState()
        }
    }

    if (courseToReview != null) {
        CourseReviewDialog(
            courseTitle = courseToReview!!.title,
            onDismiss = { courseToReview = null },
            onSubmit = { rating, review ->
                reviewViewModel.submitReview(token, courseToReview!!.courseId, rating, review)
            }
        )
    }

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
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when (homeUiState) {
            is com.example.skillforge.feature.home.viewmodel.HomeUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is com.example.skillforge.feature.home.viewmodel.HomeUiState.Error -> {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (homeUiState as com.example.skillforge.feature.home.viewmodel.HomeUiState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            is com.example.skillforge.feature.home.viewmodel.HomeUiState.Success -> {
                val myCourses = (homeUiState as com.example.skillforge.feature.home.viewmodel.HomeUiState.Success).dashboard.courses

                if (myCourses.isEmpty()) {
                    EmptyMyCoursesState(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
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
                        items(myCourses, key = { it.courseId }) { course ->
                            MyCourseCard(
                                course = course,
                                onClick = { onCourseClick(course.courseId) },
                                onRateClick = { courseToReview = course }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MyCourseCard(
    course: ActiveCourse,
    onClick: () -> Unit,
    onRateClick: () -> Unit,
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
            // --- Thumbnail ---
            AsyncImage(
                model = course.thumbnailUrl,
                placeholder = androidx.compose.ui.res.painterResource(id = com.example.skillforge.R.drawable.mock_course_thumbnail),
                contentDescription = "Course Thumbnail",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(SkillforgeShapes.medium)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            )

            Spacer(modifier = Modifier.width(SkillforgeSpacing.medium))

            // --- Info & Progress ---
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

                // --- TEXT TRẠNG THÁI ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (course.percentage == 100) "Completed" else "${course.percentage}% Done",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    val remaining = course.totalLessons - course.completedLessons
                    if (remaining > 0) {
                        Text(
                            text = "$remaining lessons left",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "All lessons completed",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4CAF50) // Xanh lá cây báo hoàn thành
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                SkillforgeProgressBar(progress = course.percentage / 100f)
            }

            Spacer(modifier = Modifier.width(SkillforgeSpacing.small))

            // --- ACTION BUTTONS (Đưa nút Đánh giá lên đây) ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Nút Đánh giá (Ngôi sao vàng) - Luôn hiển thị
                IconButton(
                    onClick = onRateClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rate Course",
                        tint = Color(0xFFFFC107), // Màu vàng Gold
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Nút Tiếp tục học (Play)
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

//@Preview(name = "My Courses Screen", showBackground = true, showSystemUi = true, device = "id:pixel_6")
//@Composable
//fun MyCoursesScreenPreview() {
//    SkillforgeTheme {
//        MyCoursesScreen(
//            onNavigateBack = {},
//            onCourseClick = {},
//        )
//    }
//}

@Composable
fun CourseReviewDialog(
    courseTitle: String,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, review: String) -> Unit
) {
    var rating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rate Course", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(courseTitle, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Star Rating
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Star $i",
                            tint = Color(0xFFFFC107), // Golden yellow
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { rating = i }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Write your review (optional)") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(rating, reviewText) },
                enabled = rating > 0, // Must select at least 1 star
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}