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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.skillforge.domain.model.CourseChapter
import com.example.skillforge.feature.student_courses.viewmodel.StudentCourseDetailsUiState
import com.example.skillforge.feature.student_courses.viewmodel.StudentCoursesViewModel

enum class DetailTab(val title: String) {
    Overview("Overview"),
    Curriculum("Curriculum"),
    Instructor("Instructor"),
    Review("Review")
}

@Composable
fun StudentCourseDetailsRoute(
    courseId: String,
    token: String,
    viewModel: StudentCoursesViewModel,
    onOpenCurriculum: (String) -> Unit,
    onCheckoutSelected: (String) -> Unit,
    onBack: () -> Unit,
) {
    val uiState by viewModel.courseDetailsState.collectAsState()

    LaunchedEffect(courseId, token) {
        viewModel.loadCourseDetails(courseId, token)
    }

    StudentCourseDetailsScreen(
        uiState = uiState,
        onOpenCurriculum = onOpenCurriculum,
        onCheckoutSelected = onCheckoutSelected,
        onToggleFavorite = { viewModel.toggleFavorite(token, courseId) },
        onBack = onBack,
        onRetry = { viewModel.loadCourseDetails(courseId, token, forceReload = true) },
    )
}

@Composable
fun StudentCourseDetailsScreen(
    uiState: StudentCourseDetailsUiState,
    onOpenCurriculum: (String) -> Unit,
    onCheckoutSelected: (String) -> Unit,
    onToggleFavorite: () -> Unit,
    onBack: () -> Unit,
    onRetry: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf(DetailTab.Overview) }

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
                val reviews = uiState.reviews
                val displayRating = if (reviews.isNotEmpty()) reviews.map { it.rating }.average().toFloat() else course.averageRating
                val displayReviewCount = if (reviews.isNotEmpty()) reviews.size else course.reviewCount
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    // Hero section is always visible
                    CourseDetailsHero(course = course, onBack = onBack)

                    TabRow(
                        selectedTabIndex = selectedTab.ordinal,
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = PrimaryOrange,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                                color = PrimaryOrange
                            )
                        },
                        divider = {}
                    ) {
                        DetailTab.entries.forEach { tab ->
                            Tab(
                                selected = selectedTab == tab,
                                onClick = { selectedTab = tab },
                                text = {
                                    Text(
                                        text = tab.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                selectedContentColor = PrimaryOrange,
                                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        when (selectedTab) {
                            DetailTab.Overview -> OverviewTabContent(
                                course = course,
                                displayRating = displayRating,
                                displayReviewCount = displayReviewCount,
                                isEnrolled = uiState.isEnrolled,
                                isFavorite = uiState.isFavorite,
                                onOpenCurriculum = onOpenCurriculum,
                                onCheckoutSelected = onCheckoutSelected,
                                onToggleFavorite = onToggleFavorite
                            )
                            DetailTab.Curriculum -> CurriculumTabContent(course = course)
                            DetailTab.Instructor -> InstructorTabContent(course = course)
                            DetailTab.Review -> ReviewTabContent(
                                averageRating = displayRating,
                                reviews = reviews
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OverviewTabContent(
    course: CourseDetails,
    displayRating: Float,
    displayReviewCount: Int,
    isEnrolled: Boolean,
    isFavorite: Boolean,
    onOpenCurriculum: (String) -> Unit,
    onCheckoutSelected: (String) -> Unit,
    onToggleFavorite: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(SkillforgeLayout.screenHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.large)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)) {
                Spacer(modifier = Modifier.height(SkillforgeSpacing.small))
                Text(
                    text = "About this course",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = course.summary ?: "No description available.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        item {
            ElevatedCard(
                shape = SkillforgeShapes.card,
                colors = skillforgeElevatedCardColors()
            ) {
                Column(
                    modifier = Modifier.padding(SkillforgeLayout.cardContentPadding),
                    verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
                ) {
                    Text(text = "Course Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Price", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(coursePrice(course), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryOrange)
                        }
                        Column {
                            val lessonCount = course.chapters.sumOf { it.lessons.size }
                            Text("Lessons", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$lessonCount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            val quizCount = course.chapters.sumOf { it.quizzes.size }
                            Text("Quizzes", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$quizCount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = SkillforgeSpacing.small), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("Instructor", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(course.instructorName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Rating", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(16.dp))
                                Text(formatRating(displayRating), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("($displayReviewCount)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        item {
            val canOpenCurriculum = isEnrolled || course.isFree
            Button(
                onClick = {
                    if (canOpenCurriculum) {
                        onOpenCurriculum(course.id)
                    } else {
                        onCheckoutSelected(course.id)
                    }
                },
                colors = skillforgePrimaryButtonColors(),
                modifier = Modifier.fillMaxWidth(),
                shape = SkillforgeShapes.button
            ) {
                Text(
                    text = if (canOpenCurriculum) "Open curriculum" else "Checkout",
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(SkillforgeSpacing.medium))

            androidx.compose.material3.OutlinedButton(
                onClick = onToggleFavorite,
                modifier = Modifier.fillMaxWidth(),
                shape = SkillforgeShapes.button,
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isFavorite) MaterialTheme.colorScheme.error else PrimaryOrange
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, 
                    if (isFavorite) MaterialTheme.colorScheme.error else PrimaryOrange
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        if (isFavorite) "Remove from Wishlist" else "Add to Wishlist",
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(SkillforgeSpacing.large))
        }
    }
}

@Composable
private fun CurriculumTabContent(course: CourseDetails) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(SkillforgeLayout.screenHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
    ) {
        item {
            Spacer(modifier = Modifier.height(SkillforgeSpacing.small))
            Text(
                text = "Course Content",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${course.chapters.size} chapters • ${course.chapters.sumOf { it.lessons.size }} lessons",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(course.chapters.size) { index ->
            val chapter = course.chapters[index]
            var expanded by remember { mutableStateOf(true) }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                shape = SkillforgeShapes.card
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                            .padding(SkillforgeSpacing.medium),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Chapter ${index + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryOrange,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = chapter.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null
                        )
                    }

                    if (expanded) {
                        Column(
                            modifier = Modifier.padding(horizontal = SkillforgeSpacing.medium, vertical = SkillforgeSpacing.small),
                            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small)
                        ) {
                            chapter.lessons.forEachIndexed { lIndex, lesson ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "${lIndex + 1}. ${lesson.title}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            chapter.quizzes.forEachIndexed { qIndex, quiz ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Quiz,
                                        contentDescription = null,
                                        tint = PrimaryOrange,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Quiz: ${quiz.title}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(SkillforgeSpacing.medium))
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(SkillforgeSpacing.large))
        }
    }
}

@Composable
private fun InstructorTabContent(course: CourseDetails) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(SkillforgeLayout.screenHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.large)
    ) {
        item {
            Spacer(modifier = Modifier.height(SkillforgeSpacing.small))
            InstructorCard(course = course)
        }

        if (course.instructorSkills.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)) {
                    Text(text = "Instructor Skills", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small)
                    ) {
                        items(course.instructorSkills) { skill ->
                            AssistChip(
                                onClick = {},
                                label = { Text(skill) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                )
                            )
                        }
                    }
                }
            }
        }

        item {
            // Placeholder for other courses as we don't have the data in current model
            Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)) {
                Text(text = "More from this Instructor", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = SkillforgeShapes.card,
                    colors = skillforgeElevatedCardColors()
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(SkillforgeSpacing.large), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Discover more courses by ${course.instructorName} on their profile.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(SkillforgeSpacing.large))
        }
    }
}

@Composable
private fun ReviewTabContent(
    averageRating: Float,
    reviews: List<com.example.skillforge.data.remote.ReviewResponse>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(SkillforgeLayout.screenHorizontalPadding),
        verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
    ) {
        item {
            Spacer(modifier = Modifier.height(SkillforgeSpacing.small))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = formatRating(averageRating),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryOrange
                    )
                    Row {
                        repeat(5) { i ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (i < averageRating.toInt()) PrimaryOrange else MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = "${reviews.size} reviews",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Simplified rating bars
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(5, 4, 3, 2, 1).forEach { star ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("$star", style = MaterialTheme.typography.labelSmall)
                            val progress = if (reviews.isEmpty()) 0f else reviews.count { it.rating == star }.toFloat() / reviews.size
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.weight(1f).height(4.dp),
                                color = PrimaryOrange,
                                trackColor = MaterialTheme.colorScheme.outlineVariant,
                                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                        }
                    }
                }
            }
        }

        item {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        }

        if (reviews.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(SkillforgeSpacing.large), contentAlignment = Alignment.Center) {
                    Text("No reviews yet for this course.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            items(reviews) { review ->
                ReviewItem(review = review)
                HorizontalDivider(modifier = Modifier.padding(vertical = SkillforgeSpacing.small), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            }
        }
        
        item {
            Spacer(modifier = Modifier.height(SkillforgeSpacing.large))
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
                val thumbnailUrl = course.thumbnailUrl?.takeIf { it.isNotBlank() }

                if (thumbnailUrl != null) {
                    AsyncImage(
                        model = thumbnailUrl,
                        contentDescription = course.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }

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
                    Text(text = formatRating(course.averageRating), color = MaterialTheme.colorScheme.onPrimary)
                }
                Text(text = prettyLevel(course.level), color = MaterialTheme.colorScheme.onPrimary)
                Text(
                    text = coursePrice(course),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
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
                        .padding(SkillforgeSpacing.medium),
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
                LazyRow(horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small)) {
                    items(course.instructorSkills) { skill ->
                        AssistChip(
                            onClick = {},
                            label = { Text(skill) },
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

private fun coursePrice(course: CourseDetails): String {
    return if (course.isFree || course.price == 0.0) "Free" else String.format("$%.2f", course.price)
}

private fun formatRating(value: Float): String = String.format("%.1f", value)

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420")
@Composable
private fun StudentCourseDetailsPreview() {
    SkillforgeTheme(darkTheme = false, dynamicColor = false) {
        StudentCourseDetailsScreen(
            uiState = StudentCourseDetailsUiState(course = StudentCourseMockData.courseDetails),
            onOpenCurriculum = {},
            onCheckoutSelected = {},
            onToggleFavorite = {},
            onBack = {},
            onRetry = {},
        )
    }
}


