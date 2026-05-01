package com.example.skillforge.feature.instructor_portal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ListAlt
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
    questionId: String? = null,
    onBackClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {}
) {
    val isEditMode = questionId != null
    val existingQuestion = remember(questionId) {
        questionId?.let { viewModel.getQuestionById(it) }
    }

    var questionText by remember(existingQuestion) {
        mutableStateOf(existingQuestion?.content ?: "")
    }
    var options by remember(existingQuestion) {
        mutableStateOf(
            existingQuestion?.choices?.sortedBy { it.orderIndex }?.map { it.content }
                ?: listOf("", "")
        )
    }
    var selectedOptionIndex by remember(existingQuestion) {
        mutableStateOf(
            existingQuestion?.choices?.indexOfFirst { it.isCorrect }?.takeIf { it >= 0 } ?: 0
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Edit Question" else "Add Question",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
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
                            val filteredOptions = options.filter { it.isNotBlank() }
                            if (isEditMode && questionId != null) {
                                viewModel.updateQuestion(questionId, questionText, filteredOptions, selectedOptionIndex)
                            } else {
                                viewModel.addQuestion(questionText, filteredOptions, selectedOptionIndex)
                            }
                            onSaveClick()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            if (isEditMode) "Save" else "Publish",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            QuizBuilderBottomBar(
                selectedTab = 0,
                onTabSelected = {
                    if (it == 1) {
                        onNavigateToSettings()
                    }
                }
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Question Text Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "The Question",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    TextField(
                        value = questionText,
                        onValueChange = { questionText = it },
                        placeholder = { Text("Type your question here...", color = Color.Gray.copy(alpha = 0.6f)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        minLines = 3
                    )
                }
            }

            // Question Type Section (Locked to Multiple Choice for now)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Type",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(PrimaryOrange.copy(alpha = 0.1f), RoundedCornerShape(100.dp))
                            .border(2.dp, PrimaryOrange, RoundedCornerShape(100.dp))
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = null, tint = PrimaryOrange)
                        Text(
                            text = "Multiple Choice",
                            fontWeight = FontWeight.Bold,
                            color = PrimaryOrange,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Options Section
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "Options",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "SELECT CORRECT ANSWER",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryOrange,
                            letterSpacing = 1.sp
                        )
                    }

                    options.forEachIndexed { index, option ->
                        OptionItem(
                            index = index,
                            value = option,
                            isSelected = selectedOptionIndex == index,
                            onValueChange = { newValue ->
                                options = options.toMutableList().apply { this[index] = newValue }
                            },
                            onSelect = { selectedOptionIndex = index }
                        )
                    }

                    // Add Option Button
                    OutlinedButton(
                        onClick = { options = options + "" },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.LightGray.copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Option", fontWeight = FontWeight.Medium)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun OptionItem(
    index: Int,
    value: String,
    isSelected: Boolean,
    onValueChange: (String) -> Unit,
    onSelect: () -> Unit
) {
    val letter = ('A' + index).toString()
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.weight(1f),
            color = Color.White,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 1.dp
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = letter,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryOrange,
                    fontSize = 16.sp
                )
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = { Text("Add option text...", fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }

        IconButton(
            onClick = onSelect,
            modifier = Modifier
                .size(40.dp)
                .background(
                    if (isSelected) PrimaryOrange else Color.Transparent,
                    CircleShape
                )
                .border(2.dp, if (isSelected) PrimaryOrange else Color.LightGray.copy(alpha = 0.5f), CircleShape)
        ) {
            if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
            }
        }
    }
}
