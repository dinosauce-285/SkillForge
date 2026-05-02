package com.example.skillforge.feature.instructor_portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.remote.dto.CreateAnswerChoiceRequest
import com.example.skillforge.data.remote.dto.CreateQuestionRequest
import com.example.skillforge.data.remote.dto.CreateQuizRequest
import com.example.skillforge.data.remote.dto.UpdateQuestionRequest
import com.example.skillforge.data.remote.dto.UpdateQuizRequest
import com.example.skillforge.domain.model.Question
import com.example.skillforge.domain.model.Quiz
import com.example.skillforge.domain.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class QuizUiState {
    object Loading : QuizUiState()
    data class Success(val quiz: Quiz?) : QuizUiState()
    data class Error(val message: String) : QuizUiState()
}

class QuizBuilderViewModel(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var currentQuizId: String? = null
    private var currentChapterId: String? = null

    fun loadQuiz(quizId: String) {
        if (currentQuizId == quizId && _uiState.value is QuizUiState.Success) return
        currentQuizId = quizId
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            val result = quizRepository.getQuizById(quizId)
            result.onSuccess { quiz ->
                currentChapterId = quiz.chapterId
                _uiState.value = QuizUiState.Success(quiz)
            }.onFailure {
                _uiState.value = QuizUiState.Error(it.message ?: "Unknown error")
            }
        }
    }

    fun createNewQuiz(chapterId: String, title: String, timeLimit: Int, passingScore: Float) {
        currentChapterId = chapterId
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            val result = quizRepository.createQuiz(
                request = CreateQuizRequest(
                    chapterId = chapterId,
                    title = title.ifBlank { "Untitled Quiz" },
                    timeLimit = timeLimit,
                    passingScore = passingScore,
                    randomizeQuestions = false,
                    questions = emptyList()
                )
            )
            result.onSuccess { quiz ->
                currentQuizId = quiz.id
                _uiState.value = QuizUiState.Success(quiz)
            }.onFailure {
                _uiState.value = QuizUiState.Error("Failed to create quiz")
            }
        }
    }

    fun updateQuizSettings(title: String, timeLimit: Int, passingScore: Float, randomizeQuestions: Boolean) {
        val quizId = currentQuizId ?: return

        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            val result = quizRepository.updateQuiz(
                id = quizId,
                request = UpdateQuizRequest(
                    title = title.ifBlank { null },
                    timeLimit = timeLimit,
                    passingScore = passingScore,
                    randomizeQuestions = randomizeQuestions
                )
            )
            result.onSuccess {
                loadQuiz(quizId)
            }.onFailure {
                _uiState.value = QuizUiState.Error("Failed to update quiz")
            }
        }
    }

    fun deleteQuiz(onDeleted: () -> Unit) {
        val quizId = currentQuizId ?: return
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            val result = quizRepository.deleteQuiz(quizId)
            result.onSuccess {
                onDeleted()
            }.onFailure {
                _uiState.value = QuizUiState.Error("Failed to delete quiz")
            }
        }
    }

    fun addQuestion(content: String, choicesText: List<String>, correctIndex: Int) {
        val currentState = _uiState.value
        val quizId = currentQuizId ?: return

        if (currentState is QuizUiState.Success && currentState.quiz != null) {
            viewModelScope.launch {
                _uiState.value = QuizUiState.Loading

                val choices = choicesText.mapIndexed { index, text ->
                    CreateAnswerChoiceRequest(
                        content = text,
                        isCorrect = index == correctIndex,
                        orderIndex = index
                    )
                }

                val questionRequest = CreateQuestionRequest(
                    content = content,
                    orderIndex = currentState.quiz.questions.size,
                    choices = choices
                )

                val result = quizRepository.createQuestion(quizId, listOf(questionRequest))
                result.onSuccess {
                    loadQuiz(quizId)
                }.onFailure {
                    _uiState.value = QuizUiState.Error("Failed to add question")
                }
            }
        }
    }

    fun updateQuestion(questionId: String, content: String, choicesText: List<String>, correctIndex: Int) {
        val quizId = currentQuizId ?: return

        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading

            val choices = choicesText.mapIndexed { index, text ->
                CreateAnswerChoiceRequest(
                    content = text,
                    isCorrect = index == correctIndex,
                    orderIndex = index
                )
            }

            val request = UpdateQuestionRequest(
                content = content,
                choices = choices
            )

            val result = quizRepository.updateQuestion(questionId, request)
            result.onSuccess {
                loadQuiz(quizId)
            }.onFailure {
                _uiState.value = QuizUiState.Error("Failed to update question")
            }
        }
    }

    fun deleteQuestion(questionId: String) {
        val quizId = currentQuizId ?: return
        viewModelScope.launch {
            _uiState.value = QuizUiState.Loading
            val result = quizRepository.deleteQuestion(questionId)
            result.onSuccess {
                loadQuiz(quizId)
            }.onFailure {
                _uiState.value = QuizUiState.Error("Failed to delete question")
            }
        }
    }

    fun reorderQuestions(fromIndex: Int, toIndex: Int) {
        val currentState = _uiState.value
        if (currentState is QuizUiState.Success && currentState.quiz != null) {
            val questions = currentState.quiz.questions.toMutableList()
            val moved = questions.removeAt(fromIndex)
            questions.add(toIndex, moved)

            _uiState.value = QuizUiState.Success(
                currentState.quiz.copy(
                    questions = questions.mapIndexed { index, q -> q.copy(orderIndex = index) }
                )
            )
        }
    }

    fun commitReorder() {
        val currentState = _uiState.value
        val quizId = currentQuizId ?: return

        if (currentState is QuizUiState.Success && currentState.quiz != null) {
            val orderedIds = currentState.quiz.questions.map { it.id }
            viewModelScope.launch {
                val result = quizRepository.reorderQuestions(quizId, orderedIds)
                result.onFailure {
                    loadQuiz(quizId)
                }
            }
        }
    }

    fun getQuestionById(questionId: String): Question? {
        val currentState = _uiState.value
        if (currentState is QuizUiState.Success && currentState.quiz != null) {
            return currentState.quiz.questions.find { it.id == questionId }
        }
        return null
    }

    companion object {
        fun provideFactory(
            quizRepository: QuizRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(QuizBuilderViewModel::class.java)) {
                    return QuizBuilderViewModel(quizRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
