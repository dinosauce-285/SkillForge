package com.example.skillforge.domain.model

data class Quiz(
    val id: String,
    val chapterId: String,
    val title: String,
    val timeLimit: Int,
    val passingScore: Float,
    val randomizeQuestions: Boolean,
    val questionCount: Int = 0,
    val questions: List<Question> = emptyList()
)

data class Question(
    val id: String,
    val quizId: String,
    val content: String,
    val explanation: String?,
    val orderIndex: Int,
    val choices: List<AnswerChoice> = emptyList()
)

data class AnswerChoice(
    val id: String,
    val questionId: String,
    val content: String,
    val isCorrect: Boolean,
    val orderIndex: Int
)
