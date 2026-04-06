package com.example.skillforge.feature.student_courses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.domain.model.Category
import com.example.skillforge.domain.model.CourseDetails
import com.example.skillforge.domain.model.LessonContent
import com.example.skillforge.domain.model.CourseSummary
import com.example.skillforge.domain.repository.CategoryRepository
import com.example.skillforge.domain.repository.CourseRepository
import com.example.skillforge.domain.repository.LessonRepository
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
    val isEnrolled: Boolean = false,
    val errorMessage: String? = null,
)

data class LessonContentUiState(
    val isLoading: Boolean = false,
    val lesson: LessonContent? = null,
    val errorMessage: String? = null,
)

class StudentCoursesViewModel(
    private val courseRepository: CourseRepository,
    private val categoryRepository: CategoryRepository,
    private val lessonRepository: LessonRepository,
) : ViewModel() {
    private val _courseListState = MutableStateFlow(StudentCourseListUiState(isLoading = true))
    val courseListState: StateFlow<StudentCourseListUiState> = _courseListState

    private val _courseDetailsState = MutableStateFlow(StudentCourseDetailsUiState())
    val courseDetailsState: StateFlow<StudentCourseDetailsUiState> = _courseDetailsState

    private val _lessonContentState = MutableStateFlow(LessonContentUiState())
    val lessonContentState: StateFlow<LessonContentUiState> = _lessonContentState

    private var loadedCourseDetailsId: String? = null
    private var loadedLessonId: String? = null

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

    fun loadCourseDetails(courseId: String, token: String, forceReload: Boolean = false) {
        viewModelScope.launch {
            if (!forceReload && loadedCourseDetailsId == courseId && _courseDetailsState.value.course != null) {
                val enrollmentResult = courseRepository.getEnrollmentStatus(token, courseId)
                val userIsEnrolled = enrollmentResult.getOrNull() ?: false
                _courseDetailsState.value = _courseDetailsState.value.copy(
                    isEnrolled = userIsEnrolled
                )
                return@launch
            }
            _courseDetailsState.value = StudentCourseDetailsUiState(isLoading = true)

            courseRepository.getCourseDetails(courseId).fold(
                onSuccess = { course ->
                    loadedCourseDetailsId = courseId
                    val enrollmentResult = courseRepository.getEnrollmentStatus(token, courseId)
                    val userIsEnrolled = enrollmentResult.getOrNull() ?: false

                    _courseDetailsState.value = StudentCourseDetailsUiState(
                        isLoading = false,
                        course = course,
                        isEnrolled = userIsEnrolled,
                    )
                },
                onFailure = { error ->
                    _courseDetailsState.value = StudentCourseDetailsUiState(
                        isLoading = false,
                        errorMessage = error.message ?: "Unable to load course details",
                    )
                },
            )
        }
    }

    fun loadLessonContent(token: String, lessonId: String, forceReload: Boolean = false) {
        if (!forceReload && loadedLessonId == lessonId && _lessonContentState.value.lesson != null) {
            return
        }

        viewModelScope.launch {
            _lessonContentState.value = LessonContentUiState(isLoading = true)

            lessonRepository.getLessonDetails(token, lessonId).fold(
                onSuccess = { lesson ->
                    android.util.Log.d("SKILLFORGE_DEBUG", "Lesson loaded successfully in ViewModel: \${lesson.title}")
                    loadedLessonId = lessonId
                    _lessonContentState.value = LessonContentUiState(
                        isLoading = false,
                        lesson = lesson,
                    )
                },
                onFailure = { error ->
                    android.util.Log.e("SKILLFORGE_DEBUG", "ViewModel failed to load lesson content: \${error.message}", error)
                    _lessonContentState.value = LessonContentUiState(
                        isLoading = false,
                        errorMessage = error.message ?: "Unable to load lesson",
                    )
                },
            )
        }
    }
}
