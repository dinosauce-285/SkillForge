package com.example.skillforge.domain.model

data class Quiz(
    val id: String,
    val chapterId: String,
    val title: String,
    val timeLimit: Int,
    val passingScore: Float,
    val randomizeQuestions: Boolean,
    val isEssay: Boolean,
    val questions: List<Question>
)
