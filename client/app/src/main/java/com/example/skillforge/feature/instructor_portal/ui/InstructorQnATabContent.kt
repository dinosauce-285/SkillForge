package com.example.skillforge.feature.instructor_portal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.skillforge.core.designsystem.*
import com.example.skillforge.feature.instructor_portal.viewmodel.InstructorQnAState
import com.example.skillforge.feature.instructor_portal.viewmodel.InstructorQnAViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QnATabContent(viewModel: InstructorQnAViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val unansweredOnly by viewModel.unansweredOnly.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    val selectedCourseId by viewModel.selectedCourseId.collectAsState()
    val selectedCourseName = courses.find { it.id == selectedCourseId }?.title ?: "All Courses"

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Filters Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text(selectedCourseName, color = PrimaryOrange)
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text("All Courses") },
                        onClick = {
                            viewModel.setCourseFilter(null)
                            expanded = false
                        }
                    )
                    courses.forEach { course ->
                        DropdownMenuItem(
                            text = { Text(course.title) },
                            onClick = {
                                viewModel.setCourseFilter(course.id)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Unanswered Only", fontSize = 14.sp, color = TextSecondaryColor)
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = unansweredOnly,
                    onCheckedChange = { viewModel.toggleUnansweredOnly() },
                    colors = SwitchDefaults.colors(checkedThumbColor = PrimaryOrange, checkedTrackColor = PrimaryOrangeLight)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        var isRefreshing by remember { mutableStateOf(false) }

        LaunchedEffect(uiState) {
            if (uiState !is InstructorQnAState.Loading) {
                isRefreshing = false
            }
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.fetchDiscussions()
            },
            modifier = Modifier.fillMaxSize()
        ) {
            when (uiState) {
                is InstructorQnAState.Loading -> {
                    if (!isRefreshing) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = PrimaryOrange)
                        }
                    }
                }
            is InstructorQnAState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text((uiState as InstructorQnAState.Error).message, color = Color.Red)
                }
            }
            is InstructorQnAState.Success -> {
                val discussions = (uiState as InstructorQnAState.Success).discussions
                if (discussions.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No questions found.", color = TextSecondaryColor)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(discussions) { discussion ->
                            InstructorQnACard(
                                discussion = discussion,
                                onReplySubmit = { content ->
                                    viewModel.replyToQuestion(
                                        discussionId = discussion.id,
                                        lessonId = discussion.lessonId,
                                        content = content,
                                        onSuccess = { /* Handle success visually if needed */ }
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun InstructorQnACard(
    discussion: com.example.skillforge.data.remote.InstructorDiscussionDto,
    onReplySubmit: (String) -> Unit
) {
    var replyText by remember { mutableStateOf("") }
    var showReplyBox by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Context header (Course > Lesson)
            Text(
                text = "${discussion.lesson.course.title} > ${discussion.lesson.title}",
                fontSize = 12.sp,
                color = PrimaryOrange,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // User Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = discussion.user.avatarUrl ?: "https://ui-avatars.com/api/?name=${discussion.user.fullName}",
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(32.dp).clip(CircleShape).border(1.dp, Color.LightGray, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(discussion.user.fullName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimaryColor)
                    Text(discussion.createdAt.take(10), fontSize = 10.sp, color = TextSecondaryColor)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(discussion.content, fontSize = 14.sp, color = TextPrimaryColor)

            Spacer(modifier = Modifier.height(12.dp))

            // Display existing replies
            if (!discussion.replies.isNullOrEmpty()) {
                Column(modifier = Modifier.padding(start = 16.dp, top = 8.dp).fillMaxWidth().background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)).padding(8.dp)) {
                    discussion.replies.forEach { reply ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(reply.user.fullName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryOrange)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(reply.content, fontSize = 12.sp, color = TextPrimaryColor)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            if (!showReplyBox) {
                OutlinedButton(
                    onClick = { showReplyBox = true },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryOrange),
                    border = BorderStroke(1.dp, PrimaryOrange)
                ) {
                    Text("Reply", fontSize = 12.sp)
                }
            } else {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Write your reply...", fontSize = 12.sp) },
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryOrange,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (replyText.isNotBlank()) {
                                onReplySubmit(replyText)
                                replyText = ""
                                showReplyBox = false
                            }
                        },
                        modifier = Modifier.background(PrimaryOrange, CircleShape).size(40.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = SurfaceColor, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}
