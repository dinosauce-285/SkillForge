package com.example.skillforge.domain.model

data class Question(
    val id: String,
    val quizId: String,
    val content: String,
    val explanation: String?,
    val orderIndex: Int,
    val choices: List<AnswerChoice>
)
