package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.QuizApi
import com.example.skillforge.data.remote.dto.*
import com.example.skillforge.domain.model.AnswerChoice
import com.example.skillforge.domain.model.Question
import com.example.skillforge.domain.model.Quiz
import com.example.skillforge.domain.repository.QuizRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizRepositoryImpl(
    private val quizApi: QuizApi
) : QuizRepository {

    override suspend fun getQuizzesByChapter(chapterId: String): Result<List<Quiz>> = withContext(Dispatchers.IO) {
        try {
            val response = quizApi.getQuizzesByChapter(chapterId)
            if (response.isSuccessful) {
                val body = response.body() ?: emptyList()
                Result.success(body.map { mapQuizDtoToModel(it) })
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getQuizById(quizId: String): Result<Quiz> = withContext(Dispatchers.IO) {
        try {
            val response = quizApi.getQuizById(quizId)
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

    override suspend fun createQuiz(request: CreateQuizRequest): Result<Quiz> = withContext(Dispatchers.IO) {
        try {
            val response = quizApi.createQuiz(request)
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

    override suspend fun updateQuiz(id: String, request: UpdateQuizRequest): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = quizApi.updateQuiz(id, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteQuiz(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = quizApi.deleteQuiz(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createQuestion(quizId: String, request: List<CreateQuestionRequest>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = quizApi.createQuestions(quizId, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateQuestion(id: String, request: UpdateQuestionRequest): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = quizApi.updateQuestion(id, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun reorderQuestions(quizId: String, orderedIds: List<String>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = quizApi.reorderQuestions(quizId, ReorderQuestionsRequest(orderedIds))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteQuestion(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = quizApi.deleteQuestion(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createAnswerChoices(questionId: String, request: List<CreateAnswerChoiceRequest>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = quizApi.createAnswerChoices(questionId, request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapQuizDtoToModel(dto: QuizDto): Quiz {
        return Quiz(
            id = dto.id,
            chapterId = dto.chapterId,
            title = dto.title ?: "Untitled Quiz",
            timeLimit = dto.timeLimit,
            passingScore = dto.passingScore,
            randomizeQuestions = dto.randomizeQuestions,
            questionCount = dto._count?.questions ?: dto.questions?.size ?: 0,
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
            choices = dto.choices?.map { mapChoiceDtoToModel(it) } ?: emptyList()
        )
    }

    private fun mapChoiceDtoToModel(dto: AnswerChoiceDto): AnswerChoice {
        return AnswerChoice(
            id = dto.id,
            questionId = dto.questionId,
            content = dto.content,
            isCorrect = dto.isCorrect,
            orderIndex = dto.orderIndex
        )
    }
}
