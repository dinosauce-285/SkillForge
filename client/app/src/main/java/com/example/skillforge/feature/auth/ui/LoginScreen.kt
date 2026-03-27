package com.example.skillforge.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillforge.R // Thay bằng package R của dự án
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.core.designsystem.PrimaryOrangeLight
import com.example.skillforge.core.designsystem.SocialButtonBorderColor
import com.example.skillforge.core.designsystem.SocialButtonTextColor
import com.example.skillforge.core.designsystem.TextFieldBackgroundColor
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skillforge.feature.auth.viewmodel.LoginViewModel
import com.example.skillforge.feature.auth.viewmodel.LoginState

@Composable
fun LoginScreen(
    viewModel: LoginViewModel, // Truyền ViewModel vào đây
    onLoginSuccess: () -> Unit // Hàm callback để chuyển màn hình khi thành công
) {
    // Biến lưu trạng thái ô nhập liệu
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Lắng nghe trạng thái từ ViewModel
    val loginState by viewModel.loginState.collectAsState()

    // Padding mặc định dùng khắp nơi
    val defaultPadding = 16.dp
    val cardPadding = 24.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Dùng màu từ Theme
            .padding(defaultPadding),
        contentAlignment = Alignment.Center
    ) {
        // Cái thẻ trắng chính bo góc lớn
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
                // 1. Logo & Tên App
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                MaterialTheme.colorScheme.primary, // Lấy màu cam chính
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Icon mũ tốt nghiệp (Bạn cần tải ảnh về bỏ vào res/drawable)
                        // Icon(painter = painterResource(id = R.drawable.ic_graduation_cap), contentDescription = null, tint = Color.White)
                        // Tạm dùng text thay
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

                // 2. Tiêu đề chính
                Text(
                    text = "Welcome back to\nyour learning journey",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        lineHeight = 36.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Please enter your details to continue",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )

                // 3. Ô nhập Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = TextFieldBackgroundColor, // Dùng màu custom từ Color.kt
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 4. Ô nhập Password với icon con mắt
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

                // 5. Quên mật khẩu
                TextButton(
                    onClick = { /* Xử lý quên mật khẩu */ },
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 8.dp, bottom = 24.dp)
                ) {
                    Text(
                        text = "Forgot password?",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                        color = PrimaryOrangeLight // Dùng màu custom từ Color.kt
                    )
                }

                if (loginState is LoginState.Error) {
                    Text(
                        text = (loginState as LoginState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // 6. Nút Đăng nhập
                Button(
                    enabled = loginState !is LoginState.Loading,
                    onClick = {
                        // GỌI HÀM LOGIN Ở ĐÂY
                        viewModel.login(email, password)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                    // Mặc định Button dùng màu primary từ Theme (cam), ko cần set lại!
                ) {
                    if (loginState is LoginState.Loading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Login", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 7. Phần ngăn cách
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )
                    Text(
                        "Or continue with",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 8. Nút Social Login
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Nút Google
                    SocialLoginButton(
                        text = "Google",
                        // painter = painterResource(id = R.drawable.ic_google),
                        // Tạm dùng emojiแทน icon
                        emoji = "G",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(defaultPadding))
                    // Nút Facebook
                    SocialLoginButton(
                        text = "Facebook",
                        // painter = painterResource(id = R.drawable.ic_facebook),
                        // Tạm dùng emojiแทน icon
                        emoji = "f",
                        iconTint = Color(0xFF1877F2), // Màu xanh Facebook đặc trưng ko nên hardcode nhưng để đây cho nhanh, nên bỏ vô Colors.kt
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(defaultPadding))

                // 9. Nút Đăng ký
                Row {
                    Text(
                        "Don't have an account? ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Sign up",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimaryOrangeLight
                    )
                }
            }
        }
    }

    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            onLoginSuccess() // Ví dụ: Chuyển sang màn hình Home
        }
    }
}

// ---------------------------------------------------------
// Composable helper cho các nút Social Login để tránh lặp code
// ---------------------------------------------------------
@Composable
fun SocialLoginButton(
    text: String,
    modifier: Modifier = Modifier,
    // painter: androidx.compose.ui.graphics.painter.Painter? = null,
    emoji: String = "", // Tạm dùng emoji cho nhanh
    iconTint: Color = Color.Unspecified
) {
    Row(
        modifier = modifier
            .height(56.dp)
            .border(
                1.dp,
                SocialButtonBorderColor, // Màu custom từ Color.kt
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Icon social (Bạn cần tải ảnh về bỏ vào res/drawable)
        // if (painter != null) {
        //     Icon(painter = painter, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
        // } else if (emoji.isNotEmpty()){
        // Text emoji thay icon
        Text(emoji, color = iconTint, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        // }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            color = SocialButtonTextColor // Màu custom từ Color.kt
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun LoginScreenPreview() {
//    // Để thấy giao diện trong Android Studio, ta chỉ cần gọi hàm chính ra
//    LoginScreen()
//}
