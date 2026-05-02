package com.example.skillforge.feature.instructor_portal.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillforge.core.designsystem.BackgroundColor
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.domain.model.Question
import com.example.skillforge.feature.instructor_portal.viewmodel.QuizBuilderViewModel
import com.example.skillforge.feature.instructor_portal.viewmodel.QuizUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizBuilderScreen(
    viewModel: QuizBuilderViewModel,
    chapterId: String? = null,
    quizId: String? = null,
    initialTab: Int = 0,
    onBackClick: () -> Unit = {},
    onNavigateToAddQuestion: () -> Unit = {},
    onNavigateToEditQuestion: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(initialTab) }
    var hasInitialized by rememberSaveable { mutableStateOf(false) }

    // Trigger load or create
    LaunchedEffect(quizId, chapterId) {
        if (!hasInitialized) {
            hasInitialized = true
            if (quizId != null) {
                viewModel.loadQuiz(quizId)
            } else if (chapterId != null) {
                viewModel.createNewQuiz(chapterId, "Untitled Quiz", 45, 80f)
            }
        }
    }

    // Delete confirmation dialog
    var showDeleteQuestionDialog by remember { mutableStateOf<String?>(null) }
    if (showDeleteQuestionDialog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteQuestionDialog = null },
            title = { Text("Delete Question", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this question? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteQuestion(showDeleteQuestionDialog!!)
                        showDeleteQuestionDialog = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteQuestionDialog = null }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (selectedTab == 0) "Quiz Builder" else "Quiz Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = onBackClick) {
                        Text("Publish", fontWeight = FontWeight.Bold, color = PrimaryOrange)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Construction, contentDescription = null) },
                    label = { Text("BUILDER", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryOrange, selectedTextColor = PrimaryOrange,
                        unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray,
                        indicatorColor = PrimaryOrange.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("SETTINGS", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryOrange, selectedTextColor = PrimaryOrange,
                        unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray,
                        indicatorColor = PrimaryOrange.copy(alpha = 0.1f)
                    )
                )
            }
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (val state = uiState) {
                is QuizUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PrimaryOrange
                    )
                }

                is QuizUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(state.message, color = Color(0xFFE53935), fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                if (quizId != null) viewModel.loadQuiz(quizId)
                                else if (chapterId != null) viewModel.createNewQuiz(chapterId, "Untitled Quiz", 45, 80f)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                        ) { Text("Retry") }
                    }
                }

                is QuizUiState.Success -> {
                    val quiz = state.quiz
                    if (quiz != null) {
                        if (selectedTab == 0) {
                            QuestionsListContent(
                                questions = quiz.questions.sortedBy { it.orderIndex },
                                onAddQuestionClick = onNavigateToAddQuestion,
                                onEditQuestionClick = onNavigateToEditQuestion,
                                onDeleteQuestionClick = { questionId -> showDeleteQuestionDialog = questionId },
                                onMoveUp = { index -> if (index > 0) { viewModel.reorderQuestions(index, index - 1); viewModel.commitReorder() } },
                                onMoveDown = { index -> if (index < quiz.questions.size - 1) { viewModel.reorderQuestions(index, index + 1); viewModel.commitReorder() } }
                            )
                        } else {
                            QuizSettingsScreen(
                                initialTitle = quiz.title,
                                initialTimeLimit = quiz.timeLimit.toString(),
                                initialPassMark = quiz.passingScore.toInt().toString(),
                                initialShuffle = quiz.randomizeQuestions,
                                onSaveSettings = { title, timeLimit, passingScore, shuffle ->
                                    viewModel.updateQuizSettings(
                                        title = title,
                                        timeLimit = timeLimit.toIntOrNull() ?: 45,
                                        passingScore = (passingScore.toFloatOrNull() ?: 80f),
                                        randomizeQuestions = shuffle
                                    )
                                },
                                onDeleteQuiz = { viewModel.deleteQuiz(onDeleted = onBackClick) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionsListContent(
    questions: List<Question>,
    onAddQuestionClick: () -> Unit,
    onEditQuestionClick: (String) -> Unit,
    onDeleteQuestionClick: (String) -> Unit,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text("CREATION SUITE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryOrange, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Questions", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.Black)
            }
        }

        // Count
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${questions.size} question(s)", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        }

        // Add question button
        item {
            OutlinedButton(
                onClick = onAddQuestionClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, PrimaryOrange.copy(alpha = 0.3f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryOrange),
                contentPadding = PaddingValues(vertical = 24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("ADD NEW QUESTION", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }
        }

        // Question cards
        itemsIndexed(questions, key = { _, q -> q.id }) { index, question ->
            QuestionItemCard(
                index = index,
                question = question,
                isFirst = index == 0,
                isLast = index == questions.lastIndex,
                onEdit = { onEditQuestionClick(question.id) },
                onDelete = { onDeleteQuestionClick(question.id) },
                onMoveUp = { onMoveUp(index) },
                onMoveDown = { onMoveDown(index) }
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun QuestionItemCard(
    index: Int,
    question: Question,
    isFirst: Boolean,
    isLast: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Number badge
                Box(
                    modifier = Modifier.size(36.dp).background(PrimaryOrange.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (index + 1).toString().padStart(2, '0'),
                        color = PrimaryOrange,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Question content
                Column(modifier = Modifier.weight(1f)) {
                    Text("Question ${index + 1}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = question.content,
                        fontSize = 13.sp,
                        color = Color.DarkGray,
                        lineHeight = 18.sp,
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Choices preview
            val correctChoice = question.choices.find { it.isCorrect }
            if (correctChoice != null) {
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Correct: ${correctChoice.content}", fontSize = 12.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Info row
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(color = Color(0xFFF3F3F4), shape = RoundedCornerShape(100.dp)) {
                    Text(
                        text = "${question.choices.size} CHOICES",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Move up
                IconButton(onClick = onMoveUp, enabled = !isFirst, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up", tint = if (!isFirst) PrimaryOrange else Color.LightGray)
                }

                // Move down
                IconButton(onClick = onMoveDown, enabled = !isLast, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down", tint = if (!isLast) PrimaryOrange else Color.LightGray)
                }

                // Edit
                IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF1976D2))
                }

                // Delete
                IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFE53935))
                }
            }
        }
    }
}
