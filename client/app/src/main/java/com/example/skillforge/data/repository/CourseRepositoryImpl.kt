package com.example.skillforge.data.repository

import com.example.skillforge.data.remote.CourseApi
import com.example.skillforge.data.remote.CreateCourseRequest
import com.example.skillforge.domain.model.CourseChapter
import com.example.skillforge.domain.model.CourseDetails
import com.example.skillforge.domain.model.CourseLesson
import com.example.skillforge.domain.model.CourseSummary
import com.example.skillforge.domain.repository.CourseRepository
import com.example.skillforge.data.remote.CourseManagerDto
import com.example.skillforge.data.remote.CourseSummaryDto

class CourseRepositoryImpl(
    private val api: CourseApi,
) : CourseRepository {

    override suspend fun getCourses(
        searchQuery: String?,
        categoryId: String?,
        level: String?,
    ): Result<List<CourseSummary>> {
        return try {
            val response = api.getCourses(searchQuery, categoryId, level)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data.map { dto ->
                    CourseSummary(
                        id = dto.id,
                        title = dto.title,
                        subtitle = dto.subtitle,
                        summary = dto.summary,
                        thumbnailUrl = dto.thumbnailUrl,
                        categoryId = dto.category.id,
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
                                lessons = chapter.lessons.map { lesson ->
                                    CourseLesson(
                                        id = lesson.id,
                                        title = lesson.title,
                                    )
                                },
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

    override suspend fun createCourse(
        token: String, title: String, summary: String, price: Double, categoryId: String
    ): Result<Unit> {
        return try {
            val request = CreateCourseRequest(title, summary, price, categoryId)
            val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
            val response = api.createCourse(bearerToken, request)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Lỗi từ server: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi mạng: ${e.message}"))
        }
    }

    override suspend fun getCourseForManager(token: String, courseId: String): Result<CourseManagerDto> {
        return try {
            val response = api.getCourseForManager(courseId, "Bearer $token")

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    // 3. Bóc thành công cục data (CourseManagerDto) ra và gửi đi
                    Result.success(body)
                } else {
                    Result.failure(Exception("Dữ liệu trả về bị rỗng!"))
                }
            } else {
                Result.failure(Exception("Lỗi server: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyCourses(token: String): Result<List<CourseSummaryDto>> {
        return try {
            val response = api.getMyCourses("Bearer $token")
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Lỗi: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
