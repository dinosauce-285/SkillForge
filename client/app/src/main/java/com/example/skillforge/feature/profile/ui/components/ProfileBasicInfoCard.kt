package com.example.skillforge.feature.profile.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.TextSecondaryColor
import com.example.skillforge.core.designsystem.TextFieldBackgroundColor
import com.example.skillforge.core.designsystem.SkillforgeShapes

@Composable
fun ProfileBasicInfoCard(
    fullName: String,
    headline: String,
    onFullNameChange: (String) -> Unit,
    isEditMode: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        SectionHeading(title = "Profile Information")

        Spacer(modifier = Modifier.height(SkillforgeSpacing.medium))

        if (isEditMode) {
            OutlinedTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = SkillforgeShapes.input,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = TextFieldBackgroundColor,
                    focusedContainerColor = TextFieldBackgroundColor,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
        } else {
            ProfileInfoRow(label = "Full Name", value = fullName.ifBlank { "Not set" })
            Spacer(modifier = Modifier.height(SkillforgeSpacing.medium))
            ProfileInfoRow(label = "Role", value = headline.ifBlank { "Student" })
        }
    }
}

@Composable
private fun SectionHeading(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

@Composable
private fun ProfileInfoRow(
    label: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondaryColor
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
