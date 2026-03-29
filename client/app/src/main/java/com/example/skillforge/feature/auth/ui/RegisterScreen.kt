package com.example.skillforge.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skillforge.core.designsystem.PrimaryOrangeLight
import com.example.skillforge.core.designsystem.TextFieldBackgroundColor
import com.example.skillforge.feature.auth.viewmodel.RegisterState
import com.example.skillforge.feature.auth.viewmodel.RegisterViewModel

// Nhớ import RegisterViewModel và RegisterState của bạn vào đây
// import com.example.skillforge.feature.auth.viewmodel.RegisterViewModel
// import com.example.skillforge.feature.auth.viewmodel.RegisterState

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: (String) -> Unit, // Callback trả về câu message thành công
    onBackToLogin: () -> Unit // Callback để quay lại màn hình Login
) {
    // 1. Biến lưu trạng thái các ô nhập liệu (Thêm biến fullName)
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // 2. Lắng nghe trạng thái từ ViewModel (Tạm comment để không báo lỗi build)
    val registerState by viewModel.registerState.collectAsState()

    // Padding mặc định đồng bộ với Login
    val defaultPadding = 16.dp
    val cardPadding = 24.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Dùng màu nền từ Theme
            .padding(defaultPadding),
        contentAlignment = Alignment.Center
    ) {
        // Thẻ trắng chính bo góc lớn, đổ bóng (Giống hệt Login)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 10.dp, shape = RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(cardPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Logo & Tên App (Đồng bộ với Login)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                MaterialTheme.colorScheme.primary, // Màu cam chính
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎓", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "LMS Learning",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 2. Tiêu đề chính (Đổi chữ)
                Text(
                    text = "Create Your\nLearning Account",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 36.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Enter your details to sign up",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )

                // -------------------------------------------------------------------
                // 3. CÁC Ô NHẬP LIỆU (Đồng bộ style màu sắc, bo góc với Login)
                // -------------------------------------------------------------------

                // Ô nhập Họ và Tên (MỚI)
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    placeholder = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = TextFieldBackgroundColor, // Màu custom
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words) // Tự viết hoa chữ cái đầu
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Ô nhập Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = TextFieldBackgroundColor,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Ô nhập Password với icon con mắt
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = TextFieldBackgroundColor,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                // 4. Hiển thị lỗi nếu có (Xóa cái Forgot Password đi)
                Spacer(modifier = Modifier.height(24.dp))
                 if (registerState is RegisterState.Error) {
                     Text(
                         text = (registerState as RegisterState.Error).message,
                         color = MaterialTheme.colorScheme.error,
                         style = MaterialTheme.typography.bodySmall,
                         modifier = Modifier.padding(bottom = 8.dp)
                     )
                 }

                // 5. Nút Đăng ký (Đổi chữ thành Sign Up)
                Button(
                    enabled = registerState !is RegisterState.Loading,
                    onClick = {
                        viewModel.register(fullName, email, password) // GỌI HÀM REGISTER
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                     if (registerState is RegisterState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                     } else {
                    Text("Sign Up", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
                     }
                }

                // (Tùy chọn: Xóa phần Or continue with Google/Facebook ở màn hình Đăng ký cho gọn)

                Spacer(modifier = Modifier.height(cardPadding))

                // 6. Nút chuyển về Đăng nhập (Đổi chữ)
                Row {
                    Text(
                        "Already have an account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = onBackToLogin, contentPadding = PaddingValues(0.dp), modifier = Modifier.height(20.dp)) {
                        Text(
                            "Login",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimaryOrangeLight // Màu cam nhạt
                        )
                    }
                }
            }
        }
    }

    // Xử lý khi thành công (Đồng bộ luồng logic)
    LaunchedEffect(registerState) {
        when (val state = registerState) {
            is RegisterState.Success -> {
                onRegisterSuccess(state.message)
            }
            else -> { /* Không làm gì cả nếu là Idle, Loading hoặc Error */ }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun RegisterScreenPreview() {
//    // Fake callback để xem preview
//    RegisterScreen(onRegisterSuccess = {}, onBackToLogin = {})
//}