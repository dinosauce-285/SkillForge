package com.example.skillforge.feature.home.ui.mock

import com.example.skillforge.domain.model.CourseSummary

object HomeMockData {
    val mockCategories = listOf("All", "Design", "Coding", "Business")

    val mockCourses = listOf(
        CourseSummary(
            id = "1",
            title = "Advanced UI/UX Design Principles",
            subtitle = null,
            summary = null,
            thumbnailUrl = null,
            categoryName = "Design",
            instructorName = "Sarah Jenkins",
            level = "Advanced",
            price = 89.99,
            isFree = false,
            averageRating = 4.8f,
            studentCount = 1240, // Lớn hơn 1000 sẽ tự hiện Best Seller
            reviewCount = 1240,
            chapterCount = 12,
            tags = emptyList()
        ),
        CourseSummary(
            id = "2",
            title = "Full Stack Web Development with React",
            subtitle = null,
            summary = null,
            thumbnailUrl = null,
            categoryName = "Coding",
            instructorName = "Michael Chen",
            level = "Intermediate",
            price = 124.00,
            isFree = false,
            averageRating = 4.9f,
            studentCount = 856,
            reviewCount = 856,
            chapterCount = 20,
            tags = emptyList()
        )
    )
}