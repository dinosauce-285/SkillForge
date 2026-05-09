package com.example.skillforge.feature.profile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.core.designsystem.SkillforgeLayout
import com.example.skillforge.core.designsystem.SkillforgeShapes
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.SurfaceColor
import com.example.skillforge.core.designsystem.TextSecondaryColor

@Composable
fun ProfileAvatarHeader(
    fullName: String,
    headline: String,
    role: String,
    avatarUrl: String?,
    skillsCount: Int,
    hasLearningGoals: Boolean,
    isEditMode: Boolean,
    isUploadingAvatar: Boolean = false,
    onEditAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val avatarFallbackName = fullName.trim().ifBlank { "User" }.replace(" ", "+")

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = SkillforgeLayout.screenHorizontalPadding,
                end = SkillforgeLayout.screenHorizontalPadding,
                top = SkillforgeSpacing.medium
            ),
        shape = SkillforgeShapes.extraLarge,
        color = SurfaceColor,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeSpacing.large),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
        ) {
            Box(
                modifier = Modifier.size(96.dp)
            ) {
                AsyncImage(
                    model = avatarUrl ?: "https://ui-avatars.com/api/?name=$avatarFallbackName&background=F26724&color=fff",
                    contentDescription = "User Profile Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(3.dp, PrimaryOrange.copy(alpha = 0.18f), CircleShape)
                        .background(Color.White)
                )

                if (isUploadingAvatar) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 3.dp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 2.dp, y = 2.dp)
                ) {
                    if (isEditMode) {
                        Surface(
                            shape = CircleShape,
                            color = PrimaryOrange,
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                            shadowElevation = 4.dp,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable(onClick = onEditAvatarClick)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Change Avatar",
                                tint = Color.White,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small)
            ) {
                Text(
                    text = fullName.ifBlank { "Your Name" },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                RoleBadge(text = role.ifBlank { headline.ifBlank { "Student" } })
                Text(
                    text = profileSubtitle(skillsCount, hasLearningGoals),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryColor
                )
            }
        }
    }
}

@Composable
private fun RoleBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = PrimaryOrange.copy(alpha = 0.12f)
    ) {
        Text(
            text = text.replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = SkillforgeSpacing.small, vertical = SkillforgeSpacing.xSmall),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryOrange
        )
    }
}

private fun profileSubtitle(skillsCount: Int, hasLearningGoals: Boolean): String {
    return when {
        skillsCount > 0 && hasLearningGoals -> "$skillsCount skills listed and learning goals set"
        skillsCount > 0 -> "$skillsCount skills listed"
        hasLearningGoals -> "Learning goals set"
        else -> "Profile in progress"
    }
}
