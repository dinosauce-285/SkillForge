package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.CourseApi
import com.example.skillforge.domain.model.CourseChapter
import com.example.skillforge.domain.model.CourseDetails
import com.example.skillforge.domain.model.CourseSummary
import com.example.skillforge.domain.repository.CourseRepository

class CourseRepositoryImpl(
    private val api: CourseApi,
) : CourseRepository {
    override suspend fun getCourses(): Result<List<CourseSummary>> {
        return try {
            val response = api.getCourses()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data.map { dto ->
                    CourseSummary(
                        id = dto.id,
                        title = dto.title,
                        subtitle = dto.subtitle,
                        summary = dto.summary,
                        thumbnailUrl = dto.thumbnailUrl,
                        categoryName = dto.category.name,
                        instructorName = dto.instructor.fullName,
                        level = dto.level,
                        price = dto.price,
                        isFree = dto.isFree,
                        averageRating = dto.averageRating,
                        studentCount = dto.studentCount,
                        reviewCount = dto.counts.reviews,
                        chapterCount = dto.counts.chapters,
                        tags = dto.tags.map { it.name },
                    )
                })
            } else {
                Result.failure(Exception("Failed to load courses"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to load courses"))
        }
    }

    override suspend fun getCourseDetails(courseId: String): Result<CourseDetails> {
        return try {
            val response = api.getCourseDetails(courseId)
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                Result.success(
                    CourseDetails(
                        id = dto.id,
                        title = dto.title,
                        subtitle = dto.subtitle,
                        summary = dto.summary,
                        thumbnailUrl = dto.thumbnailUrl,
                        categoryName = dto.category.name,
                        instructorName = dto.instructor.fullName,
                        instructorSkills = dto.instructor.profile?.skills.orEmpty(),
                        instructorGoals = dto.instructor.profile?.learningGoals,
                        level = dto.level,
                        price = dto.price,
                        isFree = dto.isFree,
                        averageRating = dto.averageRating,
                        studentCount = dto.studentCount,
                        reviewCount = dto.counts.reviews,
                        chapterCount = dto.counts.chapters,
                        tags = dto.tags.map { it.name },
                        chapters = dto.chapters.map { chapter ->
                            CourseChapter(
                                id = chapter.id,
                                title = chapter.title,
                                lessonTitles = chapter.lessons.map { it.title },
                            )
                        },
                    )
                )
            } else {
                Result.failure(Exception("Failed to load course details"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "Failed to load course details"))
        }
    }
}
