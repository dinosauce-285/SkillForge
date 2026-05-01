package com.example.skillforge.domain.repository

import com.example.skillforge.data.remote.dto.*
import com.example.skillforge.domain.model.Quiz

interface QuizRepository {
    suspend fun getQuizzesByChapter(chapterId: String): Result<List<Quiz>>
    suspend fun getQuizById(quizId: String): Result<Quiz>
    suspend fun createQuiz(request: CreateQuizRequest): Result<Quiz>
    suspend fun updateQuiz(id: String, request: UpdateQuizRequest): Result<Unit>
    suspend fun deleteQuiz(id: String): Result<Unit>
    suspend fun createQuestion(quizId: String, request: List<CreateQuestionRequest>): Result<Unit>
    suspend fun updateQuestion(id: String, request: UpdateQuestionRequest): Result<Unit>
    suspend fun reorderQuestions(quizId: String, orderedIds: List<String>): Result<Unit>
    suspend fun deleteQuestion(id: String): Result<Unit>
    suspend fun createAnswerChoices(questionId: String, request: List<CreateAnswerChoiceRequest>): Result<Unit>
}
