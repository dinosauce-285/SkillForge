package com.example.skillforge.feature.favorite.ui
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.skillforge.core.designsystem.BackgroundColor
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.core.designsystem.SurfaceColor
import com.example.skillforge.core.designsystem.TextPrimaryColor
import com.example.skillforge.core.designsystem.TextSecondaryColor
import com.example.skillforge.domain.model.AuthSession
import com.example.skillforge.domain.model.FavoriteCourse
import com.example.skillforge.feature.favorite.viewmodel.FavoriteUiState
import com.example.skillforge.feature.favorite.viewmodel.FavoriteViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteRoute(
    session: AuthSession,
    viewModel: FavoriteViewModel,
    onBackClick: () -> Unit,
    onCourseClick: (String) -> Unit,
    onNavigateToDiscovery: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(session.accessToken) {
        viewModel.loadFavorites(session.accessToken)
    }

    FavoriteScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onCourseClick = onCourseClick,
        onNavigateToDiscovery = onNavigateToDiscovery,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    uiState: FavoriteUiState,
    onBackClick: () -> Unit,
    onCourseClick: (String) -> Unit,
    onNavigateToDiscovery: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Favorite Courses",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryOrange,
                        ),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryOrange,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                ),
            )
        },
        containerColor = BackgroundColor,
    ) { innerPadding ->
        when {
            uiState.isLoading -> FavoriteLoadingState(modifier = Modifier.padding(innerPadding))
            uiState.errorMessage != null -> FavoriteErrorState(
                message = uiState.errorMessage,
                modifier = Modifier.padding(innerPadding),
            )
            uiState.courses.isEmpty() -> FavoriteEmptyState(
                onNavigateToDiscovery = onNavigateToDiscovery,
                modifier = Modifier.padding(innerPadding),
            )
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                item {
                    FavoriteHeaderSection()
                }

                items(uiState.courses) { course ->
                    FavoriteCourseCard(
                        course = course,
                        isPurchased = course.isFree,
                        onClick = { onCourseClick(course.id) },
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun FavoriteHeaderSection() {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "YOUR COLLECTION",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                color = TextSecondaryColor,
            ),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text(
                text = "Continue your journey of ",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimaryColor,
                ),
            )
            Text(
                text = "knowledge",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryOrange,
                ),
            )
        }
    }
}

@Composable
private fun FavoriteCourseCard(
    course: FavoriteCourse,
    isPurchased: Boolean,
    onClick: () -> Unit,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(width = 120.dp, height = 90.dp)
                    .clip(RoundedCornerShape(12.dp)),
            ) {
                val thumbnailUrl = course.thumbnailUrl?.takeIf { it.isNotBlank() }

                if (thumbnailUrl != null) {
                    AsyncImage(
                        model = thumbnailUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp),
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        contentDescription = "Favorite",
                        tint = PrimaryOrange,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    if (isPurchased) {
                        Surface(
                            color = Color(0xFFE8F5E9),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(bottom = 4.dp),
                        ) {
                            Text(
                                text = "OWNED",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32),
                                    fontSize = 10.sp,
                                ),
                            )
                        }
                    }

                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = TextPrimaryColor,
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp),
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = TextSecondaryColor,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = course.instructorName,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondaryColor,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isPurchased) {
                    Button(
                        onClick = onClick,
                        modifier = Modifier
                            .align(Alignment.End)
                            .height(36.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        contentPadding = PaddingValues(horizontal = 12.dp),
                    ) {
                        Icon(
                            Icons.Default.PlayCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text("Start Learning", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if (course.price > 0) String.format("$%.2f", course.price) else "Free",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryOrange,
                            ),
                        )
                        Button(
                            onClick = onClick,
                            modifier = Modifier
                                .height(36.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(PrimaryOrange, Color(0xFFFF6B00)),
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                ),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(horizontal = 12.dp),
                        ) {
                            Text(
                                "Enroll",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = PrimaryOrange)
    }
}

@Composable
private fun FavoriteErrorState(
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

@Composable
private fun FavoriteEmptyState(
    onNavigateToDiscovery: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "No favorite courses yet",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = TextPrimaryColor,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Explore more courses and save the ones you want to learn later.",
            textAlign = TextAlign.Center,
            color = TextSecondaryColor,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateToDiscovery) {
            Text("Explore Courses")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoriteScreenPreview() {
    FavoriteScreen(
        uiState = FavoriteUiState(
            courses = listOf(
                FavoriteCourse(
                    id = "preview-course",
                    title = "UI Design Fundamentals",
                    instructorName = "Alex Rivera",
                    price = 0.0,
                    isFree = true,
                ),
            ),
        ),
        onBackClick = {},
        onCourseClick = {},
        onNavigateToDiscovery = {},
    )
}
