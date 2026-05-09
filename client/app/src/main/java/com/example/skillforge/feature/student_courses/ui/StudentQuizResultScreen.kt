package com.example.skillforge.feature.student_courses.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.foundation.border
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
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.feature.student_courses.viewmodel.StudentQuizResultState
import com.example.skillforge.feature.student_courses.viewmodel.StudentQuizResultViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentQuizResultScreen(
    viewModel: StudentQuizResultViewModel,
    onBack: () -> Unit,
    onRetake: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Result", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF9F9F9)
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = uiState) {
                is StudentQuizResultState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryOrange)
                }
                is StudentQuizResultState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = Color.Red, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadResult() }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)) {
                            Text("Retry")
                        }
                    }
                }
                is StudentQuizResultState.Success -> {
                    val details = state.details
                    val isPassed = details.isPassed == true
                    
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Pass/Fail Header
                        Surface(
                            modifier = Modifier.size(100.dp),
                            shape = CircleShape,
                            color = if (isPassed) Color(0xFFE0F2F1) else Color(0xFFFFEBEE)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isPassed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = if (isPassed) Color(0xFF00796B) else Color(0xFFD32F2F)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = if (isPassed) "Congratulations!" else "Keep Improving!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isPassed) Color(0xFF00796B) else Color(0xFFD32F2F)
                        )
                        
                        Text(
                            text = if (isPassed) "You passed this quiz." else "You did not meet the passing score.",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Score Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("YOUR SCORE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${details.score?.toInt() ?: 0}%",
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Black,
                                    color = PrimaryOrange
                                )
                                Text("Passing Score: ${details.quiz.passingScore.toInt()}%", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Instructor Feedback
                        if (!details.instructorFeedback.isNullOrBlank()) {
                            Text(
                                "Instructor's Feedback",
                                modifier = Modifier.fillMaxWidth(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Evaluation Note", fontWeight = FontWeight.Bold, color = AcademicOnPrimaryContainer)
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(details.instructorFeedback, lineHeight = 24.sp, color = Color.DarkGray)
                                }
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                        
                        // Answer Breakdown
                        Text(
                            "Question Breakdown",
                            modifier = Modifier.fillMaxWidth(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        details.answers.forEachIndexed { index, answer ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Question ${index + 1}", fontWeight = FontWeight.Bold, color = PrimaryOrange)
                                        Surface(
                                            color = Color(0xFFF5F5F5),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                "${answer.pointsAwarded?.toInt() ?: 0} / ${answer.question.points} pts",
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(answer.question.content, fontWeight = FontWeight.Medium)
                                    
                                    if (!answer.essayAnswer.isNullOrBlank()) {
                                        Text("Your Answer:", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Surface(
                                            color = Color(0xFFFAFAFA),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                answer.essayAnswer ?: "(No answer provided)",
                                                modifier = Modifier.padding(12.dp),
                                                fontSize = 14.sp,
                                                lineHeight = 22.sp
                                            )
                                        }
                                    } else {
                                        val choices = answer.question.choices ?: emptyList()
                                        choices.sortedBy { it.orderIndex }.forEach { choice ->
                                            val isSelected = choice.id == answer.selectedChoiceId
                                            val isCorrect = choice.isCorrect
                                            
                                            val backgroundColor = when {
                                                isSelected && isCorrect -> Color(0xFFE8F5E9)
                                                isSelected && !isCorrect -> Color(0xFFFFEBEE)
                                                !isSelected && isCorrect -> Color(0xFFF1F8E9)
                                                else -> Color.Transparent
                                            }
                                            
                                            val borderColor = when {
                                                isSelected && isCorrect -> Color(0xFF4CAF50)
                                                isSelected && !isCorrect -> Color(0xFFF44336)
                                                !isSelected && isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.3f)
                                                else -> Color(0xFFEEEEEE)
                                            }

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp)
                                                    .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                                                    .background(backgroundColor, RoundedCornerShape(8.dp))
                                                    .padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = isSelected,
                                                    onClick = null,
                                                    enabled = false,
                                                    colors = RadioButtonDefaults.colors(
                                                        selectedColor = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336),
                                                        unselectedColor = Color.LightGray,
                                                        disabledSelectedColor = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336),
                                                        disabledUnselectedColor = Color.LightGray
                                                    )
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text(
                                                    text = choice.content,
                                                    modifier = Modifier.weight(1f),
                                                    fontSize = 14.sp,
                                                    color = if (isSelected) Color.Black else Color.DarkGray,
                                                    fontWeight = if (isSelected || isCorrect) FontWeight.Bold else FontWeight.Normal
                                                )
                                                if (isCorrect) {
                                                    Icon(
                                                        Icons.Default.CheckCircle, 
                                                        contentDescription = "Correct", 
                                                        tint = Color(0xFF4CAF50), 
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                } else if (isSelected && !isCorrect) {
                                                    Icon(
                                                        Icons.Default.Cancel, 
                                                        contentDescription = "Incorrect", 
                                                        tint = Color(0xFFF44336), 
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                        
                                        if (answer.selectedChoiceId == null) {
                                            Text(
                                                "No option selected",
                                                color = Color.Red,
                                                fontSize = 12.sp,
                                                modifier = Modifier.padding(top = 8.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))

                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedButton(
                                onClick = onBack,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Back to Course")
                            }
                            
                            Button(
                                onClick = { onRetake(details.quizId) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                            ) {
                                Text("Retake Quiz")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
            }
        }
    }
}
