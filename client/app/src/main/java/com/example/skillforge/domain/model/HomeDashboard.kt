package com.example.skillforge.domain.model

data class HomeDashboard(
    val studentName: String,
    val hoursSpent: Double,
    val badgesEarned: Int,
    val courses: List<ActiveCourse>
)