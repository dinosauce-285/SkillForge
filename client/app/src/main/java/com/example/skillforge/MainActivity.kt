package com.example.skillforge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
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
import com.example.skillforge.feature.auth.viewmodel.LoginViewModel
import com.example.skillforge.feature.auth.viewmodel.LoginViewModelFactory
import com.example.skillforge.feature.instructor_portal.ui.SkillforgeInstructorDashboardScreen
import com.example.skillforge.feature.student_courses.ui.StudentCourseListingScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkillforgeTheme(dynamicColor = false) {
                val appContainer = (LocalContext.current.applicationContext as SkillforgeApplication).container
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(appContainer.loginUseCase)
                )
                var currentRoute by remember { mutableStateOf<AppRoute>(AppRoute.Login) }

                Surface(modifier = Modifier.fillMaxSize()) {
                    when (val route = currentRoute) {
                        AppRoute.Login -> LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = { session ->
                                currentRoute = if (session.user.role.equals("STUDENT", ignoreCase = true)) {
                                    AppRoute.StudentCourseListing(session)
                                } else {
                                    AppRoute.InstructorPortal(session)
                                }
                            }
                        )
                        is AppRoute.StudentCourseListing -> StudentCourseListingScreen(
                            session = route.session,
                            onLogout = { currentRoute = AppRoute.Login }
                        )
                        is AppRoute.InstructorPortal -> SkillforgeInstructorDashboardScreen()
                    }
                }
            }
        }
    }
}
