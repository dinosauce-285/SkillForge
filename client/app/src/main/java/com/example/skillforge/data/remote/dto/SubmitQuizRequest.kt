package com.example.skillforge.data.remote.dto

data class SubmitQuizRequest(
    val answers: Map<String, String>
)

data class QuizSubmissionResultDto(
    val attemptId: String,
    val score: Int,
    val isPassed: Boolean,
    val correctAnswers: Int,
    val totalQuestions: Int
)
