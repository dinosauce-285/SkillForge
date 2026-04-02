package com.example.skillforge.feature.home.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.SkillforgeTheme
import com.example.skillforge.feature.home.ui.components.*
import com.example.skillforge.feature.home.ui.mock.HomeMockData

@Composable
fun HomeScreen(
    onNavigateToMyCourses: () -> Unit = {}
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(SkillforgeSpacing.large))

            // 1. Header
            HomeWelcomeHeader(
                studentName = "Alex",
                onNotificationClick = { /* TODO: Mở thông báo */ }
            )

            Spacer(modifier = Modifier.height(SkillforgeSpacing.large))

            // 2. Thẻ Continue Learning
            ContinueLearningCard(
                course = HomeMockData.mockMostRecentCourse
            )

            Spacer(modifier = Modifier.height(SkillforgeSpacing.large))

            // 3. Hàng Thống Kê
            StudentStatsRow(
                hoursSpent = HomeMockData.mockStats.hoursSpent,
                badgesEarned = HomeMockData.mockStats.badgesEarned
            )

            Spacer(modifier = Modifier.height(SkillforgeSpacing.large))

            // 4. Danh sách Active Courses
            ActiveCourseList(
                courses = HomeMockData.mockActiveCourses,
                onViewAllClick = { onNavigateToMyCourses() }
            )

            Spacer(modifier = Modifier.height(SkillforgeSpacing.large))
        }
    }
}

@Preview(
    name = "Student Dashboard",
    showBackground = true,
    showSystemUi = true,
    device = "id:pixel_6"
)
@Composable
fun HomeScreenPreview() {
    SkillforgeTheme {
        HomeScreen()
    }
}