package com.example.skillforge.feature.profile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.skillforge.core.designsystem.SkillforgeShapes
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.TextFieldBackgroundColor
import com.example.skillforge.core.designsystem.skillforgeElevatedCardColors

@Composable
fun ProfileBasicInfoCard(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    isEditMode: Boolean,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = SkillforgeShapes.card,
        colors = skillforgeElevatedCardColors()
    ) {
        Column(
            modifier = Modifier.padding(SkillforgeSpacing.medium)
        ) {
            Text(
                text = "Basic Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(SkillforgeSpacing.medium))

            OutlinedTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                readOnly = !isEditMode,
                shape = SkillforgeShapes.input,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = TextFieldBackgroundColor,
                    focusedContainerColor = TextFieldBackgroundColor,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        }
    }
}
