package com.example.skillforge.feature.student_courses.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.PlayLesson
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.domain.model.Quiz
import com.example.skillforge.domain.model.Question
import com.example.skillforge.domain.model.AnswerChoice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentQuizScreen(
    quiz: Quiz?,
    onBack: () -> Unit,
    onSubmit: (Map<String, String>) -> Unit
) {
    if (quiz == null || quiz.questions.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Quiz not available or has no questions.")
        }
        return
    }

    val questions = quiz.questions!!
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswers by remember { mutableStateOf(mapOf<String, String>()) } // questionId to optionId
    var showExitWarning by remember { mutableStateOf(false) }

    // Timer logic
    var timeRemainingSeconds by remember { mutableStateOf(quiz.timeLimit * 60) }
    
    LaunchedEffect(quiz.timeLimit) {
        while (timeRemainingSeconds > 0) {
            delay(1000)
            timeRemainingSeconds--
        }
        if (timeRemainingSeconds <= 0) {
            onSubmit(selectedAnswers)
        }
    }

    val formatTime = { seconds: Int ->
        val m = seconds / 60
        val s = seconds % 60
        String.format("%02d:%02d", m, s)
    }

    if (showExitWarning) {
        AlertDialog(
            onDismissRequest = { showExitWarning = false },
            title = { Text("Exit Quiz?") },
            text = { Text("Are you sure you want to exit? Your progress will not be saved.") },
            confirmButton = {
                TextButton(onClick = {
                    showExitWarning = false
                    onBack()
                }) {
                    Text("Exit", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitWarning = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("Academic Atelier", fontWeight = FontWeight.ExtraBold, color = Color(0xFF783925)) 
                },
                navigationIcon = {
                    IconButton(onClick = { showExitWarning = true }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF783925))
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
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFF3A0B00), modifier = Modifier.size(16.dp))
                            Text(formatTime(timeRemainingSeconds), color = Color(0xFF3A0B00), fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White.copy(alpha = 0.9f))
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { if (currentQuestionIndex > 0) currentQuestionIndex-- },
                        enabled = currentQuestionIndex > 0
                    ) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("PREVIOUS", fontWeight = FontWeight.Bold)
                    }
                    
                    Button(
                        onClick = { if (currentQuestionIndex < questions.size - 1) currentQuestionIndex++ },
                        enabled = currentQuestionIndex < questions.size - 1,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Text("NEXT", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        },
        containerColor = Color(0xFFF9F9F9)
    ) { paddingValues ->
        val currentQuestion = questions[currentQuestionIndex]
        
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Progress Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("CURRENT PROGRESS", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text(
                        "Question ${currentQuestionIndex + 1} of ${questions.size}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryOrange
                    )
                }
                val progress = (currentQuestionIndex + 1).toFloat() / questions.size.toFloat()
                Text("${(progress * 100).toInt()}%", color = PrimaryOrange, fontWeight = FontWeight.Bold)
            }
            
            Spacer(Modifier.height(16.dp))
            
            val animatedProgress by animateFloatAsState(targetValue = (currentQuestionIndex + 1).toFloat() / questions.size.toFloat())
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(99.dp)),
                color = PrimaryOrange,
                trackColor = Color(0xFFEEEEEE)
            )
            
            Spacer(Modifier.height(32.dp))
            
            // Question Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = currentQuestion.content,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(32.dp),
                    lineHeight = 30.sp
                )
            }
            
            Spacer(Modifier.height(32.dp))
            
            // Options
            currentQuestion.choices.forEachIndexed { index, option ->
                val isSelected = selectedAnswers[currentQuestion.id] == option.id
                val optionLetter = ('A' + index).toString()
                
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) Color.White else Color(0xFFF3F3F4),
                    border = if (isSelected) BorderStroke(2.dp, PrimaryOrange) else BorderStroke(1.dp, Color.Transparent),
                    shadowElevation = if (isSelected) 4.dp else 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clickable {
                            selectedAnswers = selectedAnswers.toMutableMap().apply {
                                put(currentQuestion.id, option.id)
                            }
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryOrange else Color.White)
                                .border(1.dp, if (isSelected) Color.Transparent else Color(0xFFE0BFB6), RoundedCornerShape(8.dp)),
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
                            fontSize = 16.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = PrimaryOrange)
                        }
                    }
                }
            }
            
            Spacer(Modifier.weight(1f))
            
            // Submit Button
            AnimatedVisibility(visible = currentQuestionIndex == questions.size - 1) {
                Button(
                    onClick = { onSubmit(selectedAnswers) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .height(56.dp)
                ) {
                    Text("Submit Quiz", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color(0xFF641800))
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.Publish, contentDescription = null, tint = Color(0xFF641800))
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

    LaunchedEffect(quizId) {
        viewModel.loadQuiz(quizId)
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryOrange)
        }
    } else if (uiState.errorMessage != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(uiState.errorMessage ?: "Error loading quiz", color = MaterialTheme.colorScheme.error)
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            StudentQuizScreen(
                quiz = uiState.quiz,
                onBack = onBack,
                onSubmit = { answers ->
                    viewModel.submitQuiz(answers)
                }
            )

            if (isSubmitting) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryOrange)
                }
            }

            submissionResult?.let { result ->
                AlertDialog(
                    onDismissRequest = { },
                    title = { Text(if (result.isPassed) "Quiz Passed!" else "Quiz Failed") },
                    text = { 
                        Column {
                            Text("Score: ${result.score}%")
                            Text("Correct Answers: ${result.correctAnswers} / ${result.totalQuestions}")
                            if (!result.isPassed) {
                                Text("\nYou didn't meet the passing score. Would you like to try again or exit?", color = MaterialTheme.colorScheme.error)
                            } else {
                                Text("\nCongratulations! This will be counted towards your course progress.", color = Color(0xFF006972))
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { 
                            viewModel.resetSubmission()
                            if (result.isPassed) {
                                onSubmit(emptyMap()) // Trigger navigation back in MainActivity
                            } else {
                                viewModel.loadQuiz(quizId) // Reload quiz to try again
                            }
                        }) {
                            Text(if (result.isPassed) "Continue" else "Try Again")
                        }
                    },
                    dismissButton = {
                        if (!result.isPassed) {
                            TextButton(onClick = {
                                viewModel.resetSubmission()
                                onSubmit(emptyMap()) // Exit
                            }) {
                                Text("Exit")
                            }
                        }
                    }
                )
            }
        }
    }
}
