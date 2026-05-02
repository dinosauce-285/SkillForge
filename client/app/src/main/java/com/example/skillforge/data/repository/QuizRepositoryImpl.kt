package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.QuizApi
import com.example.skillforge.data.remote.dto.*
import com.example.skillforge.domain.model.AnswerChoice
import com.example.skillforge.domain.model.Question
import com.example.skillforge.domain.model.Quiz
import com.example.skillforge.domain.repository.QuizRepository

class QuizRepositoryImpl(private val api: QuizApi) : QuizRepository {

    private fun mapQuizDtoToModel(dto: QuizDto): Quiz {
        return Quiz(
            id = dto.id,
            chapterId = dto.chapterId,
            title = dto.title,
            timeLimit = dto.timeLimit,
            passingScore = dto.passingScore,
            randomizeQuestions = dto.randomizeQuestions,
            questions = dto.questions?.map { mapQuestionDtoToModel(it) } ?: emptyList()
        )
    }

    private fun mapQuestionDtoToModel(dto: QuestionDto): Question {
        return Question(
            id = dto.id,
            quizId = dto.quizId,
            content = dto.content,
            explanation = dto.explanation,
            orderIndex = dto.orderIndex,
            choices = dto.choices?.map { mapAnswerChoiceDtoToModel(it) } ?: emptyList()
        )
    }

    private fun mapAnswerChoiceDtoToModel(dto: AnswerChoiceDto): AnswerChoice {
        return AnswerChoice(
            id = dto.id,
            questionId = dto.questionId,
            content = dto.content,
            isCorrect = dto.isCorrect,
            orderIndex = dto.orderIndex
        )
    }

    override suspend fun getQuizzesByChapter(chapterId: String): Result<List<Quiz>> {
        return try {
            val response = api.getQuizzesByChapter(chapterId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { mapQuizDtoToModel(it) })
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getQuizById(id: String): Result<Quiz> {
        return try {
            val response = api.getQuizById(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(mapQuizDtoToModel(response.body()!!))
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createQuiz(request: CreateQuizRequest): Result<Quiz> {
        return try {
            val response = api.createQuiz(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(mapQuizDtoToModel(response.body()!!))
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateQuiz(id: String, request: UpdateQuizRequest): Result<Quiz> {
        return try {
            val response = api.updateQuiz(id, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(mapQuizDtoToModel(response.body()!!))
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteQuiz(id: String): Result<Unit> {
        return try {
            val response = api.deleteQuiz(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createQuestion(
        quizId: String,
        request: List<CreateQuestionRequest>
    ): Result<List<Question>> {
        return try {
            val response = api.createQuestion(quizId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { mapQuestionDtoToModel(it) })
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateQuestion(
        id: String,
        request: UpdateQuestionRequest
    ): Result<Question> {
        return try {
            val response = api.updateQuestion(id, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(mapQuestionDtoToModel(response.body()!!))
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteQuestion(id: String): Result<Unit> {
        return try {
            val response = api.deleteQuestion(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reorderQuestions(
        quizId: String,
        orderedQuestionIds: List<String>
    ): Result<List<Question>> {
        return try {
            val request = ReorderQuestionsRequest(orderedQuestionIds)
            val response = api.reorderQuestions(quizId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { mapQuestionDtoToModel(it) })
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun submitQuiz(
        quizId: String,
        answers: Map<String, String>
    ): Result<com.example.skillforge.domain.model.QuizSubmissionResult> {
        return try {
            val response = api.submitQuiz(quizId, com.example.skillforge.data.remote.dto.SubmitQuizRequest(answers))
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                Result.success(
                    com.example.skillforge.domain.model.QuizSubmissionResult(
                        attemptId = dto.attemptId,
                        score = dto.score,
                        isPassed = dto.isPassed,
                        correctAnswers = dto.correctAnswers,
                        totalQuestions = dto.totalQuestions
                    )
                )
            } else {
                val errorMsg = response.errorBody()?.string() ?: response.message()
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
