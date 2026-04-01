package com.example.skillforge.core.di

import com.example.skillforge.data.remote.AuthApi
import com.example.skillforge.data.remote.CategoryApi
import com.example.skillforge.data.remote.ChapterApi
import com.example.skillforge.data.remote.CourseApi
import com.example.skillforge.data.remote.LessonApi
import com.example.skillforge.data.repository.AuthRepositoryImpl
import com.example.skillforge.data.repository.CategoryRepositoryImpl
import com.example.skillforge.data.repository.CourseRepositoryImpl
import com.example.skillforge.data.repository.ChapterRepositoryImpl
import com.example.skillforge.data.repository.LessonRepositoryImpl
import com.example.skillforge.domain.repository.AuthRepository
import com.example.skillforge.domain.repository.CategoryRepository
import com.example.skillforge.domain.repository.CourseRepository
import com.example.skillforge.domain.repository.ChapterRepository
import com.example.skillforge.domain.repository.LessonRepository
import com.example.skillforge.domain.usecase.LoginUseCase
import com.example.skillforge.domain.usecase.RegisterUseCase
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.1.7:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val authApi = retrofit.create(AuthApi::class.java)
    private val courseApi = retrofit.create(CourseApi::class.java)
    private val categoryApi = retrofit.create(CategoryApi::class.java)
    private val chapterApi = retrofit.create(ChapterApi::class.java)
    private val lessonApi = retrofit.create(LessonApi::class.java)
    private val authRepository: AuthRepository = AuthRepositoryImpl(authApi)
    val courseRepository: CourseRepository = CourseRepositoryImpl(courseApi)
    val categoryRepository: CategoryRepository = CategoryRepositoryImpl(categoryApi)

    val chapterRepository: ChapterRepository = ChapterRepositoryImpl(chapterApi)

    val lessonRepository: LessonRepository = LessonRepositoryImpl(lessonApi)
    val loginUseCase = LoginUseCase(authRepository)
    val registerUseCase = RegisterUseCase(authRepository)
}
