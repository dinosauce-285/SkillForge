package com.example.skillforge.feature.student_courses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.data.remote.DiscussionDto
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
    val suggestions: List<CourseSummary> = emptyList(),
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
    val completedLessonIds: List<String> = emptyList(),
    val errorMessage: String? = null,
)

data class LessonContentUiState(
    val isLoading: Boolean = false,
    val lesson: LessonContent? = null,
    val discussions: List<DiscussionDto> = emptyList(),
    val errorMessage: String? = null,
)

class StudentCoursesViewModel(
    private val courseRepository: CourseRepository,
    private val categoryRepository: CategoryRepository,
    private val lessonRepository: LessonRepository,
    private val progressRepository: com.example.skillforge.domain.repository.ProgressRepository,
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
            val suggestionsDeferred = async { courseRepository.getCourseSuggestions() }

            val categoriesResult = categoriesDeferred.await()
            val coursesResult = coursesDeferred.await()
            val suggestionsResult = suggestionsDeferred.await()

            val categories = categoriesResult.getOrDefault(currentState.categories)
            val suggestions = suggestionsResult.getOrDefault(emptyList())

            coursesResult.fold(
                onSuccess = { courses ->
                    _courseListState.value = currentState.copy(
                        isLoading = false,
                        categories = categories,
                        suggestions = suggestions,
                        courses = courses,
                        errorMessage = categoriesResult.exceptionOrNull()?.message,
                    )
                },
                onFailure = { error ->
                    _courseListState.value = currentState.copy(
                        isLoading = false,
                        categories = categories,
                        suggestions = suggestions,
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
                    
                    var completedLessons: List<String> = emptyList()
                    if (userIsEnrolled) {
                        try {
                            val progress = progressRepository.getCourseProgress(courseId)
                            completedLessons = progress.completedLessonIds ?: emptyList()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    _courseDetailsState.value = StudentCourseDetailsUiState(
                        isLoading = false,
                        course = course,
                        isEnrolled = userIsEnrolled,
                        completedLessonIds = completedLessons,
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
        viewModelScope.launch {
            // SAFE: Keep existing state (like discussions), only change loading status
            _lessonContentState.update { currentState ->
                currentState.copy(isLoading = true, errorMessage = null)
            }

            // IMPORTANT: Start fetching discussions at the same time
            loadDiscussions(token, lessonId)

            lessonRepository.getLessonDetails(token, lessonId).fold(
                onSuccess = { lesson ->
                    android.util.Log.d("SKILLFORGE_DEBUG", "Lesson loaded successfully in ViewModel: ${lesson.title}")
                    loadedLessonId = lessonId

                    // SAFE: Keep existing state (like discussions), only update the lesson data
                    _lessonContentState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            lesson = lesson
                        )
                    }
                },
                onFailure = { error ->
                    android.util.Log.e("SKILLFORGE_DEBUG", "ViewModel failed to load lesson content: ${error.message}", error)

                    // SAFE: Keep existing state, only update the error message
                    _lessonContentState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Unable to load lesson"
                        )
                    }
                }
            )
        }
    }

    private fun loadDiscussions(token: String, lessonId: String) {
        viewModelScope.launch {
            lessonRepository.getDiscussions(token, lessonId).fold(
                onSuccess = { discussionList ->
                    _lessonContentState.update { currentState ->
                        currentState.copy(discussions = discussionList)
                    }
                },
                onFailure = { error ->
                    error.printStackTrace()
                }
            )
        }
    }

    fun postDiscussion(token: String, lessonId: String, content: String, parentId: String?) {
        viewModelScope.launch {
            lessonRepository.postDiscussion(token, lessonId, content, parentId).fold(
                onSuccess = {
                    // Refresh discussions after posting successfully
                    loadDiscussions(token, lessonId)
                },
                onFailure = { error ->
                    error.printStackTrace()
                }
            )
        }
    }
}
