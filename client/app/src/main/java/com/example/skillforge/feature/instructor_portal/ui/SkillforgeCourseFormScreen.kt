package com.example.skillforge.feature.instructor_portal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillforgeCourseFormScreen(
    isEditMode: Boolean = false,
    onNavigateBack: () -> Unit = {},
    // Trả dữ liệu ra ngoài khi bấm Save (Tạm thời là các trường cơ bản)
    onSaveClick: (title: String, description: String, price: String, category: String) -> Unit = { _, _, _, _ -> }
) {
    var courseTitle by remember { mutableStateOf("") }
    var courseDescription by remember { mutableStateOf("") }
    var coursePrice by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Development") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Course" else "Create New Course",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
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
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onSaveClick(courseTitle, courseDescription, coursePrice, selectedCategory) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
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
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Cover Image Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(2.dp, MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                    .clickable { /* Mở thư viện ảnh */ },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Upload cover",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Upload Course Cover", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                    Text("16:9 ratio recommended", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // 2. Title
            OutlinedTextField(
                value = courseTitle,
                onValueChange = { courseTitle = it },
                label = { Text("Course Title") },
                placeholder = { Text("e.g. Advanced UI/UX Design") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            // 3. Description
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
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            // 4. Price
            OutlinedTextField(
                value = coursePrice,
                onValueChange = { coursePrice = it },
                label = { Text("Price") },
                placeholder = { Text("0.00") },
                leadingIcon = {
                    Text(
                        text = "$",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            // 5. Category (Dùng LazyRow không crash)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val categories = listOf("Design", "Development", "Business", "Marketing", "Photography")

                LazyRow(
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
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.primary,
                                labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SkillforgeCourseFormPreview() {
    MaterialTheme {
        SkillforgeCourseFormScreen()
    }
}