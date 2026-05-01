package com.example.skillforge.feature.instructor_portal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillforge.core.designsystem.BackgroundColor
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.core.designsystem.SkillforgeTheme

@Composable
fun QuizSettingsScreen(
    initialTitle: String = "Untitled Quiz",
    initialPassMark: String = "80",
    initialTimeLimit: String = "45",
    initialShuffle: Boolean = true,
    onSaveSettings: (String, String, String, Boolean) -> Unit = { _, _, _, _ -> },
    onResetDefaults: () -> Unit = {},
    onDeleteQuiz: () -> Unit = {}
) {
    var title by remember { mutableStateOf(initialTitle) }
    var passMark by remember { mutableStateOf(initialPassMark) }
    var timeLimit by remember { mutableStateOf(initialTimeLimit) }
    var shuffleQuestions by remember { mutableStateOf(initialShuffle) }
    var allowRetakes by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        contentPadding = PaddingValues(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Editorial Intro Section
        item {
            Column(modifier = Modifier.padding(bottom = 8.dp)) {
                Surface(
                    color = PrimaryOrange.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text(
                        text = "ACADEMIC CONFIGURATION",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryOrange,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Define the Challenge",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.Black,
                    lineHeight = 36.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Fine-tune the parameters for your assessment. Precision in settings creates excellence in results.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    lineHeight = 20.sp
                )
            }
        }

        // Numerical Inputs Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    SettingInput(
                        label = "Quiz Title",
                        value = title,
                        onValueChange = { title = it },
                        suffix = "",
                        isNumber = false
                    )
                    SettingInput(
                        label = "Pass mark percentage",
                        value = passMark,
                        onValueChange = { passMark = it },
                        suffix = "%",
                        isNumber = true
                    )
                    SettingInput(
                        label = "Time limit (minutes)",
                        value = timeLimit,
                        onValueChange = { timeLimit = it },
                        suffix = "min",
                        isNumber = true
                    )
                }
            }
        }

        // Toggles Card
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF0F0F0))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    ToggleSetting(
                        title = "Shuffle questions",
                        description = "Present items in a random order for each student to prevent collaboration.",
                        checked = shuffleQuestions,
                        onCheckedChange = { shuffleQuestions = it }
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 20.dp), color = Color(0xFFF5F5F5))
                    ToggleSetting(
                        title = "Allow retakes",
                        description = "Permit learners to attempt the quiz multiple times for mastery.",
                        checked = allowRetakes,
                        onCheckedChange = { allowRetakes = it }
                    )
                }
            }
        }

        // Mastery Note
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFE0F2F1).copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00796B).copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = Color(0xFF00796B),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Higher pass marks (above 85%) are recommended for professional certification courses to ensure rigorous standards.",
                        fontSize = 12.sp,
                        color = Color(0xFF00796B),
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Action Buttons
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { onSaveSettings(title, passMark, timeLimit, shuffleQuestions) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                TextButton(
                    onClick = onResetDefaults,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = PrimaryOrange)
                ) {
                    Text("Reset to Defaults", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                OutlinedButton(
                    onClick = onDeleteQuiz,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete Quiz", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun SettingInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String,
    isNumber: Boolean = true
) {
    Column {
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Box(contentAlignment = Alignment.CenterEnd) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF3F3F4),
                    unfocusedContainerColor = Color(0xFFF3F3F4),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )
            Text(
                text = suffix,
                modifier = Modifier.padding(end = 16.dp),
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ToggleSetting(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 12.sp,
                color = Color.Gray,
                lineHeight = 18.sp
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PrimaryOrange,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFE2E2E2),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun QuizSettingsScreenPreview() {
    SkillforgeTheme {
        QuizSettingsScreen()
    }
}
