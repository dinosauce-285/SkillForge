package com.example.skillforge.feature.student_courses.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.skillforge.R
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.core.designsystem.SkillforgeComponentSizes
import com.example.skillforge.core.designsystem.SkillforgeLayout
import com.example.skillforge.core.designsystem.SkillforgeShapes
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.SkillforgeTheme
import com.example.skillforge.core.designsystem.skillforgeElevatedCardColors
import com.example.skillforge.core.designsystem.skillforgePrimaryButtonColors
import com.example.skillforge.domain.model.AuthSession
import com.example.skillforge.domain.model.AuthUser
import com.example.skillforge.domain.model.CourseSummary
import com.example.skillforge.feature.student_courses.ui.components.StudentBottomNavigationBar
import com.example.skillforge.feature.student_courses.viewmodel.StudentCourseListUiState
import com.example.skillforge.feature.student_courses.viewmodel.StudentCoursesViewModel

@Composable
fun StudentCourseListingRoute(
    session: AuthSession,
    viewModel: StudentCoursesViewModel,
    onCourseSelected: (String) -> Unit,
    onLogout: () -> Unit,
) {
    val uiState by viewModel.courseListState.collectAsState()

    StudentCourseListingScreen(
        session = session,
        uiState = uiState,
        onRetry = viewModel::loadCourses,
        onCourseSelected = onCourseSelected,
        onLogout = onLogout,
    )
}

