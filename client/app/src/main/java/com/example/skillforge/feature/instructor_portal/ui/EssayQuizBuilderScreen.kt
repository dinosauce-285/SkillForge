package com.example.skillforge.feature.instructor_portal.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.feature.instructor_portal.viewmodel.QuizBuilderViewModel
import com.example.skillforge.feature.instructor_portal.viewmodel.QuizUiState

// Define local colors from the HTML design
private val EssayPrimary = Color(0xFFAC3509)
private val EssayPrimaryContainer = Color(0xFFFF7043)
private val EssaySurfaceLow = Color(0xFFF3F3F4)
private val EssaySurfaceLowest = Color(0xFFFFFFFF)
private val EssayOnSurfaceVariant = Color(0xFF59413A)
private val EssayOutline = Color(0xFF8D7169)
private val EssayTertiary = Color(0xFF006972)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EssayQuizBuilderScreen(
    viewModel: QuizBuilderViewModel,
    courseId: String,
    chapterId: String? = null,
    quizId: String? = null,
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var hasInitialized by remember { mutableStateOf(false) }
    
    // Load or create quiz
    LaunchedEffect(quizId, chapterId) {
        if (!hasInitialized) {
            hasInitialized = true
            if (quizId != null) {
                viewModel.loadQuiz(quizId)
            } else if (chapterId != null) {
                viewModel.createNewQuiz(chapterId, "New Essay Quiz", 60, 70f, isEssay = true)
            }
        }
    }

    var showAddQuestionDialog by remember { mutableStateOf(false) }
    var editingQuestion by remember { mutableStateOf<com.example.skillforge.domain.model.Question?>(null) }
    var questionToDelete by remember { mutableStateOf<com.example.skillforge.domain.model.Question?>(null) }

    if (questionToDelete != null) {
        AlertDialog(
            onDismissRequest = { questionToDelete = null },
            title = { Text("Delete Question", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this question? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteQuestion(questionToDelete!!.id)
                        questionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { questionToDelete = null }) { Text("Cancel") }
            }
        )
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (showAddQuestionDialog || editingQuestion != null) {
        var prompt by remember { mutableStateOf(editingQuestion?.content ?: "") }
        var minWords by remember { mutableStateOf(editingQuestion?.minWords?.toString() ?: "500") }
        var points by remember { mutableStateOf(editingQuestion?.points?.toString() ?: "25") }

        ModalBottomSheet(
            onDismissRequest = { 
                showAddQuestionDialog = false
                editingQuestion = null
            },
            sheetState = sheetState,
            containerColor = EssaySurfaceLowest,
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.Gray.copy(alpha = 0.2f), CircleShape)
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 32.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Modal Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (editingQuestion != null) "Edit Essay Question" else "Add Essay Question",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = EssayPrimary,
                        letterSpacing = (-1).sp
                    )
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFFFFDBD0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = EssayPrimary, modifier = Modifier.size(20.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "Define the prompt and constraints for your students' essay response. Clear expectations lead to higher quality submissions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = EssayOnSurfaceVariant,
                    lineHeight = 20.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                // Question Prompt
                Text("Question Prompt", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(start = 4.dp))
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    placeholder = { Text("e.g., Analyze the use of metaphor in the provided text...", color = EssayOutline.copy(alpha = 0.5f)) },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = EssaySurfaceLow,
                        unfocusedContainerColor = EssaySurfaceLow,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Grid
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Min Word Count
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Min. Word Count", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(start = 4.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(contentAlignment = Alignment.CenterEnd) {
                            TextField(
                                value = minWords,
                                onValueChange = { minWords = it },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(16.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = EssaySurfaceLow,
                                    unfocusedContainerColor = EssaySurfaceLow,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )
                            Text("WORDS", modifier = Modifier.padding(end = 16.dp), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = EssayOutline.copy(alpha = 0.6f))
                        }
                    }

                    // Points
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Points", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(start = 4.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(contentAlignment = Alignment.CenterEnd) {
                            TextField(
                                value = points,
                                onValueChange = { points = it },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(16.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = EssaySurfaceLow,
                                    unfocusedContainerColor = EssaySurfaceLow,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )
                            Text("PTS", modifier = Modifier.padding(end = 16.dp), fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = EssayOutline.copy(alpha = 0.6f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Actions
                Button(
                    onClick = {
                        if (editingQuestion != null) {
                            viewModel.updateEssayQuestion(
                                questionId = editingQuestion!!.id,
                                content = prompt,
                                minWords = minWords.toIntOrNull() ?: 500,
                                points = points.toIntOrNull() ?: 25
                            )
                        } else {
                            viewModel.addEssayQuestion(
                                content = prompt,
                                minWords = minWords.toIntOrNull() ?: 500,
                                points = points.toIntOrNull() ?: 25
                            )
                        }
                        showAddQuestionDialog = false
                        editingQuestion = null
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EssayPrimaryContainer),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(if (editingQuestion != null) "Update Question" else "Add Question", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = { 
                        showAddQuestionDialog = false 
                        editingQuestion = null
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("Cancel", fontWeight = FontWeight.SemiBold, color = EssayOnSurfaceVariant)
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Aesthetic Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(EssayPrimary, EssayPrimaryContainer, EssayTertiary)
                            )
                        )
                )
            }
        }
    }

    when (val state = uiState) {
        is QuizUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EssayPrimary)
            }
        }
        is QuizUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(state.message, color = Color.Red)
            }
        }
        is QuizUiState.Success -> {
            val quiz = state.quiz
            if (quiz != null) {
                var localTitle by remember { mutableStateOf(quiz.title) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    if (selectedTab == 0) "The Academic Atelier" else "Quiz Configuration",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = Color.Black
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = onBackClick) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = EssayPrimary
                                    )
                                }
                            },
                            actions = {
                                if (selectedTab == 0) {
                                    Button(
                                        onClick = { 
                                            viewModel.updateQuizSettings(
                                                title = localTitle,
                                                timeLimit = quiz.timeLimit,
                                                passingScore = quiz.passingScore,
                                                randomizeQuestions = quiz.randomizeQuestions
                                            )
                                            onBackClick()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = EssayPrimaryContainer),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Text("Publish", fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White.copy(alpha = 0.8f))
                        )
                    },
                    bottomBar = {
                        NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                icon = { Icon(Icons.Default.Build, contentDescription = null) },
                                label = { Text("BUILDER", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = EssayPrimary,
                                    selectedTextColor = EssayPrimary,
                                    unselectedIconColor = Color.Gray,
                                    unselectedTextColor = Color.Gray,
                                    indicatorColor = EssayPrimary.copy(alpha = 0.1f)
                                )
                            )
                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                                label = { Text("SETTINGS", fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = EssayPrimary,
                                    selectedTextColor = EssayPrimary,
                                    unselectedIconColor = Color.Gray,
                                    unselectedTextColor = Color.Gray,
                                    indicatorColor = EssayPrimary.copy(alpha = 0.1f)
                                )
                            )
                        }
                    },
                    containerColor = Color(0xFFF9F9F9)
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                        if (selectedTab == 0) {
                            EssayQuizContent(
                                quizTitle = localTitle,
                                onTitleChange = { localTitle = it },
                                questions = quiz.questions,
                                onAddQuestionClick = { showAddQuestionDialog = true },
                                onEditQuestionClick = { editingQuestion = it },
                                onDeleteQuestionClick = { questionToDelete = it }
                            )
                        } else {
                            EssaySettingsContent(
                                quiz = quiz,
                                onSaveSettings = { title, timeLimit, passingScore, randomize ->
                                    viewModel.updateQuizSettings(title, timeLimit, passingScore, randomize)
                                },
                                onDeleteQuiz = {
                                    viewModel.deleteQuiz { onBackClick() }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EssaySettingsContent(
    quiz: com.example.skillforge.domain.model.Quiz,
    onSaveSettings: (String, Int, Float, Boolean) -> Unit,
    onDeleteQuiz: () -> Unit
) {
    var title by remember { mutableStateOf(quiz.title) }
    var timeLimit by remember { mutableStateOf(quiz.timeLimit.toString()) }
    var passingScore by remember { mutableStateOf(quiz.passingScore.toInt().toString()) }
    var randomizeQuestions by remember { mutableStateOf(quiz.randomizeQuestions) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Essay Quiz", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this essay quiz? All prompts and configurations will be lost.") },
            confirmButton = {
                Button(
                    onClick = { showDeleteDialog = false; onDeleteQuiz() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    "CONFIGURATIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EssayPrimary,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Quiz Parameters", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.Black)
            }
        }

        // Title
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White, shape = RoundedCornerShape(16.dp), shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("QUIZ TITLE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = EssaySurfaceLow, unfocusedContainerColor = EssaySurfaceLow,
                            focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }

        // Numbers
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White, shape = RoundedCornerShape(16.dp), shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    // Time Limit
                    Column {
                        Text("TIME LIMIT (MINUTES)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        TextField(
                            value = timeLimit,
                            onValueChange = { timeLimit = it },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = EssaySurfaceLow, unfocusedContainerColor = EssaySurfaceLow,
                                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }

                    // Passing Score
                    Column {
                        Text("PASSING SCORE (%)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        TextField(
                            value = passingScore,
                            onValueChange = { passingScore = it },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = EssaySurfaceLow, unfocusedContainerColor = EssaySurfaceLow,
                                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        }

        // Randomize
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White, shape = RoundedCornerShape(16.dp), shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Randomize Prompts", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Shuffle the order of essay questions for each student.", fontSize = 12.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = randomizeQuestions,
                        onCheckedChange = { randomizeQuestions = it },
                        colors = SwitchDefaults.colors(checkedTrackColor = EssayPrimary)
                    )
                }
            }
        }

        // Save
        item {
            Button(
                onClick = {
                    onSaveSettings(
                        title,
                        timeLimit.toIntOrNull() ?: 60,
                        passingScore.toFloatOrNull() ?: 70f,
                        randomizeQuestions
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EssayPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Configuration", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        // Delete
        item {
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935)),
                border = BorderStroke(1.dp, Color(0xFFE53935))
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Quiz", fontWeight = FontWeight.Bold)
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
fun EssayQuizContent(
    quizTitle: String,
    onTitleChange: (String) -> Unit,
    questions: List<com.example.skillforge.domain.model.Question>,
    onAddQuestionClick: () -> Unit,
    onEditQuestionClick: (com.example.skillforge.domain.model.Question) -> Unit,
    onDeleteQuestionClick: (com.example.skillforge.domain.model.Question) -> Unit
) {
    var title by remember { mutableStateOf(quizTitle) }
    var objectives by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Hero Section
        item {
            Column {
                Text(
                    "CONTENT CREATOR",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EssayPrimary,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Essay Quiz ",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        "Builder",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = EssayPrimaryContainer,
                        letterSpacing = (-1).sp
                    )
                }
            }
        }

        // Form Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                // Quiz Title
                Column {
                    Text(
                        "Quiz Title",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EssayOnSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    TextField(
                        value = title,
                        onValueChange = { title = it; onTitleChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g., Classical Economics & Modern Theory", color = EssayOutline.copy(alpha = 0.5f)) },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = EssaySurfaceLow,
                            unfocusedContainerColor = EssaySurfaceLow,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }

                // Learning Objectives
                Column {
                    Text(
                        "Learning Objectives",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EssayOnSurfaceVariant,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    TextField(
                        value = objectives,
                        onValueChange = { objectives = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Describe what students will master after completing this essay series...", color = EssayOutline.copy(alpha = 0.5f)) },
                        shape = RoundedCornerShape(16.dp),
                        minLines = 3,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = EssaySurfaceLow,
                            unfocusedContainerColor = EssaySurfaceLow,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }

        // Questions List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    "Curated Questions",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    "${questions.size} Questions Added",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = EssayOnSurfaceVariant
                )
            }
        }

        // Question Cards
        items(questions) { question ->
            EssayQuestionCard(
                question = question,
                onEditClick = { onEditQuestionClick(question) },
                onDeleteClick = { onDeleteQuestionClick(question) }
            )
        }

        // Add Action
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAddQuestionClick() },
                color = Color.Transparent,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, Brush.linearGradient(listOf(EssayOutline.copy(alpha = 0.3f), EssayOutline.copy(alpha = 0.3f)))) // Simplified dashed border
            ) {
                Column(
                    modifier = Modifier.padding(vertical = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .background(Color(0xFFFFDBD0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = EssayPrimary, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Add Essay Question",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EssayOnSurfaceVariant
                    )
                    Text(
                        "Define prompt, constraints, and scoring",
                        fontSize = 14.sp,
                        color = EssayOutline,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }

        // Illustration Section
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = EssaySurfaceLow,
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDDVKHEcG77ya_OX43hYuSm3rhqkNHPIfQqcpO1i4ohSDHgmhqKu4exU9dBebYPY0w429PLuH8Jx0e25NLOeElx21ulCJke39ge4cGfazfoeyryguICEZ2bTjsj4E2BVGY6eGiiZ2lRx_97y436DTEUP5Sgl6qUm0ReYIfM06dps8TvmjVyfIZP3cTeLv2vbym3Vyo8pC77_Kug7xZrCdV5zEugQrdGQY8NlYC_rJYZrhlJRhQxeCXxdhzQMCy6P1XsSAlisEA5vA",
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Writer's Focus",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EssayPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "\"Open-ended assessments are the cornerstone of critical thinking. Use this space to challenge students beyond simple recall.\"",
                        fontSize = 16.sp,
                        color = EssayOnSurfaceVariant,
                        lineHeight = 24.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.width(32.dp).height(4.dp).background(EssayPrimary, CircleShape))
                        Box(modifier = Modifier.width(8.dp).height(4.dp).background(EssayOutline.copy(alpha = 0.3f), CircleShape))
                        Box(modifier = Modifier.width(8.dp).height(4.dp).background(EssayOutline.copy(alpha = 0.3f), CircleShape))
                    }
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
fun EssayQuestionCard(
    question: com.example.skillforge.domain.model.Question,
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = EssaySurfaceLowest,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp
    ) {
        Box {
            // Side accent
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
                    .background(EssayPrimaryContainer)
            )
            
            Column(modifier = Modifier.padding(24.dp).padding(start = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = EssayPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "ESSAY PROMPT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EssayPrimary,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            question.content,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            lineHeight = 26.sp
                        )
                    }
                    Row {
                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = EssayOutline)
                        }
                        IconButton(onClick = onDeleteClick) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = EssayOutline)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Surface(
                        color = EssaySurfaceLow,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF8F4C37), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${question.minWords ?: 0} words", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Surface(
                        color = EssayTertiary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = EssayTertiary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${question.points} Points", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EssayTertiary)
                        }
                    }
                }
            }
        }
    }
}
