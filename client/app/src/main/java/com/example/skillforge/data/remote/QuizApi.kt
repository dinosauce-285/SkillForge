package com.example.skillforge.data.remote

import com.example.skillforge.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface QuizApi {
    @GET("quiz/chapter/{chapterId}")
    suspend fun getQuizzesByChapter(@Path("chapterId") chapterId: String): Response<List<QuizDto>>

    @GET("quiz/{id}")
    suspend fun getQuizById(@Path("id") id: String): Response<QuizDto>

    @POST("quiz")
    suspend fun createQuiz(@Body request: CreateQuizRequest): Response<QuizDto>

    @POST("quiz/{id}/submit")
    suspend fun submitQuiz(
        @Path("id") id: String,
        @Body request: SubmitQuizRequest
    ): Response<QuizSubmissionResultDto>

    @PATCH("quiz/{id}")
    suspend fun updateQuiz(
        @Path("id") id: String,
        @Body request: UpdateQuizRequest
    ): Response<QuizDto>

    @DELETE("quiz/{id}")
    suspend fun deleteQuiz(@Path("id") id: String): Response<Unit>

    @POST("question/{quizId}")
    suspend fun createQuestion(
        @Path("quizId") quizId: String,
        @Body request: List<CreateQuestionRequest>
    ): Response<List<QuestionDto>>

    @PATCH("question/{id}")
    suspend fun updateQuestion(
        @Path("id") id: String,
        @Body request: UpdateQuestionRequest
    ): Response<QuestionDto>

    @POST("question/reorder/{quizId}")
    suspend fun reorderQuestions(
        @Path("quizId") quizId: String,
        @Body request: ReorderQuestionsRequest
    ): Response<List<QuestionDto>>

    @DELETE("question/{id}")
    suspend fun deleteQuestion(@Path("id") id: String): Response<Unit>

    @POST("answer_choice/{questionId}")
    suspend fun createAnswerChoices(
        @Path("questionId") questionId: String,
        @Body request: List<CreateAnswerChoiceRequest>
    ): Response<List<AnswerChoiceDto>>
}
