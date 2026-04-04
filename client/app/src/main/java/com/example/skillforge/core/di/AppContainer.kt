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
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {
    // Supabase Client
    val supabase = createSupabaseClient(
        supabaseUrl = "https://awenevlehjlpiyfxlpky.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImF3ZW5ldmxlaGpscGl5ZnhscGt5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQyNjQ5NTksImV4cCI6MjA4OTg0MDk1OX0.XifMoJE8q8Gf_rDS1mbSGRM5E8MxnEH_M8IFbGvzauI"
    ) {
        install(Auth) {
            scheme = "myapp"
            host = "callback"
        }
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:3000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val authApi = retrofit.create(AuthApi::class.java)
    private val courseApi = retrofit.create(CourseApi::class.java)

    private val authRepository: AuthRepository = AuthRepositoryImpl(authApi, supabase)
    private val categoryApi = retrofit.create(CategoryApi::class.java)
    private val chapterApi = retrofit.create(ChapterApi::class.java)
    private val lessonApi = retrofit.create(LessonApi::class.java)
    val courseRepository: CourseRepository = CourseRepositoryImpl(courseApi)
    val categoryRepository: CategoryRepository = CategoryRepositoryImpl(categoryApi)

    val chapterRepository: ChapterRepository = ChapterRepositoryImpl(chapterApi)

    val lessonRepository: LessonRepository = LessonRepositoryImpl(lessonApi)
    val loginUseCase = LoginUseCase(authRepository)
    val registerUseCase = RegisterUseCase(authRepository)
}
