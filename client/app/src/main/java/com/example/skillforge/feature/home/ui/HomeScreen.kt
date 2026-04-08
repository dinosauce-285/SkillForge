package com.example.skillforge.feature.home.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.SkillforgeTheme
import com.example.skillforge.domain.model.ActiveCourse
import com.example.skillforge.feature.home.ui.components.ActiveCourseList
import com.example.skillforge.feature.home.ui.components.ContinueLearningCard
import com.example.skillforge.feature.home.ui.components.EmptyDashboardState
import com.example.skillforge.feature.home.ui.components.HomeWelcomeHeader
import com.example.skillforge.feature.home.ui.components.StudentStatsRow
import com.example.skillforge.feature.home.viewmodel.HomeUiState
import com.example.skillforge.feature.home.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    token: String,
    viewModel: HomeViewModel,
    onNavigateToMyCourses: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchDashboard(token)
    }

    HomeScreenContent(
        uiState = uiState,
        onNavigateToMyCourses = onNavigateToMyCourses
    )
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onNavigateToMyCourses: () -> Unit = {}
) {
    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is HomeUiState.Error -> {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is HomeUiState.Success -> {
                    val courses = uiState.courses

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(SkillforgeSpacing.large))

                        HomeWelcomeHeader(
                            studentName = "Student",
                            onNotificationClick = { }
                        )

                        Spacer(modifier = Modifier.height(SkillforgeSpacing.large))

                        if (courses.isEmpty()) {
                            EmptyDashboardState()
                        } else {
                            val mostRecent = courses.firstOrNull()
                            val otherCourses = if (courses.size > 1) courses.drop(1) else emptyList()

                            mostRecent?.let { course ->
                                ContinueLearningCard(
                                    course = course
                                )
                                Spacer(modifier = Modifier.height(SkillforgeSpacing.large))
                            }

                            StudentStatsRow(
                                hoursSpent = 12.5,
                                badgesEarned = 4
                            )

                            Spacer(modifier = Modifier.height(SkillforgeSpacing.large))

                            if (otherCourses.isNotEmpty()) {
                                ActiveCourseList(
                                    courses = otherCourses,
                                    onViewAllClick = onNavigateToMyCourses
                                )
                                Spacer(modifier = Modifier.height(SkillforgeSpacing.large))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Student Dashboard - Empty",
    showBackground = true,
    showSystemUi = true,
    device = "id:pixel_6"
)
@Composable
fun HomeScreenEmptyPreview() {
    SkillforgeTheme {
        HomeScreenContent(
            uiState = HomeUiState.Success(
                courses = emptyList()
            )
        )
    }
}