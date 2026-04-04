package com.example.skillforge

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skillforge.core.designsystem.SkillforgeTheme
import com.example.skillforge.core.navigation.AppRoute
import com.example.skillforge.feature.auth.ui.LoginScreen
import com.example.skillforge.feature.auth.ui.RegisterScreen
import com.example.skillforge.feature.auth.viewmodel.LoginViewModel
import com.example.skillforge.feature.auth.viewmodel.LoginViewModelFactory
import com.example.skillforge.feature.auth.viewmodel.RegisterViewModel
import com.example.skillforge.feature.auth.viewmodel.RegisterViewModelFactory
import com.example.skillforge.feature.favorite.ui.FavoriteRoute
import com.example.skillforge.feature.favorite.viewmodel.FavoriteViewModel
import com.example.skillforge.feature.favorite.viewmodel.FavoriteViewModelFactory
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
import com.example.skillforge.feature.student_courses.ui.StudentCourseDetailsRoute
import com.example.skillforge.feature.student_courses.ui.StudentCourseListingRoute
import com.example.skillforge.feature.student_courses.viewmodel.StudentCoursesViewModel
import com.example.skillforge.feature.student_courses.viewmodel.StudentCoursesViewModelFactory
import com.example.skillforge.feature.transaction.ui.TransactionRoute
import com.example.skillforge.feature.transaction.viewmodel.TransactionViewModel
import com.example.skillforge.feature.transaction.viewmodel.TransactionViewModelFactory

// translated comment
import com.example.skillforge.domain.model.Category
import com.example.skillforge.feature.home.ui.HomeScreen
import com.example.skillforge.feature.student_courses.ui.MyCoursesScreen
import com.example.skillforge.feature.student_courses.ui.LessonLearningScreen
import com.example.skillforge.feature.student_courses.ui.StudentProfileScreen

