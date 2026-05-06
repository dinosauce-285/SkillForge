package com.example.skillforge.core.di

import android.content.Context
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
import com.example.skillforge.data.remote.DashboardApi
import com.example.skillforge.data.remote.ReviewApi
import com.example.skillforge.data.repository.AuthRepositoryImpl
import com.example.skillforge.data.repository.CategoryRepositoryImpl
import com.example.skillforge.data.repository.CourseRepositoryImpl
import com.example.skillforge.data.repository.ChapterRepositoryImpl
import com.example.skillforge.data.repository.DashboardRepositoryImpl
import com.example.skillforge.data.repository.FavoriteRepositoryImpl
import com.example.skillforge.data.repository.LessonRepositoryImpl
import com.example.skillforge.data.repository.MaterialRepositoryImpl
import com.example.skillforge.data.repository.OrderRepositoryImpl
import com.example.skillforge.data.repository.ProgressRepositoryImpl
import com.example.skillforge.data.repository.ReviewRepositoryImpl
import com.example.skillforge.domain.repository.AuthRepository
import com.example.skillforge.domain.repository.CategoryRepository
import com.example.skillforge.domain.repository.CourseRepository
import com.example.skillforge.domain.repository.ChapterRepository
import com.example.skillforge.domain.repository.DashboardRepository
import com.example.skillforge.domain.repository.FavoriteRepository
import com.example.skillforge.domain.repository.LessonRepository
import com.example.skillforge.domain.repository.MaterialRepository
import com.example.skillforge.domain.repository.OrderRepository
import com.example.skillforge.domain.repository.ProgressRepository
import com.example.skillforge.domain.repository.ReviewRepository
import com.example.skillforge.domain.usecase.CheckSessionUseCase
import com.example.skillforge.domain.usecase.LoginUseCase
import com.example.skillforge.domain.usecase.RegisterUseCase
import com.example.skillforge.data.remote.*
import com.example.skillforge.data.repository.*
import com.example.skillforge.domain.repository.*
import com.example.skillforge.domain.usecase.*
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.skillforge.data.local.AuthPreferences
import com.example.skillforge.core.network.TokenAuthenticator

class AppContainer(private val context: Context) {
    
    // Supabase Client initialization
    val supabase = createSupabaseClient(
        supabaseUrl = "https://awenevlehjlpiyfxlpky.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImF3ZW5ldmxlaGpscGl5ZnhscGt5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzQyNjQ5NTksImV4cCI6MjA4OTg0MDk1OX0.XifMoJE8q8Gf_rDS1mbSGRM5E8MxnEH_M8IFbGvzauI"
    ) {
        install(Auth) {
            scheme = "myapp"
            host = "callback"
        }
    }

    // Network logging interceptor for debugging
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authPreferences = AuthPreferences(context)

    // OkHttpClient with automatic Authorization header injection
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val accessToken = runBlocking { authPreferences.accessToken.firstOrNull() }
            
            val requestBuilder = originalRequest.newBuilder()
            if (!accessToken.isNullOrBlank()) {
                requestBuilder.header("Authorization", "Bearer $accessToken")
            }
            
            chain.proceed(requestBuilder.build())
        }
        .authenticator(TokenAuthenticator(authPreferences))
        .build()

    // Retrofit instance configured for the custom backend
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:3000/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // --- API Definitions ---
    private val authApi = retrofit.create(AuthApi::class.java)
    private val userApi = retrofit.create(UserApi::class.java)
    private val courseApi = retrofit.create(CourseApi::class.java)
    private val categoryApi = retrofit.create(CategoryApi::class.java)
    private val chapterApi = retrofit.create(ChapterApi::class.java)
    private val favoriteApi = retrofit.create(FavoriteApi::class.java)
    private val lessonApi = retrofit.create(LessonApi::class.java)

    private val discussionApi = retrofit.create(DiscussionApi::class.java)
    private val orderApi = retrofit.create(OrderApi::class.java)
    private val progressApi = retrofit.create(ProgressApi::class.java)
    private val notificationApi = retrofit.create(NotificationApi::class.java)

    private val dashboardApi= retrofit.create(DashboardApi::class.java)
    private val couponApi = retrofit.create(CouponApi::class.java)
    private val quizApi = retrofit.create(QuizApi::class.java)
    private val adminApi = retrofit.create(AdminApi::class.java)
    private val subscriptionApi = retrofit.create(SubscriptionApi::class.java)
    
    private val materialApi: MaterialApi by lazy {
        retrofit.create(MaterialApi::class.java)
    }

    private val reviewApi: ReviewApi by lazy {
        retrofit.create(ReviewApi::class.java)
    }

    // --- Repositories ---
    val authRepository: AuthRepository = AuthRepositoryImpl(authApi, supabase, authPreferences)
    val userRepository: UserRepository = UserRepositoryImpl(userApi)
    val courseRepository: CourseRepository = CourseRepositoryImpl(courseApi)
    val categoryRepository: CategoryRepository = CategoryRepositoryImpl(categoryApi)
    val chapterRepository: ChapterRepository = ChapterRepositoryImpl(chapterApi)
    val favoriteRepository: FavoriteRepository = FavoriteRepositoryImpl(favoriteApi)
    val lessonRepository: LessonRepository = LessonRepositoryImpl(lessonApi, discussionApi)
    val materialRepository: MaterialRepository by lazy {
        MaterialRepositoryImpl(materialApi)
    }
    val orderRepository: OrderRepository = OrderRepositoryImpl(orderApi)
    val progressRepository: ProgressRepository = ProgressRepositoryImpl(progressApi)
    val notificationRepository: NotificationRepository = NotificationRepositoryImpl(notificationApi)
    val reviewRepository: ReviewRepository = ReviewRepositoryImpl(reviewApi)
    val dashboardRepository: DashboardRepository = DashboardRepositoryImpl(dashboardApi)
    val couponRepository: CouponRepository = CouponRepositoryImpl(couponApi)
    val quizRepository: QuizRepository = QuizRepositoryImpl(quizApi)
    val adminRepository: AdminRepository = AdminRepositoryImpl(adminApi)
    val subscriptionRepository: SubscriptionRepository = SubscriptionRepositoryImpl(subscriptionApi)

    // --- Use Cases ---
    val loginUseCase = LoginUseCase(authRepository)
    val registerUseCase = RegisterUseCase(authRepository)
    val checkSessionUseCase = CheckSessionUseCase(authRepository)
    val logoutUseCase = LogoutUseCase(authRepository)
    val becomeInstructorUseCase = BecomeInstructorUseCase(subscriptionRepository)
    
    // Profile Use Cases
    val getProfileUseCase = GetProfileUseCase(userRepository)
    val updateProfileUseCase = UpdateProfileUseCase(userRepository)
    val updateAvatarUseCase = UpdateAvatarUseCase(userRepository)

}
