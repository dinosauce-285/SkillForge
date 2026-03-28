package com.example.skillforge

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.skillforge.core.designsystem.SkillforgeTheme
import com.example.skillforge.feature.auth.ui.LoginScreen
import com.example.skillforge.feature.auth.ui.RegisterScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import com.example.skillforge.feature.auth.viewmodel.LoginViewModel
import com.example.skillforge.feature.auth.viewmodel.LoginViewModelFactory
import com.example.skillforge.feature.auth.viewmodel.RegisterViewModel
import com.example.skillforge.feature.auth.viewmodel.RegisterViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkillforgeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1. Tạo chiếc Xe (NavController)
                    val navController = rememberNavController()

                    // 2. Vẽ Bản đồ (NavHost) với trạm xuất phát là "login"
                    NavHost(navController = navController, startDestination = "login") {

                        // Trạm 1: Màn hình Đăng nhập
                        composable("login") {
                            // 1. Lấy AppContainer ra từ Application
                            val context = LocalContext.current
                            val appContainer =
                                (context.applicationContext as SkillforgeApplication).container

                            // 2. Nhờ Factory tạo ra LoginViewModel
                            val loginViewModel: LoginViewModel = viewModel(
                                factory = LoginViewModelFactory(appContainer.loginUseCase)
                            )

                            // 3. Gắn ViewModel vào Screen
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = {
                                    // Tạm thời báo Toast, lát nữa làm chuyển màn hình Học viên sau
                                    Toast.makeText(
                                        context,
                                        "Đăng nhập thành công!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onNavigateToRegister = {
                                    navController.navigate("register")
                                }
                            )
                        }

                        // Trạm 2: Màn hình Đăng ký
                        composable("register") {
                            val context = LocalContext.current
                            val appContainer = (context.applicationContext as SkillforgeApplication).container

                            // 2. Nhờ Factory tạo RegisterViewModel
                            val registerViewModel: RegisterViewModel = viewModel(
                                factory = RegisterViewModelFactory(appContainer.registerUseCase)
                            )

                            RegisterScreen(
                                viewModel = registerViewModel,
                                onRegisterSuccess = { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    navController.popBackStack()
                                },
                                onBackToLogin = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}