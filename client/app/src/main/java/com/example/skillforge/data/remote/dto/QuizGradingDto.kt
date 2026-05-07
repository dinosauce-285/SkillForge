package com.example.skillforge.data.remote.dto

data class QuizSubmissionDetailsDto(
    val id: String,
    val studentId: String,
    val quizId: String,
    val startTime: String,
    val endTime: String?,
    val score: Float?,
    val isPassed: Boolean?,
    val status: String,
    val instructorFeedback: String?,
    val quiz: QuizDetailsBriefDto,
    val student: StudentBriefDto,
    val answers: List<StudentAnswerDetailsDto>
)

data class QuizDetailsBriefDto(
    val title: String,
    val questions: List<QuestionBriefDto>,
    val passingScore: Float
)

data class QuestionBriefDto(
    val id: String,
    val content: String,
    val points: Int,
    val choices: List<AnswerChoiceBriefDto>? = null
)

data class AnswerChoiceBriefDto(
    val id: String,
    val content: String,
    val isCorrect: Boolean,
    val orderIndex: Int
)

data class StudentBriefDto(
    val fullName: String
)

data class StudentAnswerDetailsDto(
    val id: String,
    val essayAnswer: String?,
    val selectedChoiceId: String?,
    val pointsAwarded: Float?,
    val question: QuestionBriefDto
)

data class QuestionGradeDto(
    val questionId: String,
    val points: Float
)

data class GradeEssayRequest(
    val questionGrades: List<QuestionGradeDto>,
    val feedback: String
)
