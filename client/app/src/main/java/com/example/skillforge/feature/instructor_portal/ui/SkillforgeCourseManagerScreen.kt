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
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skillforge.core.designsystem.PrimaryOrange
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
    onNavigateToUpload: (lessonId: String) -> Unit,
    onNavigateToQuizBuilder: (courseId: String, chapterId: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    var showAddChapterDialog by remember { mutableStateOf(false) }
    var showAddLessonDialogForChapter by remember { mutableStateOf<String?>(null) }
    var newItemTitle by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadCourseStructure(token, courseId)
    }

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
                }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showAddChapterDialog = false; newItemTitle = "" }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)) { Text("Cancel") }
            }
        )
    }

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
                }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)) { Text("Create") }
            },
            dismissButton = {
                TextButton(onClick = { showAddLessonDialogForChapter = null; newItemTitle = "" }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)) { Text("Cancel") }
            }
        )
    }

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
            ExtendedFloatingActionButton(
                onClick = { showAddChapterDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Chapter") },
                text = { Text("Add Chapter") },
                containerColor = PrimaryOrange,
                contentColor = Color.White
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = uiState) {
                is CourseManagerState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryOrange)
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
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        item {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Text(course.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                                Text(course.category?.name ?: "No category", color = PrimaryOrange, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        items(course.chapters) { chapter ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().background(PrimaryOrange.copy(alpha = 0.1f)).padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Chapter ${chapter.orderIndex + 1}: ${chapter.title}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryOrange
                                        )
                                    }

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

                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Add Lesson Button
                                        TextButton(
                                            onClick = { showAddLessonDialogForChapter = chapter.id },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.textButtonColors(contentColor = PrimaryOrange)
                                        ) {
                                            Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text("Add Lesson", fontWeight = FontWeight.Bold)
                                        }
                                        
                                        // Add Quiz Button - Updated to match Add Lesson style
                                        TextButton(
                                            onClick = { onNavigateToQuizBuilder(courseId, chapter.id) },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.textButtonColors(contentColor = PrimaryOrange)
                                        ) {
                                            Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text("Add Quiz", fontWeight = FontWeight.Bold)
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
            Text(
                text = if (lesson.materials.isEmpty()) "No materials" else "${lesson.materials.size} materials",
                style = MaterialTheme.typography.bodySmall,
                color = if (lesson.materials.isEmpty()) MaterialTheme.colorScheme.error else PrimaryOrange
            )
        }

        IconButton(
            onClick = onAddMaterial,
            colors = IconButtonDefaults.iconButtonColors(contentColor = PrimaryOrange)
        ) {
            Icon(Icons.Default.Attachment, contentDescription = "Add material")
        }
    }
}
