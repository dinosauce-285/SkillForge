package com.example.skillforge.feature.instructor_portal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.feature.instructor_portal.viewmodel.GradingState
import com.example.skillforge.feature.instructor_portal.viewmodel.GradingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillforgeGradingScreen(
    viewModel: GradingViewModel,
    onBack: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var questionScores by remember { mutableStateOf(mapOf<String, String>()) }
    var feedbackText by remember { mutableStateOf("") }

    val isGraded = (uiState as? GradingState.Success)?.details?.status == "GRADED"
    val isEditable = !isGraded

    LaunchedEffect(uiState) {
        if (uiState is GradingState.SubmitSuccess) {
            onSubmitSuccess()
        }
        if (uiState is GradingState.Success) {
            val details = (uiState as GradingState.Success).details
            questionScores = details.answers.associate { it.question.id to (it.pointsAwarded?.toString() ?: "") }
            feedbackText = details.instructorFeedback ?: ""
        }
    }
    
    val totalScore = questionScores.values.sumOf { it.toDoubleOrNull() ?: 0.0 }.toFloat()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        val title = (uiState as? GradingState.Success)?.details?.quiz?.title ?: "Grading"
                        val student = (uiState as? GradingState.Success)?.details?.student?.fullName ?: ""
                        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        if (student.isNotEmpty()) {
                            Text("Evaluating: $student", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    val initial = (uiState as? GradingState.Success)?.details?.student?.fullName?.take(2)?.uppercase() ?: "AJ"
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFFF7043),
                        modifier = Modifier.size(40.dp).padding(end = 8.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(initial, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF9F9F9)
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = uiState) {
                is GradingState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryOrange)
                }
                is GradingState.Error -> {
                    Text(state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center).padding(16.dp))
                }
                is GradingState.Success, is GradingState.SubmitSuccess -> {
                    val details = (uiState as? GradingState.Success)?.details ?: (uiState as? GradingState.SubmitSuccess)?.let { null }
                    
                    if (details != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            // Questions and Answers
                            details.quiz.questions.forEachIndexed { index, question ->
                                val studentAnswer = details.answers.find { it.question.id == question.id }
                                
                                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                    // Question Card
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        shape = RoundedCornerShape(12.dp),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(24.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                Icon(Icons.Default.AutoStories, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(18.dp))
                                                Text("QUESTION ${index + 1}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = PrimaryOrange)
                                            }
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text(
                                                text = question.content,
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                lineHeight = 28.sp
                                            )
                                        }
                                    }

                                    // Answer
                                    Column {
                                        Text("Student Submission", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Surface(
                                            modifier = Modifier.fillMaxWidth(),
                                            color = Color(0xFFEEEEEE),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                text = studentAnswer?.essayAnswer ?: "No response provided",
                                                modifier = Modifier.padding(24.dp),
                                                lineHeight = 32.sp,
                                                fontSize = 16.sp
                                            )
                                        }
                                    }
                                    
                                    // Question Score
                                    val currentScore = questionScores[question.id]?.toFloatOrNull() ?: 0f
                                    val isScoreInvalid = currentScore > question.points

                                    OutlinedTextField(
                                        value = questionScores[question.id] ?: "",
                                        onValueChange = { if (isEditable) questionScores = questionScores + (question.id to it) },
                                        enabled = isEditable,
                                        modifier = Modifier.fillMaxWidth(),
                                        label = { Text("Score for this question") },
                                        suffix = { Text("/ ${question.points}") },
                                        isError = isScoreInvalid,
                                        supportingText = if (isScoreInvalid) {
                                            { Text("Score cannot exceed ${question.points} points") }
                                        } else null,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            unfocusedContainerColor = Color.White,
                                            focusedContainerColor = Color.White,
                                            unfocusedBorderColor = Color.LightGray,
                                            focusedBorderColor = PrimaryOrange,
                                            errorBorderColor = Color.Red
                                        )
                                    )
                                }
                            }

                            // Submitted At Info
                            val formattedDate = remember(details.endTime, details.startTime) {
                                try {
                                    val dateStr = details.endTime ?: details.startTime
                                    val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US)
                                    inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
                                    val date = inputFormat.parse(dateStr)
                                    if (date != null) {
                                        val outputFormat = java.text.SimpleDateFormat("MMM dd, yyyy • HH:mm", java.util.Locale.US)
                                        outputFormat.format(date)
                                    } else {
                                        details.endTime ?: details.startTime
                                    }
                                } catch (e: Exception) {
                                    details.endTime ?: details.startTime
                                }
                            }
                            Text(
                                text = "Submitted on $formattedDate",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                color = Color.Gray,
                                fontSize = 12.sp
                            )

                            // Grading Panel
                            var showConfirmDialog by remember { mutableStateOf(false) }
                            
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F3F4)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Text("Evaluation", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(24.dp))
                                    
                                    Text("Total Score", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "$totalScore / ${details.quiz.questions.sumOf { it.points }}",
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Black,
                                        color = PrimaryOrange,
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Text("Feedback & Comments", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    OutlinedTextField(
                                        value = feedbackText,
                                        onValueChange = { feedbackText = it },
                                        enabled = isEditable,
                                        modifier = Modifier.fillMaxWidth().height(200.dp),
                                        placeholder = { Text("Provide constructive criticism...") },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            unfocusedContainerColor = Color.White,
                                            focusedContainerColor = Color.White,
                                            unfocusedBorderColor = Color.Transparent,
                                            focusedBorderColor = PrimaryOrange
                                        )
                                    )

                                    if (isEditable) {
                                        Spacer(modifier = Modifier.height(24.dp))

                                        Button(
                                            onClick = { showConfirmDialog = true },
                                            modifier = Modifier.fillMaxWidth().height(56.dp),
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                                        ) {
                                            Text("Submit Grade", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Text(
                                            "Evaluating status: GRADED",
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF006972)
                                        )
                                    }
                                }
                            }

                            if (showConfirmDialog) {
                                AlertDialog(
                                    onDismissRequest = { showConfirmDialog = false },
                                    title = { Text("Confirm Grade") },
                                    text = { Text("Are you sure you want to submit this grade? This will finalize the evaluation for this student and cannot be edited later.") },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                val allQuestionsGraded = details.quiz.questions.all { q ->
                                                    questionScores[q.id]?.isNotBlank() == true
                                                }
                                                
                                                if (!allQuestionsGraded) {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("Please award points to all questions")
                                                    }
                                                    return@TextButton
                                                }

                                                val hasInvalidScores = details.quiz.questions.any { q ->
                                                    val score = questionScores[q.id]?.toFloatOrNull() ?: 0f
                                                    score > q.points
                                                }

                                                if (hasInvalidScores) {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("Some scores exceed the maximum points allowed")
                                                    }
                                                    return@TextButton
                                                }

                                                if (feedbackText.isBlank()) {
                                                    scope.launch {
                                                        snackbarHostState.showSnackbar("Feedback is required for essay grading")
                                                    }
                                                    return@TextButton
                                                }

                                                showConfirmDialog = false
                                                val grades = details.quiz.questions.map { question ->
                                                    com.example.skillforge.data.remote.dto.QuestionGradeDto(
                                                        questionId = question.id,
                                                        points = questionScores[question.id]?.toFloatOrNull() ?: 0f
                                                    )
                                                }
                                                viewModel.submitGrade(grades, feedbackText)
                                            }
                                        ) {
                                            Text("Submit", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showConfirmDialog = false }) {
                                            Text("Cancel")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
