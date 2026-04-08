package com.example.skillforge.feature.home.ui.mock

import com.example.skillforge.R

data class ActiveCourse(
    val courseId: String,       
    val title: String,         
    val instructorName: String, 
    val thumbnailUrl: Any,        
    val totalLessons: Int,      
    val completedLessons: Int,  
    val percentage: Int         
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
        courseId = "c1",
        title = "Advanced React Patterns",
        instructorName = "Next: Higher Order Components vs Hooks",
        thumbnailUrl = R.drawable.mock_course_thumbnail,
        totalLessons = 20,
        completedLessons = 13,
        percentage = 65 
    )

    val mockActiveCourses = listOf(
        ActiveCourse(
            courseId = "c2",
            title = "UX Research Fundamentals",
            instructorName = "Jane Doe",
            thumbnailUrl = R.drawable.mock_course_thumbnail,
            totalLessons = 15,
            completedLessons = 5,
            percentage = 33 
        ),
        ActiveCourse(
            courseId = "c3",
            title = "Python for Data Science",
            instructorName = "John Smith",
            thumbnailUrl = "https://images.unsplash.com/photo-1526379095098-d400fd0bf935?auto=format&fit=crop&w=1200&q=80",
            totalLessons = 50,
            completedLessons = 40,
            percentage = 80 
        )
    )
}