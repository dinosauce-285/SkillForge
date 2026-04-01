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

    // 🌟 Quản lý trạng thái bật/tắt Popup (Dialog)
    var showAddChapterDialog by remember { mutableStateOf(false) }

    // Lưu chapterId khi bấm "Thêm Bài học" để biết đang thêm vào chương nào
    var showAddLessonDialogForChapter by remember { mutableStateOf<String?>(null) }
    var newItemTitle by remember { mutableStateOf("") }

    // Gọi API lấy dữ liệu ngay khi vào màn hình
    LaunchedEffect(Unit) {
        viewModel.loadCourseStructure(token, courseId)
    }

    // --- DIALOG THÊM CHƯƠNG ---
    if (showAddChapterDialog) {
        AlertDialog(
            onDismissRequest = { showAddChapterDialog = false; newItemTitle = "" },
            title = { Text("Thêm Chương mới") },
            text = {
                OutlinedTextField(
                    value = newItemTitle,
                    onValueChange = { newItemTitle = it },
                    label = { Text("Tên chương") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.createChapter(newItemTitle)
                    showAddChapterDialog = false
                    newItemTitle = ""
                }) { Text("Tạo") }
            },
            dismissButton = {
                TextButton(onClick = { showAddChapterDialog = false; newItemTitle = "" }) { Text("Hủy") }
            }
        )
    }

    // --- DIALOG THÊM BÀI HỌC ---
    if (showAddLessonDialogForChapter != null) {
        AlertDialog(
            onDismissRequest = { showAddLessonDialogForChapter = null; newItemTitle = "" },
            title = { Text("Thêm Bài học mới") },
            text = {
                OutlinedTextField(
                    value = newItemTitle,
                    onValueChange = { newItemTitle = it },
                    label = { Text("Tên bài học") },
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
                }) { Text("Tạo") }
            },
            dismissButton = {
                TextButton(onClick = { showAddLessonDialogForChapter = null; newItemTitle = "" }) { Text("Hủy") }
            }
        )
    }

    // --- GIAO DIỆN CHÍNH ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý Nội dung", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            // Nút nổi (FAB) to bự ở góc để thêm Chương
            ExtendedFloatingActionButton(
                onClick = { showAddChapterDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Add Chapter") },
                text = { Text("Thêm Chương") },
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
                        text = "Lỗi: ${state.message}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                is CourseManagerState.Success -> {
                    val course = state.course

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp) // Chừa chỗ cho cái nút nổi FAB
                    ) {
                        // Tiêu đề khóa học
                        item {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Text(course.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                                Text(course.category?.name ?: "Chưa có danh mục", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }

                        // Danh sách Chương
                        items(course.chapters) { chapter ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column {
                                    // Header Chương
                                    Row(
                                        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.secondaryContainer).padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Chương ${chapter.orderIndex + 1}: ${chapter.title}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }

                                    // Danh sách Bài học của Chương này
                                    if (chapter.lessons.isEmpty()) {
                                        Text("Chưa có bài học nào.", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodySmall)
                                    } else {
                                        chapter.lessons.forEach { lesson ->
                                            LessonItemRow(
                                                lesson = lesson,
                                                onAddMaterial = { onNavigateToUpload(lesson.id) }
                                            )
                                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                        }
                                    }

                                    // Nút Thêm Bài học nhét ở cuối mỗi Chương
                                    TextButton(
                                        onClick = { showAddLessonDialogForChapter = chapter.id },
                                        modifier = Modifier.fillMaxWidth().padding(8.dp)
                                    ) {
                                        Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Thêm Bài học")
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
                text = "Bài ${lesson.orderIndex + 1}: ${lesson.title}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            // Hiển thị số lượng tài liệu
            Text(
                text = if (lesson.materials.isEmpty()) "Chưa có tài liệu" else "${lesson.materials.size} tài liệu",
                style = MaterialTheme.typography.bodySmall,
                color = if (lesson.materials.isEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }

        IconButton(
            onClick = onAddMaterial,
            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(Icons.Default.Attachment, contentDescription = "Thêm tài liệu")
        }
    }
}