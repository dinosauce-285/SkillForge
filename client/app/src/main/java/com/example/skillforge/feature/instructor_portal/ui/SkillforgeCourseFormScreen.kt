package com.example.skillforge.feature.instructor_portal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add // Đã đổi sang Icon Add cơ bản
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val SkillForgePrimary = Color(0xFFD84B1E)
private val SkillForgePrimaryContainer = Color(0xFFFFEAD8)
private val SkillForgeSurfaceVariant = Color(0xFFF0F0F0)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SkillforgeCourseFormScreen(
    isEditMode: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onSaveClick: () -> Unit = {}
) {
    var courseTitle by remember { mutableStateOf("") }
    var courseDescription by remember { mutableStateOf("") }
    var coursePrice by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Design") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Course" else "Create New Course",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = onSaveClick,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = SkillForgePrimary)
                    ) {
                        Text(if (isEditMode) "Save Changes" else "Publish Course")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SkillForgeSurfaceVariant)
                    .border(2.dp, SkillForgePrimaryContainer, RoundedCornerShape(12.dp))
                    .clickable { /* Mở thư viện ảnh */ },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Đã thay AddPhotoAlternate bằng Add (có sẵn trong Core)
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Upload cover",
                        modifier = Modifier.size(48.dp),
                        tint = SkillForgePrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Upload Course Cover", color = SkillForgePrimary, fontWeight = FontWeight.Medium)
                    Text("16:9 ratio recommended", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }

            OutlinedTextField(
                value = courseTitle,
                onValueChange = { courseTitle = it },
                label = { Text("Course Title") },
                placeholder = { Text("e.g. Advanced UI/UX Design") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SkillForgePrimary,
                    focusedLabelColor = SkillForgePrimary
                )
            )

            OutlinedTextField(
                value = courseDescription,
                onValueChange = { courseDescription = it },
                label = { Text("Course Description") },
                placeholder = { Text("What will students learn in this course?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SkillForgePrimary,
                    focusedLabelColor = SkillForgePrimary
                )
            )

            OutlinedTextField(
                value = coursePrice,
                onValueChange = { coursePrice = it },
                label = { Text("Price") },
                placeholder = { Text("0.00") },
                // Đã thay AttachMoney bằng text "$" cho gọn và không cần import thêm icon
                leadingIcon = {
                    Text(
                        text = "$",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SkillForgePrimary,
                    focusedLabelColor = SkillForgePrimary
                )
            )

            // 5. Chọn Danh mục (Category) - Đã sửa lỗi bằng LazyRow
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                val categories = listOf("Design", "Development", "Business", "Marketing", "Data Science", "Photography")

                // Thay thế FlowRow bằng LazyRow (Hỗ trợ tốt cho mọi phiên bản Compose)
                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories.size) { index ->
                        val category = categories[index]
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SkillForgePrimaryContainer,
                                selectedLabelColor = SkillForgePrimary
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420")
@Composable
fun SkillforgeCourseFormPreview() {
    MaterialTheme {
        SkillforgeCourseFormScreen(isEditMode = false)
    }
}