package com.example.skillforge.data.remote.dto

data class QuizSubmissionDto(
    val id: String,
    val studentId: String,
    val quizId: String,
    val startTime: String,
    val endTime: String?,
    val score: Float?,
    val isPassed: Boolean?,
    val status: String,
    val quiz: QuizBriefDto
)

data class QuizBriefDto(
    val title: String
)
