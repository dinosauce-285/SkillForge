package com.example.skillforge.feature.student_courses.ui

import com.example.skillforge.domain.model.CourseChapter
import com.example.skillforge.domain.model.CourseDetails
import com.example.skillforge.domain.model.CourseLesson
import com.example.skillforge.domain.model.CourseSummary

object StudentCourseMockData {
    val featuredCourses = listOf(
        CourseSummary(
            id = "course-1",
            title = "Product Design System Mastery",
            subtitle = "Build design systems that scale across teams and releases.",
            summary = "From tokens and component architecture to governance and collaboration.",
            thumbnailUrl = "https://images.unsplash.com/photo-1545239351-1141bd82e8a6?auto=format&fit=crop&w=1200&q=80",
            categoryId = "design",
            categoryName = "Design",
            instructorName = "Ariana Brooks",
            level = "INTERMEDIATE",
            price = 49.0,
            isFree = false,
            averageRating = 4.8f,
            studentCount = 1284,
            reviewCount = 312,
            chapterCount = 8,
            tags = listOf("Figma", "Design Systems", "Product"),
        ),
        CourseSummary(
            id = "course-2",
            title = "Modern Android UI with Compose",
            subtitle = "Create polished mobile apps with real production patterns.",
            summary = "Learn state, architecture, navigation, and performance techniques for Compose apps.",
            thumbnailUrl = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=1200&q=80",
            categoryId = "development",
            categoryName = "Development",
            instructorName = "Marcus Lin",
            level = "BEGINNER",
            price = 0.0,
            isFree = true,
            averageRating = 4.9f,
            studentCount = 2310,
            reviewCount = 481,
            chapterCount = 11,
            tags = listOf("Android", "Kotlin", "Compose"),
        ),
        CourseSummary(
            id = "course-3",
            title = "Startup Growth and Digital Marketing",
            subtitle = "Plan campaigns, validate channels, and grow with clear metrics.",
            summary = "A practical starter course for product, growth, and marketing collaboration.",
            thumbnailUrl = "https://images.unsplash.com/photo-1552664730-d307ca884978?auto=format&fit=crop&w=1200&q=80",
            categoryId = "business",
            categoryName = "Business",
            instructorName = "Alex Rivera",
            level = "INTERMEDIATE",
            price = 45.5,
            isFree = false,
            averageRating = 4.4f,
            studentCount = 980,
            reviewCount = 214,
            chapterCount = 7,
            tags = listOf("Growth", "Marketing", "Strategy"),
        ),
    )

    private val mockCourseDetails = listOf(
        CourseDetails(
            id = "course-1",
            title = "Product Design System Mastery",
            subtitle = "Build design systems that scale across teams and releases.",
            summary = "This course covers practical design system foundations, token strategy, component libraries, documentation, and rollout planning so teams can ship consistent product experiences with less friction.",
            thumbnailUrl = "https://images.unsplash.com/photo-1545239351-1141bd82e8a6?auto=format&fit=crop&w=1200&q=80",
            categoryName = "Design",
            instructorName = "Ariana Brooks",
            instructorSkills = listOf("Design Systems", "Product Strategy", "Figma"),
            instructorGoals = "Help teams move from isolated screens to a cohesive product language.",
            level = "INTERMEDIATE",
            price = 49.0,
            isFree = false,
            averageRating = 4.8f,
            studentCount = 1284,
            reviewCount = 312,
            chapterCount = 8,
            tags = listOf("Figma", "Design Systems", "Product"),
            chapters = listOf(
                CourseChapter(
                    id = "chapter-1",
                    title = "System Foundations",
                    lessons = listOf(
                        CourseLesson("lesson-1", "Why design systems fail"),
                        CourseLesson("lesson-2", "Tokens and naming"),
                        CourseLesson("lesson-3", "Documentation basics"),
                    ),
                ),
                CourseChapter(
                    id = "chapter-2",
                    title = "Components and Governance",
                    lessons = listOf(
                        CourseLesson("lesson-4", "Building component rules"),
                        CourseLesson("lesson-5", "Review workflows"),
                        CourseLesson("lesson-6", "Versioning decisions"),
                    ),
                ),
            ),
        ),
        CourseDetails(
            id = "course-2",
            title = "Modern Android UI with Compose",
            subtitle = "Create polished mobile apps with real production patterns.",
            summary = "You will build modern Android interfaces with Compose, state handling, reusable components, and screen architecture that feels ready for a real product team.",
            thumbnailUrl = "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?auto=format&fit=crop&w=1200&q=80",
            categoryName = "Development",
            instructorName = "Marcus Lin",
            instructorSkills = listOf("Android", "Kotlin", "Jetpack Compose"),
            instructorGoals = "Help Android developers move from demos to maintainable product UI.",
            level = "BEGINNER",
            price = 0.0,
            isFree = true,
            averageRating = 4.9f,
            studentCount = 2310,
            reviewCount = 481,
            chapterCount = 11,
            tags = listOf("Android", "Kotlin", "Compose"),
            chapters = listOf(
                CourseChapter(
                    id = "chapter-1",
                    title = "Compose Foundations",
                    lessons = listOf(
                        CourseLesson("lesson-7", "Composable thinking"),
                        CourseLesson("lesson-8", "Layouts and modifiers"),
                        CourseLesson("lesson-9", "Material 3 setup"),
                    ),
                ),
                CourseChapter(
                    id = "chapter-2",
                    title = "State and Screen Patterns",
                    lessons = listOf(
                        CourseLesson("lesson-10", "Hoisted state"),
                        CourseLesson("lesson-11", "UI state models"),
                        CourseLesson("lesson-12", "Navigation basics"),
                    ),
                ),
            ),
        ),
        CourseDetails(
            id = "course-3",
            title = "Startup Growth and Digital Marketing",
            subtitle = "Plan campaigns, validate channels, and grow with clear metrics.",
            summary = "This course introduces channel selection, landing-page experiments, content loops, and simple measurement frameworks for early-stage teams.",
            thumbnailUrl = "https://images.unsplash.com/photo-1552664730-d307ca884978?auto=format&fit=crop&w=1200&q=80",
            categoryName = "Business",
            instructorName = "Alex Rivera",
            instructorSkills = listOf("Growth", "SEO", "Positioning"),
            instructorGoals = "Teach founders and operators how to choose sustainable growth levers.",
            level = "INTERMEDIATE",
            price = 45.5,
            isFree = false,
            averageRating = 4.4f,
            studentCount = 980,
            reviewCount = 214,
            chapterCount = 7,
            tags = listOf("Growth", "Marketing", "Strategy"),
            chapters = listOf(
                CourseChapter(
                    id = "chapter-1",
                    title = "Growth Basics",
                    lessons = listOf(
                        CourseLesson("lesson-13", "Finding your audience"),
                        CourseLesson("lesson-14", "Offer clarity"),
                        CourseLesson("lesson-15", "Core funnel metrics"),
                    ),
                ),
                CourseChapter(
                    id = "chapter-2",
                    title = "Campaign Experiments",
                    lessons = listOf(
                        CourseLesson("lesson-16", "Channel testing"),
                        CourseLesson("lesson-17", "Landing page iteration"),
                        CourseLesson("lesson-18", "Retention signals"),
                    ),
                ),
            ),
        ),
    )

    val courseDetails = mockCourseDetails.first()

    fun detailsFor(courseId: String): CourseDetails? {
        return mockCourseDetails.firstOrNull { it.id == courseId }
    }
}
