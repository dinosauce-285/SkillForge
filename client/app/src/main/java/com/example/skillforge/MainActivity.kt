package com.example.skillforge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

// Import giao diện
import com.example.skillforge.core.designsystem.SkillforgeTheme
import com.example.skillforge.feature.auth.ui.LoginScreen

// Import ViewModel và Factory
import com.example.skillforge.feature.auth.viewmodel.LoginViewModel
import com.example.skillforge.feature.auth.viewmodel.LoginViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkillforgeTheme(dynamicColor = false) {
                // Lấy AppContainer từ Application
                val appContainer = (LocalContext.current.applicationContext as SkillforgeApplication).container

                // Khởi tạo ViewModel thông qua Factory
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(appContainer.loginUseCase)
                )

                Surface(modifier = Modifier.fillMaxSize()) {
                    LoginScreen(
                        viewModel = loginViewModel,
                        onLoginSuccess = {
                            // TODO: Viết code chuyển sang màn hình chính (HomeScreen) ở đây
                            println("Đăng nhập thành công, chuyển màn hình!")
                        }
                    )
                }
            }
        }
    }
}