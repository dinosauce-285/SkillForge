package com.example.skillforge.feature.home.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.SkillforgeTheme
import com.example.skillforge.domain.model.HomeDashboard
import com.example.skillforge.feature.home.ui.components.ActiveCourseList
import com.example.skillforge.feature.home.ui.components.ContinueLearningCard
import com.example.skillforge.feature.home.ui.components.EmptyDashboardState
import com.example.skillforge.feature.home.ui.components.HomeWelcomeHeader
import com.example.skillforge.feature.home.ui.components.NotificationBottomSheet
import com.example.skillforge.feature.home.ui.components.StudentStatsRow
import com.example.skillforge.feature.home.viewmodel.HomeUiState
import com.example.skillforge.feature.home.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    token: String, // Kept for repository compatibility if needed
    viewModel: HomeViewModel,
    onNavigateToMyCourses: () -> Unit = {},
    onNavigateToDiscovery: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val notificationState by viewModel.notificationState.collectAsState()
    var showNotifications by remember { mutableStateOf(false) }

    // Fetch dashboard data whenever the token changes or on initial composition
    LaunchedEffect(token) {
        viewModel.fetchDashboard(token)
        viewModel.fetchNotifications()
    }

    HomeScreenContent(
        uiState = uiState,
        unreadCount = notificationState.unreadCount,
        onNavigateToMyCourses = onNavigateToMyCourses,
        onNavigateToDiscovery = onNavigateToDiscovery,
        onNotificationClick = {
            showNotifications = true
            viewModel.fetchNotifications()
        },
        onRetry = { viewModel.fetchDashboard(token) }
    )

    if (showNotifications) {
        NotificationBottomSheet(
            notifications = notificationState.notifications,
            unreadCount = notificationState.unreadCount,
            isNotificationLoading = notificationState.isNotificationLoading,
            errorMessage = notificationState.errorMessage,
            onNotificationClick = { notification -> viewModel.markAsRead(notification.id) },
            onMarkAllAsRead = { viewModel.markAllAsRead() },
            onDismiss = { showNotifications = false },
        ) 
    }
}

@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    unreadCount: Int = 0,
    onNavigateToMyCourses: () -> Unit = {},
    onNavigateToDiscovery: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onRetry: () -> Unit = {}
) {
    Scaffold(
        contentWindowInsets = WindowInsets(0.dp)
    ) { paddingValues ->
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
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error: ${uiState.message}",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onRetry) {
                            Text("Retry")
                        }
                    }
                }

                is HomeUiState.Success -> {
                    val dashboard = uiState.dashboard
                    val courses = dashboard.courses

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        HomeWelcomeHeader(
                            studentName = dashboard.studentName,
                            onNotificationClick = onNotificationClick,
                            unreadCount = unreadCount,
                        )

                        Spacer(modifier = Modifier.height(SkillforgeSpacing.large))

                        if (courses.isEmpty()) {
                            EmptyDashboardState(onExploreClick = onNavigateToDiscovery)
                        } else {
                            val mostRecent = courses.firstOrNull()
                            val otherCourses = if (courses.size > 1) courses.drop(1) else emptyList()

                            mostRecent?.let { course ->
                                ContinueLearningCard(
                                    course = course
                                )
                            }

                            StudentStatsRow(
                                hoursSpent = dashboard.hoursSpent,
                                badgesEarned = dashboard.badgesEarned
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
    name = "Student Dashboard - Success",
    showBackground = true,
    showSystemUi = true,
    device = "id:pixel_6"
)
@Composable
fun HomeScreenPreview() {
    SkillforgeTheme {
        HomeScreenContent(
            uiState = HomeUiState.Success(
                dashboard = HomeDashboard(
                    studentName = "Alex",
                    hoursSpent = 12.5,
                    badgesEarned = 3,
                    courses = emptyList()
                )
            )
        )
    }
}
