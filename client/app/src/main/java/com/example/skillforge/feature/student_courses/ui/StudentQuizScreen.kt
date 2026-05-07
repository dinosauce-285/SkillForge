package com.example.skillforge.feature.student_courses.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.domain.model.Quiz
import com.example.skillforge.domain.model.Question
import com.example.skillforge.domain.model.AnswerChoice

// Define colors from the mockup
val AcademicPrimary = Color(0xFFAC3509)
val AcademicPrimaryContainer = Color(0xFFFF7043)
val AcademicOnPrimaryContainer = Color(0xFF641800)
val AcademicSecondary = Color(0xFF8F4C37)
val AcademicTertiary = Color(0xFF006972)
val AcademicBackground = Color(0xFFF9F9F9)
val AcademicSurfaceVariant = Color(0xFFEEEEEE)
val AcademicSurfaceLow = Color(0xFFF3F3F4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentQuizScreen(
    quiz: Quiz,
    questions: List<Question>,
    timeRemainingSeconds: Int,
    isTimeUp: Boolean = false,
    onBack: () -> Unit,
    onSubmit: (Map<String, String>) -> Unit
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var userAnswers by remember { mutableStateOf(mapOf<String, String>()) } // questionId to answer (optionId or text)
    var showExitWarning by remember { mutableStateOf(false) }

    val formatTime = { seconds: Int ->
        val m = seconds / 60
        val s = seconds % 60
        String.format("%02d:%02d", m, s)
    }

    if (showExitWarning) {
        AlertDialog(
            onDismissRequest = { showExitWarning = false },
            title = { Text("Exit Quiz?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to exit? Your progress will not be saved.") },
            confirmButton = {
                TextButton(onClick = {
                    showExitWarning = false
                    onBack()
                }) {
                    Text("Exit", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitWarning = false }) {
                    Text("Cancel", fontWeight = FontWeight.Medium)
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (isTimeUp) {
        AlertDialog(
            onDismissRequest = { },
            title = { 
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TimerOff, contentDescription = null, tint = AcademicPrimary)
                    Spacer(Modifier.width(12.dp))
                    Text("Time's Up!", fontWeight = FontWeight.Bold)
                }
            },
            text = { Text("The time for this quiz has expired. Your current answers will be submitted automatically.") },
            confirmButton = {
                Button(
                    onClick = { onSubmit(userAnswers) },
                    colors = ButtonDefaults.buttonColors(containerColor = AcademicPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Submit Now", fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(24.dp),
            properties = androidx.compose.ui.window.DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        )
        
        // Auto-submit after a short delay if they don't click anything
        LaunchedEffect(Unit) {
            delay(3000)
            onSubmit(userAnswers)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Academic Atelier", fontWeight = FontWeight.ExtraBold, color = AcademicOnPrimaryContainer, fontSize = 20.sp) 
                },
                navigationIcon = {
                    IconButton(onClick = { showExitWarning = true }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = AcademicOnPrimaryContainer)
                    }
                },
                actions = {
                    Surface(
                        color = Color(0xFFFFDBD0),
                        shape = RoundedCornerShape(99.dp),
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = AcademicPrimary, modifier = Modifier.size(16.dp))
                            Text(formatTime(timeRemainingSeconds), color = AcademicPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White.copy(alpha = 0.9f))
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Button
                    TextButton(
                        onClick = { if (currentQuestionIndex > 0) currentQuestionIndex-- },
                        enabled = currentQuestionIndex > 0,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("PREVIOUS", fontWeight = FontWeight.Bold, letterSpacing = 1.sp, fontSize = 13.sp)
                    }
                    
                    // Next Button
                    if (currentQuestionIndex < questions.size - 1) {
                        Button(
                            onClick = { currentQuestionIndex++ },
                            colors = ButtonDefaults.buttonColors(containerColor = AcademicPrimaryContainer),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Text("NEXT", fontWeight = FontWeight.Bold, letterSpacing = 1.sp, fontSize = 13.sp, color = Color.White)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                        }
                    } else {
                        // Submit Button Placeholder to maintain spacing or hide it if Submit is shown in content
                        Spacer(modifier = Modifier.width(100.dp))
                    }
                }
            }
        },
        containerColor = AcademicBackground
    ) { paddingValues ->
        val currentQuestion = questions[currentQuestionIndex]
        val scrollState = rememberScrollState()
        
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            // Header Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = AcademicPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(99.dp)
                    ) {
                        Text(
                            "QUESTION ${currentQuestionIndex + 1} OF ${questions.size}",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AcademicPrimary,
                            letterSpacing = 1.sp
                        )
                    }
                    
                    val progress = (currentQuestionIndex + 1).toFloat() / questions.size.toFloat()
                    val animatedProgress by animateFloatAsState(targetValue = progress)
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(99.dp)),
                        color = AcademicPrimary,
                        trackColor = AcademicSurfaceVariant
                    )
                }
                
                Spacer(Modifier.height(24.dp))
                
                Text(
                    text = currentQuestion.content,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1C1C),
                    lineHeight = 32.sp,
                    letterSpacing = (-0.5).sp
                )
            }
            
            Spacer(Modifier.height(32.dp))
            
            if (quiz.isEssay) {
                EssayQuestionContent(
                    question = currentQuestion,
                    answer = userAnswers[currentQuestion.id] ?: "",
                    onAnswerChange = { 
                        userAnswers = userAnswers.toMutableMap().apply { put(currentQuestion.id, it) }
                    }
                )
            } else {
                MultipleChoiceContent(
                    question = currentQuestion,
                    selectedOptionId = userAnswers[currentQuestion.id],
                    onOptionSelected = { optionId ->
                        userAnswers = userAnswers.toMutableMap().apply { put(currentQuestion.id, optionId) }
                    }
                )
            }
            
            Spacer(Modifier.height(48.dp))
            
            // Final Submit Button
            if (currentQuestionIndex == questions.size - 1) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onSubmit(userAnswers) },
                        colors = ButtonDefaults.buttonColors(containerColor = AcademicPrimaryContainer),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(64.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text("Submit Final Quiz", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = AcademicOnPrimaryContainer)
                        Spacer(Modifier.width(12.dp))
                        Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = AcademicOnPrimaryContainer)
                    }
                    Text(
                        "ACTION CANNOT BE UNDONE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 1.5.sp
                    )
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun MultipleChoiceContent(
    question: Question,
    selectedOptionId: String?,
    onOptionSelected: (String) -> Unit
) {
    question.choices.forEachIndexed { index, option ->
        val isSelected = selectedOptionId == option.id
        val optionLetter = ('A' + index).toString()
        
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) Color.White else AcademicSurfaceLow,
            border = if (isSelected) BorderStroke(2.dp, AcademicPrimary) else BorderStroke(1.dp, Color.Transparent),
            shadowElevation = if (isSelected) 6.dp else 0.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .clickable { onOptionSelected(option.id) }
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) AcademicPrimary else Color.White)
                        .border(1.dp, if (isSelected) Color.Transparent else Color(0xFFE0BFB6), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        optionLetter,
                        color = if (isSelected) Color.White else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(Modifier.width(16.dp))
                
                Text(
                    text = option.content,
                    fontSize = 17.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    modifier = Modifier.weight(1f),
                    color = if (isSelected) AcademicPrimary else Color(0xFF1A1C1C)
                )
                
                if (isSelected) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AcademicPrimary)
                }
            }
        }
    }
}

