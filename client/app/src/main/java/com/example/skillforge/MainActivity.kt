package com.example.skillforge

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skillforge.core.designsystem.SkillforgeTheme
import com.example.skillforge.core.navigation.AppRoute
import com.example.skillforge.core.designsystem.components.SkillForgeBottomNavigationBar
import com.example.skillforge.feature.auth.ui.LoginScreen
import com.example.skillforge.feature.auth.ui.RegisterScreen
import com.example.skillforge.feature.auth.viewmodel.LoginViewModel
import com.example.skillforge.feature.auth.viewmodel.LoginViewModelFactory
import com.example.skillforge.feature.auth.viewmodel.RegisterViewModel
import com.example.skillforge.feature.auth.viewmodel.RegisterViewModelFactory
import com.example.skillforge.feature.favorite.ui.FavoriteRoute
import com.example.skillforge.feature.favorite.viewmodel.FavoriteViewModel
import com.example.skillforge.feature.favorite.viewmodel.FavoriteViewModelFactory
import com.example.skillforge.feature.home.ui.HomeScreen
import com.example.skillforge.feature.home.viewmodel.HomeViewModel
import com.example.skillforge.feature.home.viewmodel.HomeViewModelFactory
import com.example.skillforge.feature.instructor_portal.ui.SkillforgeCourseFormScreen
import com.example.skillforge.feature.instructor_portal.ui.SkillforgeCourseManagerScreen
import com.example.skillforge.feature.instructor_portal.ui.SkillforgeInstructorDashboardScreen
import com.example.skillforge.feature.instructor_portal.ui.SkillforgeMaterialUploadScreen
import com.example.skillforge.feature.instructor_portal.viewmodel.CourseFormState
import com.example.skillforge.feature.instructor_portal.viewmodel.CourseFormViewModel
import com.example.skillforge.feature.instructor_portal.viewmodel.CourseFormViewModelFactory
import com.example.skillforge.feature.instructor_portal.viewmodel.CourseManagerViewModel
import com.example.skillforge.feature.instructor_portal.viewmodel.CourseManagerViewModelFactory
import com.example.skillforge.feature.instructor_portal.viewmodel.InstructorPortalViewModel
import com.example.skillforge.feature.instructor_portal.viewmodel.InstructorPortalViewModelFactory
import com.example.skillforge.feature.instructor_portal.viewmodel.MaterialUploadViewModel
import com.example.skillforge.feature.instructor_portal.viewmodel.MaterialUploadViewModelFactory
import com.example.skillforge.feature.instructor_portal.viewmodel.UploadState
import com.example.skillforge.feature.student_courses.ui.CourseCurriculumRoute
import com.example.skillforge.feature.student_courses.ui.LessonLearningScreen
import com.example.skillforge.feature.student_courses.ui.MyCoursesScreen
import com.example.skillforge.feature.student_courses.ui.StudentCourseDetailsRoute
import com.example.skillforge.feature.student_courses.ui.StudentCourseListingRoute
import com.example.skillforge.feature.student_courses.ui.StudentProfileScreen
import com.example.skillforge.feature.student_courses.viewmodel.StudentCoursesViewModel
import com.example.skillforge.feature.student_courses.viewmodel.StudentCoursesViewModelFactory
import com.example.skillforge.feature.main.viewmodel.MainViewModel
import com.example.skillforge.feature.main.viewmodel.MainViewModelFactory
import com.example.skillforge.feature.transaction.ui.TransactionScreenRoute
import com.example.skillforge.feature.transaction.viewmodel.TransactionViewModel
import com.example.skillforge.feature.transaction.viewmodel.TransactionViewModelFactory
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkillforgeTheme(dynamicColor = false) {
                val appContainer = (LocalContext.current.applicationContext as SkillforgeApplication).container

                // Initialize ViewModels
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(appContainer.loginUseCase)
                )
                val registerViewModel: RegisterViewModel = viewModel(
                    factory = RegisterViewModelFactory(appContainer.registerUseCase)
                )
                val studentCoursesViewModel: StudentCoursesViewModel = viewModel(
                    factory = StudentCoursesViewModelFactory(
                        appContainer.courseRepository,
                        appContainer.categoryRepository,
                        appContainer.lessonRepository,
                    )
                )
                val transactionViewModel: TransactionViewModel = viewModel(
                    factory = TransactionViewModelFactory(
                        appContainer.courseRepository,
                        appContainer.orderRepository,
                    )
                )
                val favoriteViewModel: FavoriteViewModel = viewModel(
                    factory = FavoriteViewModelFactory(appContainer.favoriteRepository)
                )
                val courseFormViewModel: CourseFormViewModel = viewModel(
                    factory = CourseFormViewModelFactory(
                        appContainer.courseRepository,
                        appContainer.categoryRepository
                    )
                )
                val mainViewModel: MainViewModel = viewModel(
                    factory = MainViewModelFactory(appContainer.checkSessionUseCase)
                )

                val currentRoute by mainViewModel.uiState.collectAsState()

                LaunchedEffect(Unit) {
                    mainViewModel.checkSession()
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    val route = currentRoute
                    if (route == null) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            CircularProgressIndicator()
                        }
                    } else {

                        val showBottomBar = route is AppRoute.Home ||
                                route is AppRoute.StudentCourseListing ||
                                route is AppRoute.MyCourses ||
                                route is AppRoute.Favorite ||
                                route is AppRoute.Profile

                        androidx.compose.material3.Scaffold(
                            bottomBar = {
                                if (showBottomBar) {

                                    val session = when(route) {
                                        is AppRoute.Home -> route.session
                                        is AppRoute.StudentCourseListing -> route.session
                                        is AppRoute.MyCourses -> route.session
                                        is AppRoute.Favorite -> route.session
                                        is AppRoute.Profile -> route.session
                                        else -> null
                                    }

                                    SkillForgeBottomNavigationBar(
                                        currentRoute = when(route) {
                                            is AppRoute.Home -> "Home"
                                            is AppRoute.StudentCourseListing -> "Discover"
                                            is AppRoute.MyCourses -> "Learning"
                                            is AppRoute.Favorite -> "Wishlist"
                                            is AppRoute.Profile -> "Profile"
                                            else -> ""
                                        },
                                        onNavigateToHome = { session?.let { mainViewModel.navigateTo(AppRoute.Home(it)) } },
                                        onNavigateToDiscover = { session?.let { mainViewModel.navigateTo(AppRoute.StudentCourseListing(it)) } },
                                        onNavigateToLearning = { session?.let { mainViewModel.navigateTo(AppRoute.MyCourses(it)) } },
                                        onNavigateToWishlist = { session?.let { mainViewModel.navigateTo(AppRoute.Favorite(it)) } },
                                        onNavigateToProfile = { session?.let { mainViewModel.navigateTo(AppRoute.Profile(it)) } }
                                    )
                                }
                            }
                        ) { innerPadding ->
                                Box(modifier = Modifier.padding(innerPadding)) {
                                    when (route) {
                                        AppRoute.Login -> LoginScreen(
                                            viewModel = loginViewModel,
                                            onLoginSuccess = { session ->

                                                mainViewModel.navigateTo(
                                                    if (session.user.role.equals("STUDENT", ignoreCase = true)) {
                                                        AppRoute.Home(session)
                                                    } else {
                                                        AppRoute.InstructorPortal(session)
                                                    }
                                                )
                                            },
                                            onNavigateToRegister = {
                                                mainViewModel.navigateTo(AppRoute.Register)
                                            }
                                        )

                                        is AppRoute.Home -> {
                                            val session = route.session
                                            val token = session.accessToken

                                            val homeViewModel: HomeViewModel = viewModel(
                                                factory = HomeViewModelFactory(appContainer.progressRepository)
                                            )

                                            HomeScreen(
                                                token = token,
                                                viewModel = homeViewModel,
                                                onNavigateToMyCourses = {
                                                    mainViewModel.navigateTo(AppRoute.MyCourses(session))
                                                },
                                            )
                                        }

                                        AppRoute.Register -> RegisterScreen(
                                            viewModel = registerViewModel,
                                            onRegisterSuccess = {
                                                mainViewModel.navigateTo(AppRoute.Login)
                                            },
                                            onBackToLogin = {
                                                mainViewModel.navigateTo(AppRoute.Login)
                                            }
                                        )

                                        is AppRoute.StudentCourseListing -> StudentCourseListingRoute(
                                            session = route.session,
                                            viewModel = studentCoursesViewModel,
                                            onCourseSelected = { courseId ->
                                                mainViewModel.navigateTo(AppRoute.StudentCourseDetails(route.session, courseId))
                                            },

                                            onLogout = {
                                                mainViewModel.navigateTo(AppRoute.Login)
                                            }
                                        )

                                        is AppRoute.StudentCourseDetails -> StudentCourseDetailsRoute(
                                            courseId = route.courseId,
                                            token = route.session.accessToken,
                                            viewModel = studentCoursesViewModel,
                                            onOpenCurriculum = { courseId ->
                                                mainViewModel.navigateTo(AppRoute.CourseCurriculum(route.session, courseId))
                                            },
                                            onCheckoutSelected = { courseId ->
                                                mainViewModel.navigateTo(AppRoute.Checkout(route.session, courseId))
                                            },
                                            onBack = {
                                                mainViewModel.navigateTo(AppRoute.StudentCourseListing(route.session))
                                            }
                                        )

                                        is AppRoute.CourseCurriculum -> CourseCurriculumRoute(
                                            courseId = route.courseId,
                                            token = route.session.accessToken,
                                            viewModel = studentCoursesViewModel,
                                            onLessonSelected = { lessonId ->
                                                mainViewModel.navigateTo(AppRoute.LessonLearning(route.session, route.courseId, lessonId))
                                            },
                                            onNavigateBack = {
                                                mainViewModel.navigateTo(AppRoute.StudentCourseDetails(route.session, route.courseId))
                                            }
                                        )

                                        is AppRoute.Checkout -> TransactionScreenRoute(

                                            courseId = route.courseId,
                                            token = route.session.accessToken,
                                            viewModel = transactionViewModel,
                                            onBackClick = {
                                                mainViewModel.navigateTo(AppRoute.StudentCourseDetails(route.session, route.courseId))
                                            },
                                            onPaymentSuccess = {
                                                studentCoursesViewModel.loadCourseDetails(route.courseId, route.session.accessToken, forceReload = true)
                                                mainViewModel.navigateTo(AppRoute.CourseCurriculum(route.session, route.courseId))
                                            }
                                        )

                                        is AppRoute.LessonLearning -> LessonLearningScreen(
                                            sessionToken = route.session.accessToken,
                                            courseId = route.courseId,
                                            lessonId = route.lessonId,
                                            viewModel = studentCoursesViewModel,
                                            onLessonSelected = { nextLessonId ->
                                                mainViewModel.navigateTo(AppRoute.LessonLearning(route.session, route.courseId, nextLessonId))
                                            },
                                            onNavigateBack = {
                                                mainViewModel.navigateTo(AppRoute.CourseCurriculum(route.session, route.courseId))
                                            }
                                        )

                                        is AppRoute.InstructorPortal -> {
                                            val portalViewModel: InstructorPortalViewModel = viewModel(
                                                factory = InstructorPortalViewModelFactory(
                                                    appContainer.courseRepository,
                                                    appContainer.dashboardRepository
                                                )
                                            )

                                            val courses by portalViewModel.courses.collectAsState()
                                            val isLoading by portalViewModel.isLoading.collectAsState()
                                            val dashboardData by portalViewModel.dashboardData.collectAsState()

                                            LaunchedEffect(Unit) {
                                                portalViewModel.fetchMyCourses(route.session.accessToken)
                                                portalViewModel.fetchDashboardData(route.session.accessToken)
                                            }

                                            SkillforgeInstructorDashboardScreen(
                                                courses = courses,
                                                isLoading = isLoading,
                                                dashboardData = dashboardData,
                                                onNavigateToCreateCourse = {
                                                    mainViewModel.navigateTo(AppRoute.CourseForm(route.session))
                                                },
                                                onCourseClick = { clickedCourseId ->
                                                    mainViewModel.navigateTo(AppRoute.CourseManager(route.session, clickedCourseId))
                                                },
                                                onNavigateToUploadMaterial = { },
                                                onLogout = {
                                                    mainViewModel.navigateTo(AppRoute.Login)
                                                }
                                            )
                                        }

                                        is AppRoute.CourseForm -> {
                                            val uiState by courseFormViewModel.uiState.collectAsState()
                                            val categories by courseFormViewModel.categories.collectAsState()

                                            LaunchedEffect(Unit) {
                                                courseFormViewModel.fetchCategories()
                                            }

                                            LaunchedEffect(uiState) {
                                                if (uiState is CourseFormState.Success) {
                                                    courseFormViewModel.resetState()
                                                    mainViewModel.navigateTo(AppRoute.InstructorPortal(route.session))
                                                }
                                            }

                                            SkillforgeCourseFormScreen(
                                                categories = categories,
                                                isEditMode = route.courseId != null,
                                                isLoading = uiState is CourseFormState.Loading,
                                                errorMessage = if (uiState is CourseFormState.Error) (uiState as CourseFormState.Error).message else null,
                                                uiState = uiState,
                                                onNavigateBack = {
                                                    courseFormViewModel.resetState()
                                                    mainViewModel.navigateTo(AppRoute.InstructorPortal(route.session))
                                                },
                                                onSaveClick = { title, summary, price, categoryId ->
                                                    val myToken = route.session.accessToken
                                                    courseFormViewModel.createCourse(myToken, title, summary, price, categoryId)
                                                }
                                            )
                                        }

                                        is AppRoute.MaterialUpload -> {
                                            val uploadViewModel: MaterialUploadViewModel = viewModel(
                                                factory = MaterialUploadViewModelFactory(appContainer.materialRepository)
                                            )

                                            val uploadState by uploadViewModel.uploadState.collectAsState()

                                            LaunchedEffect(uploadState) {
                                                if (uploadState is UploadState.Success) {
                                                    Toast.makeText(this@MainActivity, "Upload successful!", Toast.LENGTH_SHORT).show()
                                                    uploadViewModel.resetState()
                                                    mainViewModel.navigateTo(AppRoute.InstructorPortal(route.session))
                                                }
                                            }

                                            SkillforgeMaterialUploadScreen(
                                                lessonId = route.lessonId,
                                                isLoading = uploadState is UploadState.Loading,
                                                onNavigateBack = {
                                                    mainViewModel.navigateTo(AppRoute.InstructorPortal(route.session))
                                                },
                                                onUploadClick = { type, fileUri ->
                                                    if (fileUri != null) {
                                                        uploadViewModel.uploadFile(
                                                            context = this@MainActivity,
                                                            token = route.session.accessToken,
                                                            lessonId = route.lessonId,
                                                            type = type,
                                                            uri = fileUri
                                                        )
                                                    }
                                                }
                                            )
                                        }

                                        is AppRoute.CourseManager -> {
                                            val managerViewModel: CourseManagerViewModel = viewModel(
                                                factory = CourseManagerViewModelFactory(
                                                    appContainer.courseRepository,
                                                    appContainer.chapterRepository,
                                                    appContainer.lessonRepository
                                                )
                                            )

                                            LaunchedEffect(route.session.accessToken, route.courseId) {
                                                managerViewModel.loadCourseStructure(route.session.accessToken, route.courseId)
                                            }

                                            SkillforgeCourseManagerScreen(
                                                courseId = route.courseId,
                                                viewModel = managerViewModel,
                                                token = route.session.accessToken,
                                                onBack = {
                                                    mainViewModel.navigateTo(AppRoute.InstructorPortal(route.session))
                                                },
                                                onNavigateToUpload = { lessonId ->
                                                    mainViewModel.navigateTo(AppRoute.MaterialUpload(route.session, lessonId))
                                                },
                                                onNavigateToQuizBuilder = { courseId, chapterId ->
                                                    mainViewModel.navigateTo(AppRoute.QuizBuilder(route.session, courseId, chapterId))
                                                }
                                            )
                                        }

                                        is AppRoute.Favorite -> FavoriteRoute(
                                            session = route.session,
                                            viewModel = favoriteViewModel,
                                            onBackClick = {
                                                mainViewModel.navigateTo(AppRoute.StudentCourseListing(route.session))
                                            },
                                            onCourseClick = { courseId ->
                                                mainViewModel.navigateTo(AppRoute.StudentCourseDetails(route.session, courseId))
                                            },
                                            onNavigateToDiscovery = {
                                                mainViewModel.navigateTo(AppRoute.StudentCourseListing(route.session))
                                            },

                                        )

                                        is AppRoute.MyCourses -> {
                                            val homeViewModel: HomeViewModel = viewModel(
                                                factory = HomeViewModelFactory(appContainer.progressRepository)
                                            )
                                            val courses by homeViewModel.uiState.collectAsState()

                                            LaunchedEffect(route.session.accessToken) {
                                                homeViewModel.fetchDashboard(route.session.accessToken)
                                            }

                                            MyCoursesScreen(
                                                token = route.session.accessToken,
                                                onNavigateBack = {
                                                    mainViewModel.navigateTo(AppRoute.StudentCourseListing(route.session))
                                                },
                                                onCourseClick = { courseId ->
                                                    mainViewModel.navigateTo(AppRoute.CourseCurriculum(route.session, courseId))
                                                },

                                            )
                                        }

                                        is AppRoute.Profile -> StudentProfileScreen(
                                            session = route.session,
                                            onLogout = {
                                                mainViewModel.navigateTo(AppRoute.Login)
                                            }
                                        )

                                        else -> {}
                                    }
                                }
                            }
                    }
                }

            }
        }
    }
}