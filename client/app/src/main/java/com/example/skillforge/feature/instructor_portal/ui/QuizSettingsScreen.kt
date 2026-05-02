package com.example.skillforge.feature.instructor_portal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillforge.core.designsystem.BackgroundColor
import com.example.skillforge.core.designsystem.PrimaryOrange

@Composable
fun QuizSettingsScreen(
    initialTitle: String = "",
    initialTimeLimit: String = "45",
    initialPassMark: String = "80",
    initialShuffle: Boolean = false,
    onSaveSettings: (title: String, timeLimit: String, passingScore: String, shuffle: Boolean) -> Unit = { _, _, _, _ -> },
    onDeleteQuiz: () -> Unit = {}
) {
    var title by remember { mutableStateOf(initialTitle) }
    var passMark by remember { mutableStateOf(initialPassMark) }
    var timeLimit by remember { mutableStateOf(initialTimeLimit) }
    var shuffleQuestions by remember { mutableStateOf(initialShuffle) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Quiz", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this quiz and all its questions? This action cannot be undone.") },
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
        modifier = Modifier.fillMaxSize().background(BackgroundColor),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        item {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Surface(color = PrimaryOrange.copy(alpha = 0.1f), shape = RoundedCornerShape(100.dp)) {
                    Text(
                        text = "ACADEMIC CONFIGURATION",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryOrange, letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Define the Challenge", fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black, lineHeight = 36.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Fine-tune the parameters for your assessment. Precision in settings creates excellence in results.",
                    fontSize = 14.sp, color = Color.Gray, lineHeight = 20.sp
                )
            }
        }

        // Title input
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White, shape = RoundedCornerShape(16.dp), shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    SettingInput(label = "Quiz title", value = title, onValueChange = { title = it }, suffix = "", isNumber = false)
                }
            }
        }

        // Numerical inputs
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White, shape = RoundedCornerShape(16.dp), shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    SettingInput(label = "Pass mark percentage", value = passMark, onValueChange = { passMark = it }, suffix = "%")
                    SettingInput(label = "Time limit (minutes)", value = timeLimit, onValueChange = { timeLimit = it }, suffix = "min")
                }
            }
        }

        // Toggles
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White, shape = RoundedCornerShape(16.dp), shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    ToggleSetting(
                        title = "Shuffle questions",
                        description = "Present items in a random order for each student to prevent collaboration.",
                        checked = shuffleQuestions,
                        onCheckedChange = { shuffleQuestions = it }
                    )
                }
            }
        }

        // Mastery Note
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFE0F2F1).copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00796B).copy(alpha = 0.2f))
            ) {
                Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF00796B), modifier = Modifier.size(20.dp))
                    Text(
                        "Higher pass marks (above 85%) are recommended for professional certification courses.",
                        fontSize = 12.sp, color = Color(0xFF00796B), lineHeight = 18.sp, fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Action buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { onSaveSettings(title, timeLimit, passMark, shuffleQuestions) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE53935)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935))
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete Quiz", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
fun SettingInput(label: String, value: String, onValueChange: (String) -> Unit, suffix: String, isNumber: Boolean = true) {
    Column {
        Text(label.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 8.dp))
        Box(contentAlignment = Alignment.CenterEnd) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF3F3F4), unfocusedContainerColor = Color(0xFFF3F3F4),
                    focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )
            if (suffix.isNotEmpty()) {
                Text(suffix, modifier = Modifier.padding(end = 16.dp), fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ToggleSetting(title: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, fontSize = 12.sp, color = Color.Gray, lineHeight = 18.sp)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White, checkedTrackColor = PrimaryOrange,
                uncheckedThumbColor = Color.White, uncheckedTrackColor = Color(0xFFE2E2E2),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
