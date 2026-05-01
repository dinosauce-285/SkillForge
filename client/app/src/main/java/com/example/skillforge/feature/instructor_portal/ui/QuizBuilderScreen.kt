package com.example.skillforge.feature.instructor_portal.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillforge.core.designsystem.BackgroundColor
import com.example.skillforge.core.designsystem.PrimaryOrange
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

import com.example.skillforge.feature.instructor_portal.viewmodel.QuizBuilderViewModel
import com.example.skillforge.feature.instructor_portal.viewmodel.QuizUiState
import com.example.skillforge.domain.model.Question

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizBuilderScreen(
    initialTab: Int = 0,
    viewModel: QuizBuilderViewModel,
    onBackClick: () -> Unit = {},
    onPublishClick: () -> Unit = {},
    onAddQuestionClick: () -> Unit = {},
    onEditQuestionClick: (questionId: String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var quizTitle by remember { mutableStateOf("") }
    var quizDescription by remember { mutableStateOf("") }
    
    var selectedTab by remember { mutableStateOf(initialTab) }

    LaunchedEffect(initialTab) {
        selectedTab = initialTab
    }

    val lazyListState = rememberLazyListState()
    // The reorderable list uses header items (title, description, "Curated Questions", add button) = 4 items offset
    val headerItemCount = 4
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        val fromQuestionIndex = from.index - headerItemCount
        val toQuestionIndex = to.index - headerItemCount
        viewModel.reorderQuestions(fromQuestionIndex, toQuestionIndex)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (selectedTab == 0) "SkillForge Builder" else "Quiz Settings", 
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
                    TextButton(onClick = { onPublishClick() }) {
                        Text("Done", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            QuizBuilderBottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        if (selectedTab == 0) {
            when (uiState) {
                is QuizUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryOrange)
                    }
                }
                is QuizUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = (uiState as QuizUiState.Error).message, color = Color.Red)
                    }
                }
                is QuizUiState.Success -> {
                    val quiz = (uiState as QuizUiState.Success).quiz
                    val questions = quiz?.questions ?: emptyList()
                    
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(24.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        item {
                            Column {
                                Text(text = "CREATION SUITE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryOrange, letterSpacing = 2.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Quiz Builder", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color.Black)
                            }
                        }

                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                QuizInput(label = "Title of Assessment", value = quizTitle, onValueChange = { quizTitle = it }, placeholder = "e.g. Advanced Macroeconomics Final")
                                QuizInput(label = "Brief Description", value = quizDescription, onValueChange = { quizDescription = it }, placeholder = "Define the core objectives of this quiz...", isMultiline = true)
                            }
                        }

                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                                Text("Curated Questions", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text("${questions.size} Items", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        item {
                            OutlinedButton(
                                onClick = onAddQuestionClick,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(2.dp, PrimaryOrange.copy(alpha = 0.3f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryOrange),
                                contentPadding = PaddingValues(vertical = 32.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                    Icon(imageVector = Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("APPEND NEW QUESTION", fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                }
                            }
                        }

                        itemsIndexed(questions, key = { _, item -> item.id }) { index, question ->
                            ReorderableItem(reorderableLazyListState, key = question.id) { isDragging ->
                                val elevation by animateDpAsState(if (isDragging) 8.dp else 0.dp)
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shadowElevation = elevation,
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color.Transparent
                                ) {
                                    QuestionCard(
                                        number = index + 1,
                                        data = question,
                                        onEditClick = { onEditQuestionClick(question.id) },
                                        dragModifier = Modifier.longPressDraggableHandle(
                                            onDragStopped = {
                                                viewModel.commitReorder()
                                            }
                                        )
                                    )
                                }
                            }
                        }
                        
                        item { Spacer(modifier = Modifier.height(32.dp)) }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.padding(paddingValues)) {
                val quiz = (uiState as? QuizUiState.Success)?.quiz
                QuizSettingsScreen(
                    initialTitle = quiz?.title ?: "Untitled Quiz",
                    initialPassMark = quiz?.passingScore?.toInt()?.toString() ?: "50",
                    initialTimeLimit = quiz?.timeLimit?.toString() ?: "60",
                    initialShuffle = quiz?.randomizeQuestions ?: false,
                    onSaveSettings = { title, passMark, timeLimit, shuffle ->
                        viewModel.updateQuizSettings(
                            title = title,
                            timeLimit = timeLimit.toIntOrNull() ?: 60,
                            passingScore = passMark.toFloatOrNull() ?: 50f,
                            randomizeQuestions = shuffle
                        )
                        onPublishClick()
                    },
                    onDeleteQuiz = {
                        viewModel.deleteQuiz {
                            onBackClick()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun QuizBuilderBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
        QuizNavButton(icon = Icons.Default.Construction, label = "Builder", isSelected = selectedTab == 0, onClick = { onTabSelected(0) })
        QuizNavButton(icon = Icons.Default.Settings, label = "Settings", isSelected = selectedTab == 1, onClick = { onTabSelected(1) })
    }
}

@Composable
fun RowScope.QuizNavButton(icon: ImageVector, label: String, isSelected: Boolean, onClick: () -> Unit) {
    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(label.uppercase(), fontSize = 9.sp, fontWeight = FontWeight.Bold) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = PrimaryOrange,
            selectedTextColor = PrimaryOrange,
            unselectedIconColor = Color.Gray,
            unselectedTextColor = Color.Gray,
            indicatorColor = PrimaryOrange.copy(alpha = 0.1f)
        )
    )
}

@Composable
fun QuizInput(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String, isMultiline: Boolean = false) {
    Column {
        Text(text = label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp, modifier = Modifier.padding(start = 4.dp, bottom = 8.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray.copy(alpha = 0.6f)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
            minLines = if (isMultiline) 3 else 1
        )
    }
}

@Composable
fun QuestionCard(number: Int, data: Question, onEditClick: () -> Unit = {}, dragModifier: Modifier = Modifier) {
    Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shape = RoundedCornerShape(16.dp), shadowElevation = 1.dp) {
        Row(modifier = Modifier.padding(20.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Drag handle
            Icon(
                Icons.Default.DragHandle,
                contentDescription = "Drag to reorder",
                tint = Color.LightGray,
                modifier = dragModifier.size(24.dp)
            )
            Box(modifier = Modifier.size(40.dp).background(PrimaryOrange.copy(alpha = 0.1f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Text(text = number.toString().padStart(2, '0'), color = PrimaryOrange, fontWeight = FontWeight.Bold)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Question $number", fontWeight = FontWeight.Bold, color = Color.Black)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = data.content, fontSize = 14.sp, color = Color.DarkGray, lineHeight = 20.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Badge("MULTIPLE CHOICE")
                    Badge("${data.choices.size} OPTIONS")
                }
            }
            IconButton(onClick = onEditClick, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryOrange, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun Badge(text: String) {
    Surface(color = Color(0xFFF3F3F4), shape = RoundedCornerShape(100.dp) ) {
        Text(text = text, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
    }
}
