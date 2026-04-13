package com.example.skillforge.feature.student_courses.ui
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
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
import com.example.skillforge.domain.model.Category
import com.example.skillforge.domain.model.CourseSummary
import com.example.skillforge.feature.student_courses.ui.components.StudentBottomNavigationBar
import com.example.skillforge.feature.student_courses.viewmodel.StudentCourseListUiState
import com.example.skillforge.feature.student_courses.viewmodel.StudentCoursesViewModel

private val levelOptions = listOf(
    null to "All levels",
    "BEGINNER" to "Beginner",
    "INTERMEDIATE" to "Intermediate",
    "ADVANCED" to "Advanced",
    "ALL_LEVELS" to "All levels course",
)

@Composable
fun StudentCourseListingRoute(
    session: AuthSession,
    viewModel: StudentCoursesViewModel,
    onCourseSelected: (String) -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToLearning: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
) {
    val uiState by viewModel.courseListState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshCatalog()
    }

    StudentCourseListingScreen(
        session = session,
        uiState = uiState,
        onSearchQueryChange = viewModel::updateSearchQuery,
        onCategorySelected = viewModel::updateSelectedCategory,
        onLevelSelected = viewModel::updateSelectedLevel,
        onResetFilters = viewModel::resetFilters,
        onRetry = viewModel::refreshCatalog,
        onCourseSelected = onCourseSelected,
        onNavigateToFavorites = onNavigateToFavorites,
        onNavigateToLearning = onNavigateToLearning,
        onNavigateToProfile = onNavigateToProfile,
        onLogout = onLogout,
    )
}

