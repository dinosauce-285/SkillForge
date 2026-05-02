package com.example.skillforge.domain.model

data class QuizSubmissionResult(
    val attemptId: String,
    val score: Int,
    val isPassed: Boolean,
    val correctAnswers: Int,
    val totalQuestions: Int
)
