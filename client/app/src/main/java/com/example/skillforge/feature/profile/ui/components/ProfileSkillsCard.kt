package com.example.skillforge.feature.profile.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skillforge.core.designsystem.*
import com.example.skillforge.core.designsystem.components.SafeFlowRow

@Composable
fun ProfileSkillsCard(
    skills: List<String>,
    newSkillText: String,
    onNewSkillChange: (String) -> Unit,
    onAddSkillClick: () -> Unit,
    onRemoveSkillClick: (String) -> Unit,
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
                text = "Skills",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            AnimatedVisibility(
                visible = isEditMode,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(SkillforgeSpacing.medium))
                    OutlinedTextField(
                        value = newSkillText,
                        onValueChange = onNewSkillChange,
                        placeholder = { Text("Add a new skill (e.g., Figma)...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = SkillforgeShapes.input,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = TextFieldBackgroundColor,
                            focusedContainerColor = TextFieldBackgroundColor,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        trailingIcon = {
                            IconButton(onClick = onAddSkillClick) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Skill",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(SkillforgeSpacing.medium))

            SafeFlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalSpacing = SkillforgeSpacing.small,
                verticalSpacing = SkillforgeSpacing.small
            ) {
                skills.forEach { skill ->
                    AssistChip(
                        onClick = { },
                        label = { Text(skill, color = ChipUnselectedTextColor) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = ChipUnselectedBackgroundColor
                        ),
                        border = AssistChipDefaults.assistChipBorder(
                            borderColor = ChipUnselectedBorderColor,
                            enabled = true
                        ),
                        trailingIcon = {
                            if (isEditMode) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove Skill",
                                    tint = ChipUnselectedTextColor,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { onRemoveSkillClick(skill) }
                                )
                            }
                        },
                        shape = SkillforgeShapes.chip
                    )
                }
            }
        }
    }
}
