package com.example.skillforge.feature.home.ui.mock

import com.example.skillforge.R

data class ActiveCourse(
    val id: String,
    val title: String,
    val instructorName: String,
    // 1. ĐỔI KIỂU DỮ LIỆU TỪ String THÀNH Any ĐỂ HỖ TRỢ CẢ URL VÀ RESOURCE ID
    val thumbnailUrl: Any,
    val progressPercentage: Float,
    val remainingLessons: Int
)

data class StudentStats(
    val hoursSpent: Double,
    val badgesEarned: Int
)

object HomeMockData {
    val mockStats = StudentStats(
        hoursSpent = 12.5,
        badgesEarned = 4
    )

    val mockMostRecentCourse = ActiveCourse(
        id = "c1",
        title = "Advanced React Patterns",
        instructorName = "Next: Higher Order Components vs Hooks",
        thumbnailUrl = com.example.skillforge.R.drawable.mock_course_thumbnail,
        progressPercentage = 0.65f,
        remainingLessons = 0
    )

    val mockActiveCourses = listOf(
        ActiveCourse(
            id = "c2",
            title = "UX Research Fundamentals",
            instructorName = "Jane Doe",
            thumbnailUrl = com.example.skillforge.R.drawable.mock_course_thumbnail,
            progressPercentage = 0.35f,
            remainingLessons = 8
        ),
        ActiveCourse(
            id = "c3",
            title = "Python for Data Science",
            instructorName = "John Smith",
            thumbnailUrl = "https://images.unsplash.com/photo-1526379095098-d400fd0bf935?auto=format&fit=crop&w=1200&q=80",
            progressPercentage = 0.8f,
            remainingLessons = 2
        )
    )
}