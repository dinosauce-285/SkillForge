package com.example.skillforge.feature.instructor_portal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillforge.domain.repository.ChapterRepository
import com.example.skillforge.domain.repository.CourseRepository
import com.example.skillforge.domain.repository.LessonRepository

@Suppress("UNCHECKED_CAST")
class CourseManagerViewModelFactory(
    private val courseManagerRepo: CourseRepository,
    private val chapterRepo: ChapterRepository,
    private val lessonRepo: LessonRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CourseManagerViewModel::class.java)) {
            return CourseManagerViewModel(courseManagerRepo, chapterRepo, lessonRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}