@Composable
fun StudentCourseListingScreen(
    session: AuthSession,
    uiState: StudentCourseListUiState,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onLevelSelected: (String?) -> Unit,
    onResetFilters: () -> Unit,
    onRetry: () -> Unit,
    onCourseSelected: (String) -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToLearning: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onLogout: () -> Unit,
) {
    var isFilterPanelOpen by remember { mutableStateOf(false) }
    val activeFilterCount = remember(
        uiState.selectedCategoryId,
        uiState.selectedLevel,
    ) {
        listOf(uiState.selectedCategoryId, uiState.selectedLevel).count { it != null }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            StudentBottomNavigationBar(
                currentRoute = "Discover",
                onNavigateToLearning = onNavigateToLearning,
                onNavigateToWishlist = onNavigateToFavorites,
                onNavigateToProfile = onNavigateToProfile,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
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
                    SearchActionBar(
                        value = uiState.searchQuery,
                        activeFilterCount = activeFilterCount,
                        onValueChange = onSearchQueryChange,
                        onFilterClick = { isFilterPanelOpen = true },
                    )
                }

                item {
                    SectionHeader(
                        title = "Course catalog",
                        actionLabel = "${uiState.courses.size} results",
                    )
                }

                item {
                    CourseCatalogContent(
                        uiState = uiState,
                        onRetry = onRetry,
                        onCourseSelected = onCourseSelected,
                    )
                }
            }

            AnimatedVisibility(
                visible = isFilterPanelOpen,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f))
                        .clickable { isFilterPanelOpen = false },
                )
            }

            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterEnd,
            ) {
                val compactPanelWidth = maxWidth * 0.84f
                val panelWidth = if (maxWidth < 760.dp) compactPanelWidth else 360.dp

                AnimatedVisibility(
                    visible = isFilterPanelOpen,
                    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                    exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                top = SkillforgeSpacing.medium,
                                bottom = innerPadding.calculateBottomPadding() + SkillforgeSpacing.medium,
                                end = SkillforgeLayout.screenHorizontalPadding,
                            ),
                        contentAlignment = Alignment.CenterEnd,
                    ) {
                        CourseFilterSidebar(
                            modifier = Modifier.width(panelWidth),
                            categories = uiState.categories,
                            selectedCategoryId = uiState.selectedCategoryId,
                            selectedLevel = uiState.selectedLevel,
                            onCategorySelected = onCategorySelected,
                            onLevelSelected = onLevelSelected,
                            onResetFilters = onResetFilters,
                            onClose = { isFilterPanelOpen = false },
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
private fun SearchActionBar(
    value: String,
    activeFilterCount: Int,
    onValueChange: (String) -> Unit,
    onFilterClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
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
                Text("Search courses by title...")
            },
        )

        Surface(
            modifier = Modifier.size(56.dp),
            shape = SkillforgeShapes.input,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = SkillforgeSpacing.xSmall,
            onClick = onFilterClick,
        ) {
            Box(contentAlignment = Alignment.Center) {
                BadgedBox(
                    badge = {
                        if (activeFilterCount > 0) {
                            Badge {
                                Text(activeFilterCount.toString())
                            }
                        }
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Open filters",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CourseFilterSidebar(
    modifier: Modifier = Modifier,
    categories: List<Category>,
    selectedCategoryId: String?,
    selectedLevel: String?,
    onCategorySelected: (String?) -> Unit,
    onLevelSelected: (String?) -> Unit,
    onResetFilters: () -> Unit,
    onClose: () -> Unit,
) {
    var levelExpanded by remember { mutableStateOf(false) }
    val selectedLevelLabel = levelOptions.firstOrNull { it.first == selectedLevel }?.second ?: "All levels"

    ElevatedCard(
        modifier = modifier,
        shape = SkillforgeShapes.card,
        colors = skillforgeElevatedCardColors(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Filters",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)) {
                    OutlinedButton(onClick = onResetFilters) {
                        Text("Reset")
                    }
                    OutlinedButton(onClick = onClose) {
                        Text("Close")
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)) {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                FilterCheckboxRow(
                    label = "All categories",
                    checked = selectedCategoryId == null,
                    onClick = { onCategorySelected(null) },
                )
                categories.forEach { category ->
                    FilterCheckboxRow(
                        label = category.name,
                        checked = selectedCategoryId == category.id,
                        onClick = { onCategorySelected(category.id) },
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)) {
                Text(
                    text = "Difficulty",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                ExposedDropdownMenuBox(
                    expanded = levelExpanded,
                    onExpandedChange = { levelExpanded = !levelExpanded },
                ) {
                    OutlinedTextField(
                        value = selectedLevelLabel,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = levelExpanded)
                        },
                        colors = TextFieldDefaults.colors(),
                    )
                    ExposedDropdownMenu(
                        expanded = levelExpanded,
                        onDismissRequest = { levelExpanded = false },
                    ) {
                        levelOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.second) },
                                onClick = {
                                    onLevelSelected(option.first)
                                    levelExpanded = false
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterCheckboxRow(
    label: String,
    checked: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SkillforgeShapes.card)
            .clickable(onClick = onClick)
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall),
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onClick() },
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun CourseCatalogContent(
    uiState: StudentCourseListUiState,
    onRetry: () -> Unit,
    onCourseSelected: (String) -> Unit,
) {
    when {
        uiState.isLoading -> LoadingStateCard()
        uiState.errorMessage != null -> ErrorStateCard(
            message = uiState.errorMessage,
            onRetry = onRetry,
        )

        uiState.courses.isEmpty() -> EmptyStateCard(
            searchQuery = uiState.searchQuery,
            selectedCategoryId = uiState.selectedCategoryId,
            selectedLevel = uiState.selectedLevel,
        )

        else -> Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)) {
            uiState.courses.forEach { course ->
                CourseDiscoveryCard(
                    course = course,
                    onClick = { onCourseSelected(course.id) },
                )
            }
        }
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
            Text(text = "Loading filtered course catalog...")
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
    selectedCategoryId: String?,
    selectedLevel: String?,
) {
    val hasFilters = searchQuery.isNotBlank() || selectedCategoryId != null || selectedLevel != null
    val description = if (hasFilters) {
        "No course matches this keyword, category, and difficulty combination."
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
            uiState = StudentCourseListUiState(
                courses = StudentCourseMockData.featuredCourses,
                categories = listOf(
                    Category(id = "design", name = "Design"),
                    Category(id = "development", name = "Development"),
                    Category(id = "business", name = "Business"),
                ),
            ),
            onSearchQueryChange = {},
            onCategorySelected = {},
            onLevelSelected = {},
            onResetFilters = {},
            onRetry = {},
            onCourseSelected = {},
            onNavigateToFavorites = {},
            onNavigateToLearning = {},
            onNavigateToProfile = {},
            onLogout = {},
        )
    }
}
