package com.example.skillforge // Đổi tên package này cho đúng với dòng đầu tiên trong file cũ của bạn nhé!

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.skillforge.feature.instructor_portal.ui.SkillforgeCourseFormScreen

// CẢNH BÁO: Dòng import dưới đây có thể cần chỉnh lại đường dẫn
// cho khớp với nơi bạn lưu file SkillforgeInstructorDashboardScreen.kt
import com.example.skillforge.feature.instructor_portal.ui.SkillforgeInstructorDashboardScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Gọi hàm giao diện của bạn ra đây!
                    SkillforgeCourseFormScreen()
                }
            }
        }
    }
}