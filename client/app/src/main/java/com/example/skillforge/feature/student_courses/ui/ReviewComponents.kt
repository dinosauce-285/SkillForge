package com.example.skillforge.feature.student_courses.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skillforge.core.designsystem.PrimaryOrange

@Composable
fun InteractiveStarRating(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Default.Star else Icons.Outlined.StarOutline,
                contentDescription = "$i Star",
                tint = if (i <= rating) Color(0xFFFFC107) else Color.Gray,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onRatingChange(i) }
                    .padding(4.dp)
            )
        }
    }
}

@Composable
fun WriteReviewDialog(
    initialRating: Int = 5,
    initialContent: String = "",
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, content: String) -> Unit
) {
    var rating by remember { mutableStateOf(initialRating) }
    var content by remember { mutableStateOf(initialContent) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("How was this course?", fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InteractiveStarRating(
                    rating = rating,
                    onRatingChange = { rating = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = when(rating) {
                        1 -> "Poor"
                        2 -> "Fair"
                        3 -> "Good"
                        4 -> "Very Good"
                        5 -> "Excellent!"
                        else -> ""
                    },
                    color = PrimaryOrange,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Share your experience (Optional)") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(rating, content) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
            ) {
                Text("Submit Review")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}