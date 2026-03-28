package com.example.skillforge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skillforge.core.designsystem.SkillforgeTheme
import com.example.skillforge.domain.model.AuthSession
import com.example.skillforge.feature.auth.ui.LoginScreen
import com.example.skillforge.feature.auth.viewmodel.LoginViewModel
import com.example.skillforge.feature.auth.viewmodel.LoginViewModelFactory
import com.example.skillforge.feature.instructor_portal.ui.SkillforgeInstructorDashboardScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkillforgeTheme(dynamicColor = false) {
                val appContainer = (LocalContext.current.applicationContext as SkillforgeApplication).container
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(appContainer.loginUseCase)
                )
                var authSession by remember { mutableStateOf<AuthSession?>(null) }

                Surface(modifier = Modifier.fillMaxSize()) {
                    when (val session = authSession) {
                        null -> LoginScreen(
                            viewModel = loginViewModel,
                            onLoginSuccess = { authSession = it }
                        )
                        else -> {
                            if (session.user.role.equals("STUDENT", ignoreCase = true)) {
                                Text(
                                    text = "Đăng nhập thành công: ${session.user.fullName} (${session.user.role})",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            } else {
                                SkillforgeInstructorDashboardScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}
