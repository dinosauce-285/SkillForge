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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
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
import coil.compose.AsyncImage
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset

enum class ManagerTab {
    Curriculum, Students
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillforgeCourseManagerScreen(
    courseId: String,
    viewModel: CourseManagerViewModel,
    token: String,
    onBack: () -> Unit,
    onNavigateToUpload: (lessonId: String) -> Unit,
    onNavigateToQuizBuilder: (courseId: String, chapterId: String, quizId: String?) -> Unit,
    onNavigateToEssayQuizBuilder: (courseId: String, chapterId: String, quizId: String?) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val studentsState by viewModel.studentsState.collectAsState()

    var showAddChapterDialog by remember { mutableStateOf(false) }
    var showAddLessonDialogForChapter by remember { mutableStateOf<String?>(null) }
    var newItemTitle by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(ManagerTab.Curriculum) }
    var showQuizTypeDialog by remember { mutableStateOf(false) }
    var selectedChapterIdForQuiz by remember { mutableStateOf<String?>(null) }

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

    if (showQuizTypeDialog) {
        AlertDialog(
            onDismissRequest = { showQuizTypeDialog = false },
            title = { Text("Choose Quiz Type", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Select the type of quiz you want to create for this chapter.")
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedButton(
                        onClick = {
                            selectedChapterIdForQuiz?.let { chapterId ->
                                onNavigateToQuizBuilder(courseId, chapterId, null)
                            }
                            showQuizTypeDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryOrange),
                        border = BorderStroke(1.dp, PrimaryOrange)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                            Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Multiple Choice", fontWeight = FontWeight.Bold)
                                Text("Traditional Q&A with options", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedButton(
                        onClick = {
                            selectedChapterIdForQuiz?.let { chapterId ->
                                onNavigateToEssayQuizBuilder(courseId, chapterId, null)
                            }
                            showQuizTypeDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF8F4C37)),
                        border = BorderStroke(1.dp, Color(0xFF8F4C37))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(24.dp))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Essay Quiz", fontWeight = FontWeight.Bold)
                                Text("Open-ended critical thinking", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showQuizTypeDialog = false }) { Text("Cancel", color = Color.Gray) }
            }
        )
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Course Manager", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = PrimaryOrange,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                            color = PrimaryOrange
                        )
                    }
                ) {
                    ManagerTab.values().forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { 
                                selectedTab = tab 
                                if (tab == ManagerTab.Students) viewModel.loadStudents()
                            },
                            text = { Text(tab.name, fontWeight = FontWeight.Bold) },
                            selectedContentColor = PrimaryOrange,
                            unselectedContentColor = Color.Gray
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == ManagerTab.Curriculum) {
                ExtendedFloatingActionButton(
                    onClick = { showAddChapterDialog = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Chapter") },
                    text = { Text("Add Chapter", fontWeight = FontWeight.Bold) },
                    containerColor = PrimaryOrange,
                    contentColor = Color.White,
                    shape = CircleShape
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            var isRefreshing by remember { mutableStateOf(false) }

            LaunchedEffect(uiState) {
                if (uiState !is CourseManagerState.Loading) {
                    isRefreshing = false
                }
            }
            LaunchedEffect(studentsState) {
                if (studentsState != null) {
                    isRefreshing = false
                }
            }

            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    if (selectedTab == ManagerTab.Curriculum) {
                        viewModel.loadCourseStructure(token, courseId)
                    } else {
                        viewModel.loadStudents()
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) {
                when (selectedTab) {
                    ManagerTab.Curriculum -> {
                        when (val state = uiState) {
                            is CourseManagerState.Loading -> {
                                if (!isRefreshing) {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryOrange)
                                }
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
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
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

                                    if (course.status == "DRAFT") {
                                        Button(
                                            onClick = { viewModel.requestApproval() },
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Request Approval", color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    } else if (course.status == "PENDING") {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.Yellow.copy(alpha = 0.3f)
                                        ) {
                                            Text(
                                                text = "Pending Approval",
                                                color = Color(0xFFB8860B),
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    } else if (course.status == "PUBLISHED") {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color.Green.copy(alpha = 0.1f)
                                        ) {
                                            Text(
                                                text = "Published",
                                                color = Color(0xFF4CAF50),
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
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
                                            onClick = { 
                                                selectedChapterIdForQuiz = chapter.id
                                                showQuizTypeDialog = true 
                                            },
                                            modifier = Modifier.weight(1f),
                                            colors = ButtonDefaults.textButtonColors(contentColor = PrimaryOrange)
                                        ) {
                                            Icon(Icons.Default.Quiz, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(Modifier.width(8.dp))
                                            Text("Add Quiz", fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    // Quizzes List
                                    if (!chapter.quizzes.isNullOrEmpty()) {
                                        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                                            Text("Quizzes", style = MaterialTheme.typography.titleSmall, color = PrimaryOrange, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                                            chapter.quizzes?.forEach { quiz ->
                                                Card(
                                                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { 
                                                        if (quiz.isEssay) {
                                                            onNavigateToEssayQuizBuilder(courseId, chapter.id, quiz.id)
                                                        } else {
                                                            onNavigateToQuizBuilder(courseId, chapter.id, quiz.id)
                                                        }
                                                    },
                                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                                    shape = RoundedCornerShape(12.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(Icons.Default.Quiz, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(24.dp))
                                                        Spacer(modifier = Modifier.width(12.dp))
                                                        Column(modifier = Modifier.weight(1f)) {
                                                            Text(text = quiz.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                                            Text(text = "${quiz.questions?.size ?: 0} questions • ${quiz.timeLimit} mins • Pass: ${quiz.passingScore.toInt()}%", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                                        }
                                                        Icon(Icons.Default.Edit, contentDescription = "Edit Quiz", tint = Color.Gray)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } // closes Success
                } // closes when(uiState)
                } // closes ManagerTab.Curriculum
                ManagerTab.Students -> {
                    CourseStudentsView(studentsState)
                }
            }
            }
        }
    }
}

@Composable
fun CourseStudentsView(studentsList: List<com.example.skillforge.data.remote.CourseStudentDto>?) {
    if (studentsList == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryOrange)
        }
        return
    }

    if (studentsList.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No students enrolled yet.", color = Color.Gray)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(studentsList) { student ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = student.avatarUrl ?: "https://via.placeholder.com/150",
                        contentDescription = "Avatar",
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.size(50.dp).clip(CircleShape).border(1.dp, PrimaryOrange, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(student.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(student.email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LinearProgressIndicator(
                                progress = { student.progressPercentage / 100f },
                                modifier = Modifier.weight(1f).height(6.dp),
                                color = PrimaryOrange,
                                trackColor = MaterialTheme.colorScheme.outlineVariant,
                                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${student.progressPercentage}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryOrange)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    OutlinedButton(
                        onClick = { /* TODO: View submissions */ },
                        border = BorderStroke(1.dp, PrimaryOrange),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryOrange),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("View submission", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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

