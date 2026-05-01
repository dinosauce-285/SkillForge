package com.example.skillforge.core.navigation

import com.example.skillforge.domain.model.AuthSession

sealed interface AppRoute {
    data object Login : AppRoute
    data object Register : AppRoute

    data class StudentCourseListing(
        val session: AuthSession,
    ) : AppRoute

    data class StudentCourseDetails(
        val session: AuthSession,
        val courseId: String,
    ) : AppRoute

    data class CourseCurriculum(
        val session: AuthSession,
        val courseId: String,
    ) : AppRoute

    data class Checkout(
        val session: AuthSession,
        val courseId: String,
    ) : AppRoute

    data class LessonLearning(
        val session: AuthSession,
        val courseId: String,
        val lessonId: String,
    ) : AppRoute

    data class InstructorPortal(
        val session: AuthSession,
    ) : AppRoute

    data class Favorite(
        val session: AuthSession,
    ) : AppRoute

    data class MyCourses(
        val session: AuthSession,
    ) : AppRoute

    data class Profile(
        val session: AuthSession,
    ) : AppRoute
  
    data class CourseForm(
        val session: AuthSession ,
        val courseId: String? = null
    ) : AppRoute

    data class MaterialUpload(
        val session: AuthSession,
        val lessonId: String
    ) : AppRoute

    data class CourseManager(
        val session: AuthSession,
        val courseId: String
    ) : AppRoute

    data class QuizBuilder(
        val session: AuthSession,
        val courseId: String,
        val chapterId: String? = null,
        val quizId: String? = null,
        val initialTab: Int = 0
    ) : AppRoute

    data class AddQuestion(
        val session: AuthSession,
        val courseId: String,
        val chapterId: String? = null,
        val quizId: String? = null,
        val questionId: String? = null
    ) : AppRoute

    data class Home(val session: AuthSession) : AppRoute
}
