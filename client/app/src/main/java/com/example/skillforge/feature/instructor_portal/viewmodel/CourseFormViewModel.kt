package com.example.skillforge.feature.instructor_portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.remote.CategoryDto
import com.example.skillforge.domain.repository.CategoryRepository
import com.example.skillforge.domain.repository.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Trạng thái của màn hình Form
sealed class CourseFormState {
    object Idle : CourseFormState()
    object Loading : CourseFormState()
    object Success : CourseFormState()
    data class Error(val message: String) : CourseFormState()
}

class CourseFormViewModel(private val repository: CourseRepository,
                          private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<CourseFormState>(CourseFormState.Idle)
    val uiState: StateFlow<CourseFormState> = _uiState
    private val _categories = MutableStateFlow<List<CategoryDto>>(emptyList())
    val categories: StateFlow<List<CategoryDto>> = _categories

    fun fetchCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories()
                .onSuccess {
                    _categories.value = it
                    println("✅ ĐÃ LẤY ĐƯỢC CATEGORY: $it")
                }
                .onFailure {
                    // 🌟 In lỗi ra để xem nó bị gì!
                    println("❌ LỖI TẢI CATEGORY: ${it.message}")
                    it.printStackTrace()
                }
        }
    }

    fun createCourse(token: String, title: String, summary: String, price: String, categoryId: String) {
        viewModelScope.launch {
            _uiState.value = CourseFormState.Loading
            try {
                val priceValue = price.toDoubleOrNull() ?: 0.0
                val result = repository.createCourse(token, title, summary, priceValue, categoryId)

                result.fold(
                    onSuccess = { _uiState.value = CourseFormState.Success },
                    onFailure = { _uiState.value = CourseFormState.Error(it.message ?: "Lỗi!") }
                )
            } catch (e: Exception) {
                _uiState.value = CourseFormState.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }

    fun resetState() { _uiState.value = CourseFormState.Idle }
}