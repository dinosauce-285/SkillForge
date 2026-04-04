package com.example.skillforge.feature.student_courses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.domain.model.Category
import com.example.skillforge.domain.model.CourseDetails
import com.example.skillforge.domain.model.CourseSummary
import com.example.skillforge.domain.repository.CategoryRepository
import com.example.skillforge.domain.repository.CourseRepository
import com.example.skillforge.feature.student_courses.ui.StudentCourseMockData
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentCourseListUiState(
    val isLoading: Boolean = false,
    val courses: List<CourseSummary> = emptyList(),
    val categories: List<Category> = emptyList(),
    val searchQuery: String = "",
    val selectedCategoryId: String? = null,
    val selectedLevel: String? = null,
    val errorMessage: String? = null,
)

data class StudentCourseDetailsUiState(
    val isLoading: Boolean = false,
    val course: CourseDetails? = null,
    val errorMessage: String? = null,
)

class StudentCoursesViewModel(
    private val courseRepository: CourseRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {
    private val _courseListState = MutableStateFlow(StudentCourseListUiState(isLoading = true))
    val courseListState: StateFlow<StudentCourseListUiState> = _courseListState

    private val _courseDetailsState = MutableStateFlow(StudentCourseDetailsUiState())
    val courseDetailsState: StateFlow<StudentCourseDetailsUiState> = _courseDetailsState

    private var loadedCourseDetailsId: String? = null

    init {
        refreshCatalog()
    }

    fun refreshCatalog() {
        viewModelScope.launch {
            _courseListState.update { it.copy(isLoading = true, errorMessage = null) }

            val currentState = _courseListState.value
            val categoriesDeferred = async { categoryRepository.getCategories() }
            val coursesDeferred = async {
                courseRepository.getCourses(
                    searchQuery = currentState.searchQuery.takeIf { it.isNotBlank() },
                    categoryId = currentState.selectedCategoryId,
                    level = currentState.selectedLevel,
                )
            }

            val categoriesResult = categoriesDeferred.await()
            val coursesResult = coursesDeferred.await()

            val categories = categoriesResult.getOrDefault(currentState.categories)
            coursesResult.fold(
                onSuccess = { courses ->
                    _courseListState.value = currentState.copy(
                        isLoading = false,
                        categories = categories,
                        courses = courses,
                        errorMessage = categoriesResult.exceptionOrNull()?.message,
                    )
                },
                onFailure = { error ->
                    _courseListState.value = currentState.copy(
                        isLoading = false,
                        categories = categories,
                        courses = emptyList(),
                        errorMessage = error.message ?: "Unable to load courses",
                    )
                },
            )
        }
    }

    fun updateSearchQuery(value: String) {
        _courseListState.update { it.copy(searchQuery = value) }
        loadCourses()
    }

    fun updateSelectedCategory(categoryId: String?) {
        _courseListState.update { it.copy(selectedCategoryId = categoryId) }
        loadCourses()
    }

    fun updateSelectedLevel(level: String?) {
        _courseListState.update { it.copy(selectedLevel = level) }
        loadCourses()
    }

    fun resetFilters() {
        _courseListState.update {
            it.copy(
                searchQuery = "",
                selectedCategoryId = null,
                selectedLevel = null,
            )
        }
        loadCourses()
    }

    fun loadCourses() {
        viewModelScope.launch {
            val currentState = _courseListState.value
            _courseListState.update { it.copy(isLoading = true, errorMessage = null) }

            courseRepository.getCourses(
                searchQuery = currentState.searchQuery.takeIf { it.isNotBlank() },
                categoryId = currentState.selectedCategoryId,
                level = currentState.selectedLevel,
            ).fold(
                onSuccess = { courses ->
                    _courseListState.update {
                        it.copy(
                            isLoading = false,
                            courses = courses,
                            errorMessage = null,
                        )
                    }
                },
                onFailure = { error ->
                    _courseListState.update {
                        it.copy(
                            isLoading = false,
                            courses = emptyList(),
                            errorMessage = error.message ?: "Unable to load courses",
                        )
                    }
                },
            )
        }
    }

    fun loadCourseDetails(courseId: String, forceReload: Boolean = false) {
        if (!forceReload && loadedCourseDetailsId == courseId && _courseDetailsState.value.course != null) {
            return
        }

        viewModelScope.launch {
            val mockCourse = StudentCourseMockData.detailsFor(courseId)
            if (mockCourse != null) {
                loadedCourseDetailsId = courseId
                _courseDetailsState.value = StudentCourseDetailsUiState(
                    isLoading = false,
                    course = mockCourse,
                )
            } else {
                _courseDetailsState.value = StudentCourseDetailsUiState(
                    isLoading = false,
                    errorMessage = "Unable to load course details",
                )
            }
        }
    }
}
