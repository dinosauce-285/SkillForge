package com.example.skillforge.domain.repository

import com.example.skillforge.data.remote.dto.CreateQuestionRequest
import com.example.skillforge.data.remote.dto.CreateQuizRequest
import com.example.skillforge.data.remote.dto.UpdateQuestionRequest
import com.example.skillforge.data.remote.dto.UpdateQuizRequest
import com.example.skillforge.domain.model.Question
import com.example.skillforge.domain.model.Quiz

interface QuizRepository {
    suspend fun getQuizzesByChapter(chapterId: String): Result<List<Quiz>>
    suspend fun getQuizById(id: String): Result<Quiz>
    suspend fun createQuiz(request: CreateQuizRequest): Result<Quiz>
    suspend fun updateQuiz(id: String, request: UpdateQuizRequest): Result<Quiz>
    suspend fun deleteQuiz(id: String): Result<Unit>
    suspend fun createQuestion(quizId: String, request: List<CreateQuestionRequest>): Result<List<Question>>
    suspend fun updateQuestion(id: String, request: UpdateQuestionRequest): Result<Question>
    suspend fun deleteQuestion(id: String): Result<Unit>
    suspend fun reorderQuestions(quizId: String, orderedQuestionIds: List<String>): Result<List<Question>>
    suspend fun submitQuiz(quizId: String, answers: Map<String, String>): Result<com.example.skillforge.domain.model.QuizSubmissionResult>
    suspend fun getSubmissions(courseId: String, studentId: String, token: String): Result<List<com.example.skillforge.data.remote.dto.QuizSubmissionDto>>
    suspend fun getSubmissionDetails(attemptId: String, token: String): Result<com.example.skillforge.data.remote.dto.QuizSubmissionDetailsDto>
    suspend fun gradeAttempt(attemptId: String, questionGrades: List<com.example.skillforge.data.remote.dto.QuestionGradeDto>, feedback: String, token: String): Result<com.example.skillforge.data.remote.dto.QuizSubmissionDetailsDto>
    suspend fun getMySubmission(attemptId: String, token: String): Result<com.example.skillforge.data.remote.dto.QuizSubmissionDetailsDto>
}
