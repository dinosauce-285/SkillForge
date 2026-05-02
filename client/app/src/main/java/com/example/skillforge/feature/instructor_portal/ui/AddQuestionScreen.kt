package com.example.skillforge.feature.instructor_portal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillforge.core.designsystem.BackgroundColor
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.feature.instructor_portal.viewmodel.QuizBuilderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuestionScreen(
    viewModel: QuizBuilderViewModel,
    editQuestionId: String? = null,
    onBackClick: () -> Unit = {}
) {
    // Pre-fill if editing an existing question
    val existingQuestion = editQuestionId?.let { viewModel.getQuestionById(it) }

    var questionText by remember { mutableStateOf(existingQuestion?.content ?: "") }
    var options by remember {
        mutableStateOf(
            existingQuestion?.choices?.sortedBy { it.orderIndex }?.map { it.content }
                ?: listOf("", "", "", "")
        )
    }
    var selectedOptionIndex by remember {
        mutableStateOf(
            existingQuestion?.choices?.indexOfFirst { it.isCorrect }?.takeIf { it >= 0 } ?: 0
        )
    }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isEditing = editQuestionId != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditing) "Edit Question" else "Add Question",
                        fontWeight = FontWeight.Bold, fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryOrange)
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            // Validate
                            if (questionText.isBlank()) {
                                errorMessage = "Question text is required"
                                return@Button
                            }
                            val filledOptions = options.filter { it.isNotBlank() }
                            if (filledOptions.size < 2) {
                                errorMessage = "At least 2 options are required"
                                return@Button
                            }
                            if (selectedOptionIndex >= options.size || options[selectedOptionIndex].isBlank()) {
                                errorMessage = "Please select a valid correct answer"
                                return@Button
                            }

                            isSaving = true
                            errorMessage = null

                            if (isEditing) {
                                viewModel.updateQuestion(
                                    questionId = editQuestionId!!,
                                    content = questionText,
                                    choicesText = options.filter { it.isNotBlank() },
                                    correctIndex = selectedOptionIndex
                                )
                            } else {
                                viewModel.addQuestion(
                                    content = questionText,
                                    choicesText = options.filter { it.isNotBlank() },
                                    correctIndex = selectedOptionIndex
                                )
                            }
                            onBackClick()
                        },
                        enabled = !isSaving,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(if (isEditing) "Update" else "Save", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues).fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Error message
            if (errorMessage != null) {
                item {
                    Surface(
                        color = Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(errorMessage!!, color = Color(0xFFE53935), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Header
            item {
                Column {
                    Surface(color = PrimaryOrange.copy(alpha = 0.1f), shape = RoundedCornerShape(100.dp)) {
                        Text(
                            if (isEditing) "EDIT QUESTION" else "NEW QUESTION",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryOrange, letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        if (isEditing) "Update Your Question" else "Craft Your Question",
                        fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black
                    )
                }
            }

            // Question text
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White, shape = RoundedCornerShape(16.dp), shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("QUESTION TEXT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        TextField(
                            value = questionText,
                            onValueChange = { questionText = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter your question here...", color = Color.Gray.copy(alpha = 0.6f)) },
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF3F3F4), unfocusedContainerColor = Color(0xFFF3F3F4),
                                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                            ),
                            minLines = 3
                        )
                    }
                }
            }

            // Answer choices
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White, shape = RoundedCornerShape(16.dp), shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("ANSWER CHOICES", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                            Text("Tap radio to mark correct", fontSize = 10.sp, color = PrimaryOrange, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        options.forEachIndexed { index, option ->
                            val isCorrect = index == selectedOptionIndex
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .border(
                                        width = if (isCorrect) 2.dp else 1.dp,
                                        color = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .background(
                                        if (isCorrect) Color(0xFFE8F5E9) else Color.Transparent,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isCorrect,
                                    onClick = { selectedOptionIndex = index },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = Color(0xFF4CAF50),
                                        unselectedColor = Color.Gray
                                    )
                                )
                                TextField(
                                    value = option,
                                    onValueChange = { newValue ->
                                        options = options.toMutableList().also { it[index] = newValue }
                                    },
                                    modifier = Modifier.weight(1f),
                                    placeholder = { Text("Option ${index + 1}", color = Color.Gray.copy(alpha = 0.5f)) },
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    singleLine = true
                                )
                                if (options.size > 2) {
                                    IconButton(
                                        onClick = {
                                            options = options.toMutableList().also { it.removeAt(index) }
                                            if (selectedOptionIndex >= options.size - 1) selectedOptionIndex = 0
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Gray, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(
                            onClick = { options = options + "" },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.textButtonColors(contentColor = PrimaryOrange)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Option", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}
