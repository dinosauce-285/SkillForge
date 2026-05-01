package com.example.skillforge.data.remote.dto

data class QuizDto(
    val id: String,
    val chapterId: String,
    val title: String?,
    val timeLimit: Int,
    val passingScore: Float,
    val randomizeQuestions: Boolean,
    val questions: List<QuestionDto>? = null,
    val _count: QuizQuestionCount? = null
) {
    data class QuizQuestionCount(val questions: Int = 0)
}

data class QuestionDto(
    val id: String,
    val quizId: String,
    val content: String,
    val explanation: String?,
    val orderIndex: Int,
    val choices: List<AnswerChoiceDto>? = null
)

data class AnswerChoiceDto(
    val id: String,
    val questionId: String,
    val content: String,
    val isCorrect: Boolean,
    val orderIndex: Int
)

data class CreateQuizRequest(
    val chapterId: String,
    val title: String? = null,
    val timeLimit: Int,
    val passingScore: Float,
    val randomizeQuestions: Boolean,
    val questions: List<CreateQuestionRequest>
)

data class UpdateQuizRequest(
    val title: String? = null,
    val timeLimit: Int? = null,
    val passingScore: Float? = null,
    val randomizeQuestions: Boolean? = null
)

data class CreateQuestionRequest(
    val content: String,
    val explanation: String? = null,
    val orderIndex: Int,
    val choices: List<CreateAnswerChoiceRequest>
)

data class UpdateQuestionRequest(
    val content: String? = null,
    val explanation: String? = null,
    val orderIndex: Int? = null,
    val choices: List<CreateAnswerChoiceRequest>? = null
)

data class CreateAnswerChoiceRequest(
    val content: String,
    val isCorrect: Boolean,
    val orderIndex: Int
)

data class ReorderQuestionsRequest(
    val orderedIds: List<String>
)