@Composable
fun EssayQuestionContent(
    question: Question,
    answer: String,
    onAnswerChange: (String) -> Unit
) {
    val wordCount = if (answer.isBlank()) 0 else answer.trim().split("\\s+".toRegex()).size
    val minWords = question.minWords ?: 500

    Surface(
        color = AcademicSurfaceLow,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                "Your Response",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            
            Spacer(Modifier.height(12.dp))
            
            OutlinedTextField(
                value = answer,
                onValueChange = onAnswerChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 300.dp),
                placeholder = { Text("Begin your essay here...", color = Color.LightGray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = AcademicPrimary,
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, lineHeight = 28.sp)
            )
            
            Spacer(Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = AcademicTertiary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(99.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = AcademicTertiary, modifier = Modifier.size(12.dp))
                        Text(
                            "DRAFT AUTOSAVED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = AcademicTertiary,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
                
                Surface(
                    color = AcademicSurfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append("Words: ")
                            withStyle(style = SpanStyle(color = AcademicPrimary, fontWeight = FontWeight.Bold)) {
                                append("$wordCount")
                            }
                            append(" / $minWords")
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun StudentQuizRoute(
    quizId: String,
    viewModel: com.example.skillforge.feature.student_courses.viewmodel.StudentQuizViewModel,
    onBack: () -> Unit,
    onSubmit: (Map<String, String>) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val submissionResult by viewModel.submissionResult.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    var userAnswersMap by remember { mutableStateOf<Map<String, String>?>(null) }

    LaunchedEffect(quizId) {
        viewModel.loadQuiz(quizId)
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = AcademicPrimary)
        }
    } else if (uiState.errorMessage != null) {
        Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(16.dp))
                Text(uiState.errorMessage ?: "Error loading quiz", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                Spacer(Modifier.height(24.dp))
                Button(onClick = { viewModel.loadQuiz(quizId) }, colors = ButtonDefaults.buttonColors(containerColor = AcademicPrimary)) {
                    Text("Retry")
                }
            }
        }
    } else if (uiState.quiz != null) {
        if (uiState.shuffledQuestions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Quiz, contentDescription = null, tint = AcademicPrimary, modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("This quiz has no questions yet.", fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = AcademicPrimary)) {
                        Text("Go Back")
                    }
                }
            }
        } else {
            var showSubmitConfirmation by remember { mutableStateOf(false) }

            if (showSubmitConfirmation) {
                AlertDialog(
                    onDismissRequest = { showSubmitConfirmation = false },
                    title = { Text("Submit Quiz?", fontWeight = FontWeight.Bold) },
                    text = { Text("Are you sure you want to submit your answers? You won't be able to change them later.") },
                    confirmButton = {
                        Button(
                            onClick = { 
                                showSubmitConfirmation = false
                                viewModel.submitQuiz(userAnswersMap ?: emptyMap()) 
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AcademicPrimary)
                        ) {
                            Text("Submit", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSubmitConfirmation = false }) {
                            Text("Cancel")
                        }
                    },
                    shape = RoundedCornerShape(24.dp)
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                StudentQuizScreen(
                    quiz = uiState.quiz!!,
                    questions = uiState.shuffledQuestions,
                    timeRemainingSeconds = uiState.timeRemainingSeconds,
                    isTimeUp = uiState.isTimeUp,
                    onBack = onBack,
                    onSubmit = { answers ->
                        userAnswersMap = answers
                        showSubmitConfirmation = true
                    }
                )

                if (isSubmitting) {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)), contentAlignment = Alignment.Center) {
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                modifier = Modifier.padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(color = AcademicPrimary)
                                Spacer(Modifier.height(16.dp))
                                Text("Submitting your quiz...", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                submissionResult?.let { result ->
                    AlertDialog(
                        onDismissRequest = { },
                        title = { 
                            Text(
                                if (uiState.quiz?.isEssay == true) "Quiz Submitted!" else (if (result.isPassed) "Quiz Passed!" else "Quiz Failed"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            ) 
                        },
                        text = { 
                            Column {
                                if (uiState.quiz?.isEssay == true) {
                                    Text("Your essay has been successfully submitted for review.", fontWeight = FontWeight.Medium)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Please wait for the instructor to grade your work. You will be notified once the results are available.", color = AcademicTertiary)
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            if (result.isPassed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                            contentDescription = null,
                                            tint = if (result.isPassed) Color(0xFF006972) else MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(48.dp)
                                        )
                                        Spacer(Modifier.width(16.dp))
                                        Column {
                                            Text("Score: ${result.score}%", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                            Text("Correct: ${result.correctAnswers} / ${result.totalQuestions}")
                                        }
                                    }
                                    if (!result.isPassed) {
                                        Spacer(Modifier.height(16.dp))
                                        Text("You didn't meet the passing score.", color = MaterialTheme.colorScheme.error)
                                    } else {
                                        Spacer(Modifier.height(16.dp))
                                        Text("Congratulations! This will be counted towards your course progress.", color = AcademicTertiary)
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = { 
                                    viewModel.resetSubmission()
                                    onSubmit(emptyMap()) // Always exit after submission
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AcademicPrimary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Done")
                            }
                        },
                        shape = RoundedCornerShape(28.dp),
                        containerColor = Color.White
                    )
                }
            }
        }
    }
}

