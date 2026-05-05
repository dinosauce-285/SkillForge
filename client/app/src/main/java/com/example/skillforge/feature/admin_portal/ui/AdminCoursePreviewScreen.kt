package com.example.skillforge.feature.admin_portal.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skillforge.feature.admin_portal.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCoursePreviewScreen(
    token: String,
    courseId: String,
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val courseStructure by viewModel.coursePreview.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedLevel by remember { mutableStateOf<String?>(null) }
    var showLevelDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(courseId) {
        viewModel.fetchCoursePreview(token, courseId)
    }

    LaunchedEffect(courseStructure) {
        if (selectedLevel == null && courseStructure != null) {
            selectedLevel = courseStructure?.course?.level
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Course Preview") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                !error.isNullOrEmpty() -> {
                    Text(
                        "Error: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                courseStructure != null -> {
                    val structure = courseStructure!!
                    val course = structure.course

                    if (course == null) {
                        Text("Course data unavailable.", modifier = Modifier.align(Alignment.Center))
                    } else {
                        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            // Title & Instructor
                            Text(
                                course.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Instructor: ${course.instructor?.fullName ?: "Unknown"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "Category: ${course.category?.name ?: "Unknown"}",
                                style = MaterialTheme.typography.bodySmall
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Level override
                            Text("Override Difficulty Level:", style = MaterialTheme.typography.titleMedium)
                            Box {
                                OutlinedButton(onClick = { showLevelDropdown = true }) {
                                    Text(selectedLevel ?: "Select Level")
                                }
                                DropdownMenu(
                                    expanded = showLevelDropdown,
                                    onDismissRequest = { showLevelDropdown = false }
                                ) {
                                    listOf("BEGINNER", "INTERMEDIATE", "ADVANCED", "ALL_LEVELS").forEach { level ->
                                        DropdownMenuItem(
                                            text = { Text(level) },
                                            onClick = {
                                                selectedLevel = level
                                                showLevelDropdown = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Chapters & Content:", style = MaterialTheme.typography.titleMedium)

                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(structure.chapters ?: emptyList()) { chapter ->
                                    Card(modifier = Modifier.fillMaxWidth()) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(chapter.title, fontWeight = FontWeight.Bold)
                                            chapter.lessons?.forEach { lesson ->
                                                Text("  - ${lesson.title}")
                                            }
                                            chapter.quizzes?.forEach { quiz ->
                                                Text("Quiz: ${quiz.title}")
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Action buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.moderateCourse(token, courseId, "DRAFT", selectedLevel)
                                        onBack()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                ) {
                                    Text("Request\nChanges", style = MaterialTheme.typography.labelSmall)
                                }
                                Button(
                                    onClick = {
                                        viewModel.moderateCourse(token, courseId, "REJECTED", selectedLevel)
                                        onBack()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) {
                                    Text("Reject")
                                }
                                Button(
                                    onClick = {
                                        viewModel.moderateCourse(token, courseId, "PUBLISHED", selectedLevel)
                                        onBack()
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                                ) {
                                    Text("Publish")
                                }
                            }
                        }
                    }
                }
                else -> {
                    Text("Loading course data...", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}
