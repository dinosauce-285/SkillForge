package com.example.skillforge.feature.instructor_portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.domain.model.Category // translated comment
import com.example.skillforge.domain.repository.CategoryRepository
import com.example.skillforge.domain.repository.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

// translated comment
sealed class CourseFormState {
    object Idle : CourseFormState()
    object Loading : CourseFormState()
    object Success : CourseFormState()
    data class Error(val message: String) : CourseFormState()
}

class CourseFormViewModel(
    private val repository: CourseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<CourseFormState>(CourseFormState.Idle)
    val uiState: StateFlow<CourseFormState> = _uiState

    // translated comment
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    fun fetchCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories()
                .onSuccess {
                    _categories.value = it 
                    println("CATEGORIES LOADED: $it")
                }
                .onFailure {
                    // translated comment
                    println("CATEGORY LOAD ERROR: ${it.message}")
                    it.printStackTrace()
                }
        }
    }

    fun createCourse(
        token: String,
        title: String,
        summary: String,
        price: String,
        categoryId: String,
        status: String = "DRAFT", // Added with a default value
        thumbnailFile: File? = null // Added with a default value
    ) {
        viewModelScope.launch {
            _uiState.value = CourseFormState.Loading
            try {
                val priceValue = price.toDoubleOrNull() ?: 0.0

                // Call repository with explicit parameter mapping to prevent mismatch
                val result = repository.createCourse(
                    token = token,
                    title = title,
                    summary = summary,
                    price = priceValue,
                    categoryId = categoryId,
                    status = status,
                    thumbnailFile = thumbnailFile
                )

                result.fold(
                    onSuccess = { _uiState.value = CourseFormState.Success },
                    onFailure = { _uiState.value = CourseFormState.Error(it.message ?: "Error!") }
                )
            } catch (e: Exception) {
                _uiState.value = CourseFormState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetState() { _uiState.value = CourseFormState.Idle }
}
