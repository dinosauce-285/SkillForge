package com.example.skillforge.feature.instructor_portal.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.data.remote.LessonDto
import com.example.skillforge.feature.instructor_portal.viewmodel.CourseManagerState
import com.example.skillforge.feature.instructor_portal.viewmodel.CourseManagerViewModel
import com.example.skillforge.data.remote.LessonMaterialDto
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillforgeCourseManagerScreen(
    courseId: String,
    viewModel: CourseManagerViewModel,
    token: String,
    onBack: () -> Unit,
    onNavigateToUpload: (lessonId: String) -> Unit,
    onNavigateToQuizBuilder: (courseId: String, chapterId: String) -> Unit,
    onNavigateToEditQuiz: (courseId: String, quizId: String) -> Unit = { _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsState()

    var showAddChapterDialog by remember { mutableStateOf(false) }
    var showAddLessonDialogForChapter by remember { mutableStateOf<String?>(null) }
    var newItemTitle by remember { mutableStateOf("") }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadCourseStructure(token, courseId)
    }

    // --- DIALOGS ---
    if (showAddChapterDialog) {
        AlertDialog(
            onDismissRequest = { showAddChapterDialog = false; newItemTitle = "" },
            title = { Text("Add New Chapter", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newItemTitle,
                    onValueChange = { newItemTitle = it },
                    label = { Text("Chapter Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
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
            title = { Text("Add New Lesson", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = newItemTitle,
                    onValueChange = { newItemTitle = it },
                    label = { Text("Lesson Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
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
                title = { Text("Curriculum Manager", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddChapterDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Chapter") },
                text = { Text("Add Chapter", fontWeight = FontWeight.Bold) },
                containerColor = PrimaryOrange,
                contentColor = Color.White,
                shape = CircleShape
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                                Text(course.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = PrimaryOrange.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = course.category?.name ?: "Uncategorized",
                                        color = PrimaryOrange,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }

                        items(course.chapters) { chapter ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                            ) {
                                Column {
                                    // Chapter Header
                                    Row(
                                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)).padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(shape = CircleShape, color = PrimaryOrange, modifier = Modifier.size(28.dp)) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("${chapter.orderIndex + 1}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = chapter.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    // Lessons List
                                    if (chapter.lessons.isEmpty()) {
                                        Text(
                                            "This chapter is empty. Add a lesson to get started.",
                                            modifier = Modifier.padding(16.dp),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Gray
                                        )
                                    } else {
                                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                            chapter.lessons.forEach { lesson ->
                                                LessonItemRow(
                                                    lesson = lesson,
                                                    onAddMaterial = { onNavigateToUpload(lesson.id) },
                                                    onDeleteMaterial = { materialId ->
                                                        viewModel.deleteMaterial(token, courseId, materialId)
                                                    },

                                                    onViewMaterial = { material ->
                                                        try {

                                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(material.fileUrl))
                                                            context.startActivity(intent)
                                                        } catch (e: Exception) {
                                                            Toast.makeText(context, "Cannot open this file", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                    // ---------------------------
                                                )
                                            }
                                        }
                                    }

                                    // Quiz List
                                    if (!chapter.quizzes.isNullOrEmpty()) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                        ) {
                                            chapter.quizzes?.forEach { quiz ->
                                                Surface(
                                                    onClick = { onNavigateToEditQuiz(courseId, quiz.id) },
                                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                                    shape = RoundedCornerShape(10.dp),
                                                    color = PrimaryOrange.copy(alpha = 0.08f)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                                    ) {
                                                        Icon(Icons.Default.Quiz, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(20.dp))
                                                        Column(modifier = Modifier.weight(1f)) {
                                                            Text(
                                                                text = quiz.title ?: "Untitled Quiz",
                                                                style = MaterialTheme.typography.bodyMedium,
                                                                fontWeight = FontWeight.Bold,
                                                                color = PrimaryOrange
                                                            )
                                                            val questionCount = quiz._count?.questions ?: 0
                                                            Text(
                                                                text = "$questionCount question${if (questionCount != 1) "s" else ""}",
                                                                style = MaterialTheme.typography.bodySmall,
                                                                color = Color.Gray
                                                            )
                                                        }
                                                        Icon(Icons.Default.Edit, contentDescription = "Edit Quiz", tint = PrimaryOrange, modifier = Modifier.size(20.dp))
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    // Action Buttons
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    Row(
                                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f)).padding(8.dp),
                                    ) {
                                        TextButton(
                                            onClick = { showAddLessonDialogForChapter = chapter.id },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.textButtonColors(contentColor = PrimaryOrange)
                                        ) {
                                            Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text("Add Lesson", fontWeight = FontWeight.Bold)
                                        }

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
    onAddMaterial: () -> Unit,
    onDeleteMaterial: (String) -> Unit,
    onViewMaterial: (LessonMaterialDto) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.OndemandVideo, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = lesson.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(
                onClick = onAddMaterial,
                colors = IconButtonDefaults.iconButtonColors(contentColor = PrimaryOrange)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add material")
            }
        }

        // --- MATERIALS SUB-LIST ---
        if (lesson.materials.isEmpty()) {
            Text(
                text = "No materials uploaded yet.",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 46.dp, bottom = 8.dp)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 46.dp, end = 16.dp, bottom = 8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            ) {
                lesson.materials.forEachIndexed { index, material ->
                    MaterialItemRow(
                        material = material,
                        onDelete = { onDeleteMaterial(material.id) },
                        onClick = { onViewMaterial(material) }
                    )
                    if (index < lesson.materials.size - 1) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                    }
                }
            }
        }
    }
}

@Composable
fun MaterialItemRow(
    material: LessonMaterialDto,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Derive a display title from the URL
    val displayTitle = remember(material.fileUrl) {
        material.fileUrl.substringAfterLast("/").substringBefore("?")
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Material", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete '$displayTitle'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel", color = Color.Gray) }
            }
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth().clickable{ onClick()}.padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // FIX: Added .name to convert the Enum to a String before checking it
        val icon = when(material.type.name.uppercase()) {
            "VIDEO" -> Icons.Default.OndemandVideo
            "PDF", "DOCUMENT" -> Icons.Default.PictureAsPdf
            else -> Icons.Default.InsertDriveFile
        }

        Icon(icon, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = displayTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            // FIX: Added .name to display the Enum's string value
            Text(text = material.type.name, style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontSize = 10.sp)
        }

        IconButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
        }
    }
}

