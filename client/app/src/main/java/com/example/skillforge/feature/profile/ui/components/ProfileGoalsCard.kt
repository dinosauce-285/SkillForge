package com.example.skillforge.feature.profile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skillforge.core.designsystem.SkillforgeShapes
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.TextSecondaryColor
import com.example.skillforge.core.designsystem.TextFieldBackgroundColor

@Composable
fun ProfileGoalsCard(
    learningGoals: String,
    onLearningGoalsChange: (String) -> Unit,
    isEditMode: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Learning Goals",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(SkillforgeSpacing.medium))

        if (isEditMode) {
            OutlinedTextField(
                value = learningGoals,
                onValueChange = onLearningGoalsChange,
                placeholder = { Text("Briefly describe your learning goals...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 140.dp),
                maxLines = 5,
                shape = SkillforgeShapes.input,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = TextFieldBackgroundColor,
                    focusedContainerColor = TextFieldBackgroundColor,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        } else {
            Text(
                text = learningGoals.ifBlank { "No learning goals added yet." },
                style = MaterialTheme.typography.bodyLarge,
                color = if (learningGoals.isBlank()) {
                    TextSecondaryColor
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}