// translated comment
import io.github.jan.supabase.auth.handleDeeplinks

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkillforgeTheme(dynamicColor = false) {
                val appContainer = (LocalContext.current.applicationContext as SkillforgeApplication).container
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
                        appContainer.lessonRepository
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
                val transactionViewModel: TransactionViewModel = viewModel(
                    factory = TransactionViewModelFactory(
                        appContainer.courseRepository,
                        appContainer.orderRepository,
                    )
                )
                var currentRoute by remember { mutableStateOf<AppRoute>(AppRoute.Login) }

                Surface(modifier = Modifier.fillMaxSize()) {

                    // ===================================================================
                    // translated comment
                    val isTestingHome = false
                    // ===================================================================

                    if (isTestingHome) {
                        // translated comment
                        var showMyCourses by remember { mutableStateOf(false) }
                        var showLesson by remember { mutableStateOf(false) }

                        if (showLesson) {
                            LessonLearningScreen(
                                sessionToken = "preview-token",
                                lessonId = "preview-lesson",
                                viewModel = studentCoursesViewModel,
                                onNavigateBack = { showLesson = false }
                            )
                        } else if (showMyCourses) {
                            MyCoursesScreen(
                                onNavigateBack = { showMyCourses = false },
                                onCourseClick = { courseId ->
                                    showLesson = true
                                },
                                onNavigateToDiscover = { showMyCourses = false },
                                onNavigateToWishlist = {},
                                onNavigateToProfile = {},
                            )
                        } else {
                            HomeScreen(
                                onNavigateToMyCourses = { showMyCourses = true }
                            )
                        }
                    } else {
                        // translated comment
                        when (val route = currentRoute) {
                            AppRoute.Login -> LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = { session ->
                                    currentRoute = if (session.user.role.equals("STUDENT", ignoreCase = true)) {
                                        AppRoute.StudentCourseListing(session)
                                    } else {
                                        AppRoute.InstructorPortal(session)
                                    }
                                },
                                onNavigateToRegister = {
                                    currentRoute = AppRoute.Register
                                }
                            )

                            AppRoute.Register -> RegisterScreen(
                                viewModel = registerViewModel,
                                onRegisterSuccess = {
                                    currentRoute = AppRoute.Login
                                },
                                onBackToLogin = {
                                    currentRoute = AppRoute.Login
                                }
                            )

                            is AppRoute.StudentCourseListing -> StudentCourseListingRoute(
                                session = route.session,
                                viewModel = studentCoursesViewModel,
                                onCourseSelected = { courseId ->
                                    currentRoute = AppRoute.StudentCourseDetails(
                                        session = route.session,
                                        courseId = courseId,
                                    )
                                },
                                onNavigateToFavorites = {
                                    currentRoute = AppRoute.Favorite(route.session)
                                },
                                onNavigateToLearning = {
                                    currentRoute = AppRoute.MyCourses(route.session)
                                },
                                onNavigateToProfile = {
                                    currentRoute = AppRoute.Profile(route.session)
                                },
                                onLogout = { currentRoute = AppRoute.Login }
                            )

                            is AppRoute.StudentCourseDetails -> StudentCourseDetailsRoute(
                                courseId = route.courseId,
                                viewModel = studentCoursesViewModel,
                                onLessonSelected = { lessonId ->
                                    currentRoute = AppRoute.LessonLearning(route.session, lessonId)
                                },
                                onCheckoutSelected = { courseId ->
                                    currentRoute = AppRoute.Checkout(route.session, courseId)
                                },
                                onBack = {
                                    currentRoute = AppRoute.StudentCourseListing(route.session)
                                }
                            )

                            is AppRoute.LessonLearning -> LessonLearningScreen(
                                sessionToken = route.session.accessToken,
                                lessonId = route.lessonId,
                                viewModel = studentCoursesViewModel,
                                onNavigateBack = {
                                    currentRoute = AppRoute.StudentCourseListing(route.session)
                                }
                            )

                            is AppRoute.Checkout -> TransactionRoute(
                                sessionToken = route.session.accessToken,
                                courseId = route.courseId,
                                viewModel = transactionViewModel,
                                onBackClick = {
                                    currentRoute = AppRoute.StudentCourseDetails(route.session, route.courseId)
                                }
                            )

                            is AppRoute.InstructorPortal -> {
                                val portalViewModel: InstructorPortalViewModel = viewModel(
                                    factory = InstructorPortalViewModelFactory(appContainer.courseRepository)
                                )

                                val courses by portalViewModel.courses.collectAsState()
                                val isLoading by portalViewModel.isLoading.collectAsState()

                                LaunchedEffect(Unit) {
                                    portalViewModel.fetchMyCourses(route.session.accessToken)
                                }

                                SkillforgeInstructorDashboardScreen(
                                    courses = courses,
                                    isLoading = isLoading,
                                    onNavigateToCreateCourse = {
                                        currentRoute = AppRoute.CourseForm(route.session)
                                    },
                                    onCourseClick = { clickedCourseId ->
                                        currentRoute = AppRoute.CourseManager(route.session, clickedCourseId)
                                    },
                                    onNavigateToUploadMaterial = {
                                        // translated comment
                                    },
                                    onLogout = {
                                        currentRoute = AppRoute.Login
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
                                        currentRoute = AppRoute.InstructorPortal(route.session)
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
                                        currentRoute = AppRoute.InstructorPortal(route.session)
                                    },
                                    onSaveClick = { title, summary, price, categoryId ->
                                        val myToken = route.session.accessToken
                                        courseFormViewModel.createCourse(myToken, title, summary, price, categoryId)
                                    }
                                )
                            }

                            is AppRoute.MaterialUpload -> {
                                SkillforgeMaterialUploadScreen(
                                    courseId = route.courseId,
                                    onNavigateBack = {
                                        currentRoute = AppRoute.InstructorPortal(route.session)
                                    },
                                    onUploadClick = { title, type, fileUri ->
                                        println("Uploading file: $title, Type: $type")
                                        currentRoute = AppRoute.InstructorPortal(route.session)
                                    }
                                )
                            }

                            is AppRoute.Favorite -> FavoriteRoute(
                                session = route.session,
                                viewModel = favoriteViewModel,
                                onBackClick = {
                                    currentRoute = AppRoute.StudentCourseListing(route.session)
                                },
                                onCourseClick = { courseId ->
                                    currentRoute = AppRoute.StudentCourseDetails(route.session, courseId)
                                },
                                onNavigateToDiscovery = {
                                    currentRoute = AppRoute.StudentCourseListing(route.session)
                                },
                                onNavigateToLearning = {
                                    currentRoute = AppRoute.MyCourses(route.session)
                                },
                                onNavigateToProfile = {
                                    currentRoute = AppRoute.Profile(route.session)
                                }
                            )

                            is AppRoute.MyCourses -> MyCoursesScreen(
                                onNavigateBack = {
                                    currentRoute = AppRoute.StudentCourseListing(route.session)
                                },
                                onCourseClick = {
                                    currentRoute = AppRoute.StudentCourseListing(route.session)
                                },
                                onNavigateToDiscover = {
                                    currentRoute = AppRoute.StudentCourseListing(route.session)
                                },
                                onNavigateToWishlist = {
                                    currentRoute = AppRoute.Favorite(route.session)
                                },
                                onNavigateToProfile = {
                                    currentRoute = AppRoute.Profile(route.session)
                                }
                            )

                            is AppRoute.Profile -> StudentProfileScreen(
                                session = route.session,
                                onNavigateToDiscover = {
                                    currentRoute = AppRoute.StudentCourseListing(route.session)
                                },
                                onNavigateToLearning = {
                                    currentRoute = AppRoute.MyCourses(route.session)
                                },
                                onNavigateToWishlist = {
                                    currentRoute = AppRoute.Favorite(route.session)
                                },
                                onLogout = {
                                    currentRoute = AppRoute.Login
                                }
                            )

                            is AppRoute.CourseManager -> {
                                val managerViewModel: CourseManagerViewModel = viewModel(
                                    factory = CourseManagerViewModelFactory(
                                        appContainer.courseRepository,
                                        appContainer.chapterRepository,
                                        appContainer.lessonRepository
                                    )
                                )

                                SkillforgeCourseManagerScreen(
                                    courseId = route.courseId,
                                    viewModel = managerViewModel,
                                    token = route.session.accessToken,
                                    onBack = {
                                        currentRoute = AppRoute.InstructorPortal(route.session)
                                    },
                                    onNavigateToUpload = { lessonId ->
                                        currentRoute = AppRoute.MaterialUpload(route.session, lessonId)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val appContainer = (applicationContext as SkillforgeApplication).container
        appContainer.supabase.handleDeeplinks(intent)
    }
}
