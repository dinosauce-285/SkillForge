package com.example.skillforge.feature.instructor_portal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skillforge.data.remote.LessonDto
import com.example.skillforge.feature.instructor_portal.viewmodel.CourseManagerState
import com.example.skillforge.feature.instructor_portal.viewmodel.CourseManagerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillforgeCourseManagerScreen(
    courseId: String,
    viewModel: CourseManagerViewModel,
    token: String,
    onBack: () -> Unit,
    onNavigateToUpload: (lessonId: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // translated comment
    var showAddChapterDialog by remember { mutableStateOf(false) }

    // translated comment
    var showAddLessonDialogForChapter by remember { mutableStateOf<String?>(null) }
    var newItemTitle by remember { mutableStateOf("") }

    // translated comment
    LaunchedEffect(Unit) {
        viewModel.loadCourseStructure(token, courseId)
    }

    // translated comment
    if (showAddChapterDialog) {
        AlertDialog(
            onDismissRequest = { showAddChapterDialog = false; newItemTitle = "" },
            title = { Text("Add New Chapter") },
            text = {
                OutlinedTextField(
                    value = newItemTitle,
                    onValueChange = { newItemTitle = it },
                    label = { Text("Chapter title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.createChapter(newItemTitle)
                    showAddChapterDialog = false
                    newItemTitle = ""
                }) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showAddChapterDialog = false; newItemTitle = "" }) { Text("Cancel") }
            }
        )
    }

    // translated comment
    if (showAddLessonDialogForChapter != null) {
        AlertDialog(
            onDismissRequest = { showAddLessonDialogForChapter = null; newItemTitle = "" },
            title = { Text("Add New Lesson") },
            text = {
                OutlinedTextField(
                    value = newItemTitle,
                    onValueChange = { newItemTitle = it },
                    label = { Text("Lesson title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    showAddLessonDialogForChapter?.let { chapterId ->
                        viewModel.createLesson(chapterId, newItemTitle)
                    }
                    showAddLessonDialogForChapter = null
                    newItemTitle = ""
                }) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showAddLessonDialogForChapter = null; newItemTitle = "" }) { Text("Cancel") }
            }
        )
    }

    // translated comment
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Content Management", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            // translated comment
            ExtendedFloatingActionButton(
                onClick = { showAddChapterDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Chapter") },
                text = { Text("Add Chapter") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = uiState) {
                is CourseManagerState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is CourseManagerState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                is CourseManagerState.Success -> {
                    val course = state.course

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp) // translated comment
                    ) {
                        // translated comment
                        item {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Text(course.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                                Text(course.category?.name ?: "No category", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        // translated comment
                        items(course.chapters) { chapter ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column {
                                    // translated comment
                                    Row(
                                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer).padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Chapter ${chapter.orderIndex + 1}: ${chapter.title}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }

                                    // translated comment
                                    if (chapter.lessons.isEmpty()) {
                                        Text("No lessons yet.", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodySmall)
                                    } else {
                                        chapter.lessons.forEach { lesson ->
                                            LessonItemRow(
                                                lesson = lesson,
                                                onAddMaterial = { onNavigateToUpload(lesson.id) }
                                            )
                                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                        }
                                    }

                                    // translated comment
                                    TextButton(
                                        onClick = { showAddLessonDialogForChapter = chapter.id },
                                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                                    ) {
                                        Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Add Lesson")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LessonItemRow(
    lesson: LessonDto,
    onAddMaterial: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Lesson ${lesson.orderIndex + 1}: ${lesson.title}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            // translated comment
            Text(
                text = if (lesson.materials.isEmpty()) "No materials" else "${lesson.materials.size} materials",
                style = MaterialTheme.typography.bodySmall,
                color = if (lesson.materials.isEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }

        IconButton(
            onClick = onAddMaterial,
            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Attachment, contentDescription = "Add material")
        }
    }
}
