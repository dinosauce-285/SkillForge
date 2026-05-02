package com.example.skillforge.domain.model

data class AnswerChoice(
    val id: String,
    val questionId: String,
    val content: String,
    val isCorrect: Boolean,
    val orderIndex: Int
)
