package com.example.skillforge.core.di

import android.content.Context
import com.example.skillforge.BuildConfig
import com.example.skillforge.core.network.AuthInterceptor
import com.example.skillforge.core.network.TokenAuthenticator
import com.example.skillforge.data.local.AuthPreferences
import com.example.skillforge.data.remote.AuthApi
import com.example.skillforge.data.remote.CategoryApi
import com.example.skillforge.data.remote.ChapterApi
import com.example.skillforge.data.remote.CourseApi
import com.example.skillforge.data.remote.DiscussionApi
import com.example.skillforge.data.remote.FavoriteApi
import com.example.skillforge.data.remote.LessonApi
import com.example.skillforge.data.remote.MaterialApi
import com.example.skillforge.data.remote.OrderApi
import com.example.skillforge.data.remote.ProgressApi
import com.example.skillforge.data.repository.AuthRepositoryImpl
import com.example.skillforge.data.repository.CategoryRepositoryImpl
import com.example.skillforge.data.repository.CourseRepositoryImpl
import com.example.skillforge.data.repository.ChapterRepositoryImpl
import com.example.skillforge.data.repository.FavoriteRepositoryImpl
import com.example.skillforge.data.repository.LessonRepositoryImpl
import com.example.skillforge.data.repository.MaterialRepositoryImpl
import com.example.skillforge.data.repository.OrderRepositoryImpl
import com.example.skillforge.data.repository.ProgressRepositoryImpl
import com.example.skillforge.domain.repository.AuthRepository
import com.example.skillforge.domain.repository.CategoryRepository
import com.example.skillforge.domain.repository.CourseRepository
import com.example.skillforge.domain.repository.ChapterRepository
import com.example.skillforge.domain.repository.FavoriteRepository
import com.example.skillforge.domain.repository.LessonRepository
import com.example.skillforge.domain.repository.MaterialRepository
import com.example.skillforge.domain.repository.OrderRepository
import com.example.skillforge.domain.repository.ProgressRepository
import com.example.skillforge.domain.usecase.CheckSessionUseCase
import com.example.skillforge.domain.usecase.LoginUseCase
import com.example.skillforge.domain.usecase.RegisterUseCase
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(private val applicationContext: Context) {
    
    // --- Local Storage ---
    private val authPreferences = AuthPreferences(applicationContext)

    // --- Supabase Client ---
    val supabase = createSupabaseClient(
        supabaseUrl = "https://awenevlehjlpiyfxlpky.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImF3ZW5ldmxlaGpscGl5ZnhscGt5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQyNjQ5NTksImV4cCI6MjA4OTg0MDk1OX0.XifMoJE8q8Gf_rDS1mbSGRM5E8MxnEH_M8IFbGvzauI"
    ) {
        install(Auth) {
            scheme = "myapp"
            host = "callback"
        }
    }

    // --- OkHttpClient Configuration ---
    // Inject AuthPreferences into Interceptor and Authenticator
    private val authInterceptor = AuthInterceptor(authPreferences)
    private val tokenAuthenticator = TokenAuthenticator(authPreferences)

    // Build the HTTP Client integrating both mechanisms
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .authenticator(tokenAuthenticator)
        .build()

    // --- Retrofit Configuration ---
    // Attach the custom OkHttpClient to Retrofit
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // --- APIs ---
    private val authApi = retrofit.create(AuthApi::class.java)
    private val courseApi = retrofit.create(CourseApi::class.java)
    private val categoryApi = retrofit.create(CategoryApi::class.java)
    private val chapterApi = retrofit.create(ChapterApi::class.java)
    private val favoriteApi = retrofit.create(FavoriteApi::class.java)
    private val lessonApi = retrofit.create(LessonApi::class.java)
    private val discussionApi = retrofit.create(DiscussionApi::class.java)
    private val orderApi = retrofit.create(OrderApi::class.java)
    private val progressApi = retrofit.create(ProgressApi::class.java)
    
    private val materialApi: MaterialApi by lazy {
        retrofit.create(MaterialApi::class.java)
    }

    // --- Repositories ---
    // Make sure to pass authPreferences into AuthRepositoryImpl
    private val authRepository: AuthRepository = AuthRepositoryImpl(authApi, authPreferences, supabase)
    
    val courseRepository: CourseRepository = CourseRepositoryImpl(courseApi)
    val categoryRepository: CategoryRepository = CategoryRepositoryImpl(categoryApi)
    val chapterRepository: ChapterRepository = ChapterRepositoryImpl(chapterApi)
    val favoriteRepository: FavoriteRepository = FavoriteRepositoryImpl(favoriteApi)
    val lessonRepository: LessonRepository = LessonRepositoryImpl(lessonApi, discussionApi)
    val orderRepository: OrderRepository = OrderRepositoryImpl(orderApi)
    val progressRepository: ProgressRepository = ProgressRepositoryImpl(progressApi)
    
    val materialRepository: MaterialRepository by lazy {
        MaterialRepositoryImpl(materialApi)
    }

    // --- Use Cases ---
    val loginUseCase = LoginUseCase(authRepository)
    val registerUseCase = RegisterUseCase(authRepository)
    val checkSessionUseCase = CheckSessionUseCase(authRepository)
}