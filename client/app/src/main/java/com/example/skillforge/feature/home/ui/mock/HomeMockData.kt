package com.example.skillforge.feature.home.ui.mock

import com.example.skillforge.R

// Data Class đã được chuẩn hóa theo đúng Response của API /progress/dashboard
data class ActiveCourse(
    val courseId: String,       // Khớp với courseId từ API
    val title: String,          // Khớp với title từ API
    val instructorName: String, // Vẫn giữ tạm cho UI hiển thị (Cần update API backend sau)
    val thumbnailUrl: Any,         // Khớp với thumbnail từ API (Dùng Any để truyền được cả Int của R.drawable và String URL)
    val totalLessons: Int,      // Thêm mới: Tổng số bài học từ API
    val completedLessons: Int,  // Thêm mới: Số bài đã hoàn thành từ API
    val percentage: Int         // Đã sửa thành Int (0 - 100) khớp với công thức Math.round của Backend
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
        percentage = 65 // Truyền thẳng số nguyên (65 thay vì 0.65f)
    )

    val mockActiveCourses = listOf(
        ActiveCourse(
            courseId = "c2",
            title = "UX Research Fundamentals",
            instructorName = "Jane Doe",
            thumbnailUrl = R.drawable.mock_course_thumbnail,
            totalLessons = 15,
            completedLessons = 5,
            percentage = 33 // Truyền thẳng số nguyên
        ),
        ActiveCourse(
            courseId = "c3",
            title = "Python for Data Science",
            instructorName = "John Smith",
            thumbnailUrl = "https://images.unsplash.com/photo-1526379095098-d400fd0bf935?auto=format&fit=crop&w=1200&q=80",
            totalLessons = 50,
            completedLessons = 40,
            percentage = 80 // Truyền thẳng số nguyên
        )
    )
}