@Composable
fun StudentCourseListingScreen(
    session: AuthSession,
    uiState: StudentCourseListUiState,
    onRetry: () -> Unit,
    onCourseSelected: (String) -> Unit,
    onLogout: () -> Unit,
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val categories = remember(uiState.courses) {
        buildList {
            add("All")
            addAll(uiState.courses.map { it.categoryName }.distinct())
        }
    }
    var selectedCategory by remember(categories) {
        mutableStateOf(categories.firstOrNull().orEmpty())
    }

    val visibleCourses = remember(uiState.courses, searchQuery, selectedCategory) {
        uiState.courses.filter { course ->
            val matchesCategory = selectedCategory == "All" || selectedCategory.isBlank() || course.categoryName == selectedCategory
            val query = searchQuery.trim()
            val matchesQuery = query.isBlank() ||
                course.title.contains(query, ignoreCase = true) ||
                course.instructorName.contains(query, ignoreCase = true) ||
                course.categoryName.contains(query, ignoreCase = true) ||
                course.tags.any { tag -> tag.contains(query, ignoreCase = true) }

            matchesCategory && matchesQuery
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            StudentBottomNavigationBar(currentRoute = "Discover")
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                start = SkillforgeLayout.screenHorizontalPadding,
                end = SkillforgeLayout.screenHorizontalPadding,
                top = SkillforgeSpacing.medium,
                bottom = innerPadding.calculateBottomPadding() + SkillforgeSpacing.large,
            ),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium),
        ) {
            item {
                DiscoverHeader(
                    fullName = session.user.fullName,
                    onLogout = onLogout,
                )
            }

            item {
                SearchBar(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                )
            }

            if (categories.isNotEmpty()) {
                item {
                    CategoryRow(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it },
                    )
                }
            }

            item {
                SectionHeader(
                    title = "Popular courses",
                    actionLabel = "See all",
                )
            }

            when {
                uiState.isLoading -> item { LoadingStateCard() }
                uiState.errorMessage != null -> item {
                    ErrorStateCard(
                        message = uiState.errorMessage,
                        onRetry = onRetry,
                    )
                }

                visibleCourses.isEmpty() -> item {
                    EmptyStateCard(
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategory,
                    )
                }

                else -> {
                    items(visibleCourses, key = { it.id }) { course ->
                        CourseDiscoveryCard(
                            course = course,
                            onClick = { onCourseSelected(course.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DiscoverHeader(
    fullName: String,
    onLogout: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Column {
                Text(
                    text = "Discover",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Hi, ${fullName.substringBefore(" ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = fullName.initials(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            IconButton(onClick = onLogout) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        shape = SkillforgeShapes.input,
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        placeholder = {
            Text("Search for courses, topics...")
        },
    )
}

@Composable
private fun CategoryRow(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small),
    ) {
        categories.forEach { category ->
            val isSelected = category == selectedCategory
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = category,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.primary,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                ),
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionLabel: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = actionLabel,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun CourseDiscoveryCard(
    course: CourseSummary,
    onClick: () -> Unit,
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = SkillforgeShapes.card,
        colors = skillforgeElevatedCardColors(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = SkillforgeSpacing.small),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SkillforgeComponentSizes.thumbnailHeight)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
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
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.64f),
                                ),
                            ),
                        ),
                )

                if (course.reviewCount >= 300) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(SkillforgeSpacing.medium),
                        shape = SkillforgeShapes.input,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    ) {
                        Text(
                            text = "Best Seller",
                            modifier = Modifier.padding(
                                horizontal = SkillforgeSpacing.small,
                                vertical = SkillforgeSpacing.xSmall,
                            ),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(SkillforgeLayout.cardContentPadding),
                    verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small),
                ) {
                    Text(
                        text = course.categoryName.uppercase(),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.86f),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = course.title,
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Column(
                modifier = Modifier.padding(SkillforgeLayout.cardContentPadding),
                verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall),
                    ) {
                        Text(
                            text = "By ${course.instructorName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        course.subtitle?.takeIf { it.isNotBlank() }?.let { subtitle ->
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    Text(
                        text = course.displayPrice(),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                CourseRatingRow(course = course)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "${prettyLevel(course.level)} • ${course.chapterCount} chapters",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Button(
                        onClick = onClick,
                        colors = skillforgePrimaryButtonColors(),
                    ) {
                        Text("Open")
                        Spacer(modifier = Modifier.width(SkillforgeSpacing.xSmall))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CourseRatingRow(course: CourseSummary) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall),
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (index < course.averageRating.toInt()) PrimaryOrange else PrimaryOrange.copy(alpha = 0.24f),
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            text = "(${course.reviewCount} reviews)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun LoadingStateCard() {
    ElevatedCard(
        shape = SkillforgeShapes.card,
        colors = skillforgeElevatedCardColors(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeLayout.cardContentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium),
        ) {
            CircularProgressIndicator(color = PrimaryOrange)
            Text(text = "Loading course catalog...")
        }
    }
}

@Composable
private fun ErrorStateCard(
    message: String,
    onRetry: () -> Unit,
) {
    ElevatedCard(
        shape = SkillforgeShapes.card,
        colors = skillforgeElevatedCardColors(),
    ) {
        Column(
            modifier = Modifier.padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium),
        ) {
            Text(
                text = "Unable to load courses",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
            )
            Button(
                onClick = onRetry,
                colors = skillforgePrimaryButtonColors(),
            ) {
                Text("Try again")
            }
        }
    }
}

@Composable
private fun EmptyStateCard(
    searchQuery: String,
    selectedCategory: String,
) {
    val description = if (searchQuery.isNotBlank() || selectedCategory != "All") {
        "Try another keyword or switch to a different category."
    } else {
        "Publish a few courses from the instructor side and they will appear here automatically."
    }

    ElevatedCard(
        shape = SkillforgeShapes.card,
        colors = skillforgeElevatedCardColors(),
    ) {
        Column(
            modifier = Modifier.padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium),
        ) {
            Text(
                text = "No matching courses",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = description,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun CourseSummary.displayPrice(): String {
    return if (isFree || price == 0.0) "Free" else "$" + "%,.2f".format(price)
}

internal fun prettyLevel(level: String): String {
    return level.replace("_", " ").lowercase().split(" ")
        .joinToString(" ") { token -> token.replaceFirstChar { it.uppercase() } }
}

private fun String.initials(): String {
    return trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "SF" }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420")
@Composable
private fun StudentCourseListingPreview() {
    SkillforgeTheme(darkTheme = false, dynamicColor = false) {
        StudentCourseListingScreen(
            session = AuthSession(
                accessToken = "preview-token",
                user = AuthUser(
                    id = "student-preview",
                    email = "preview@example.com",
                    fullName = "Samantha Lee",
                    role = "STUDENT",
                ),
            ),
            uiState = StudentCourseListUiState(courses = StudentCourseMockData.featuredCourses),
            onRetry = {},
            onCourseSelected = {},
            onLogout = {},
        )
    }
}
