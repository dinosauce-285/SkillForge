package com.example.skillforge.feature.student_courses.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillforge.domain.model.CourseDetails
import com.example.skillforge.domain.model.CourseSummary
import com.example.skillforge.domain.repository.CourseRepository
import com.example.skillforge.feature.student_courses.ui.StudentCourseMockData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class StudentCourseListUiState(
    val isLoading: Boolean = false,
    val courses: List<CourseSummary> = emptyList(),
    val errorMessage: String? = null,
)

data class StudentCourseDetailsUiState(
    val isLoading: Boolean = false,
    val course: CourseDetails? = null,
    val errorMessage: String? = null,
)

class StudentCoursesViewModel(
    private val courseRepository: CourseRepository,
) : ViewModel() {
    private val _courseListState = MutableStateFlow(StudentCourseListUiState(isLoading = true))
    val courseListState: StateFlow<StudentCourseListUiState> = _courseListState

    private val _courseDetailsState = MutableStateFlow(StudentCourseDetailsUiState())
    val courseDetailsState: StateFlow<StudentCourseDetailsUiState> = _courseDetailsState

    private var loadedCourseDetailsId: String? = null

    init {
        loadCourses()
    }

    fun loadCourses() {
        viewModelScope.launch {
            _courseListState.value = StudentCourseListUiState(
                isLoading = false,
                courses = StudentCourseMockData.featuredCourses,
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
