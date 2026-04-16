package com.example.skillforge.feature.profile.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.core.designsystem.PrimaryOrangeLight

@Composable
fun ProfileAvatarHeader(
    fullName: String,
    headline: String,
    avatarUrl: String?,
    isEditMode: Boolean,
    onEditAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryOrangeLight, Color(0xFF9F3B00))
                )
            )
            .padding(top = 80.dp, bottom = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar Container
            Box(
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                AsyncImage(
                    model = avatarUrl ?: "https://ui-avatars.com/api/?name=User&background=F26724&color=fff",
                    contentDescription = "User Profile Avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(128.dp)
                        .clip(CircleShape)
                        .border(4.dp, Color.White, CircleShape)
                        .background(Color.White)
                )

                // Edit Button Badge - Only visible in Edit Mode
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-4).dp, y = (-4).dp)
                ) {
                    androidx.compose.animation.AnimatedVisibility(
                        visible = isEditMode,
                        enter = scaleIn(),
                        exit = scaleOut()
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = com.example.skillforge.core.designsystem.PrimaryOrange,
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                            shadowElevation = 8.dp,
                            modifier = Modifier
                                .size(40.dp)
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

            Text(
                text = fullName.ifEmpty { "Your Name" },
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            )
            Text(
                text = headline.ifEmpty { "Student" },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f)
                ),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
