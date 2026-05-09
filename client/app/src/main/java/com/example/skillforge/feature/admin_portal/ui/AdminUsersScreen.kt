package com.example.skillforge.feature.admin_portal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.skillforge.core.designsystem.SkillforgeLayout
import com.example.skillforge.core.designsystem.SkillforgeShapes
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.components.SafeFlowRow
import com.example.skillforge.core.designsystem.components.SkillforgeCard
import com.example.skillforge.domain.model.User
import com.example.skillforge.feature.admin_portal.viewmodel.AdminViewModel

@Composable
fun AdminUsersScreen(
    token: String,
    viewModel: AdminViewModel,
    onBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToCoupons: () -> Unit,
    onNavigateToFinance: () -> Unit
) {
    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchUsers(token)
    }

    AdminScaffold(
        title = "Manage Users",
        selectedTab = AdminTab.Users,
        onNavigateToDashboard = onNavigateToDashboard,
        onNavigateToUsers = onNavigateToUsers,
        onNavigateToQueue = onNavigateToQueue,
        onNavigateToCoupons = onNavigateToCoupons,
        onNavigateToFinance = onNavigateToFinance,
        onBack = onBack,
        actions = {
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, "Add Instructor")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading && users.isEmpty() -> {
                    UsersLoadingCard(modifier = Modifier.align(Alignment.Center))
                }

                !error.isNullOrBlank() && users.isEmpty() -> {
                    UsersMessageCard(
                        title = "Unable to load users",
                        message = error.orEmpty(),
                        isError = true,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(SkillforgeLayout.screenHorizontalPadding)
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = SkillforgeLayout.screenHorizontalPadding,
                            vertical = SkillforgeLayout.screenVerticalPadding
                        ),
                        verticalArrangement = Arrangement.spacedBy(SkillforgeLayout.listItemGap)
                    ) {
                        item {
                            UsersHeaderCard(
                                users = users,
                                onAddInstructor = { showAddDialog = true }
                            )
                        }

                        if (!error.isNullOrBlank()) {
                            item {
                                UsersMessageCard(
                                    title = "Some user data may be outdated",
                                    message = error.orEmpty(),
                                    isError = true
                                )
                            }
                        }

                        if (users.isEmpty()) {
                            item {
                                UsersMessageCard(
                                    title = "No users found",
                                    message = "New learners and instructors will appear here after they are created.",
                                    isError = false
                                )
                            }
                        } else {
                            item {
                                Text(
                                    text = "User directory",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            items(users, key = { it.id }) { user ->
                                UserCard(
                                    user = user,
                                    onBanToggle = {
                                        viewModel.toggleUserBan(token, user.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AddInstructorDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { email, fullName ->
                    viewModel.createInstructor(token, email, fullName)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
private fun UsersHeaderCard(
    users: List<User>,
    onAddInstructor: () -> Unit
) {
    val activeUsers = users.count { it.isActive }
    val bannedUsers = users.size - activeUsers
    val instructors = users.count { it.role.equals("INSTRUCTOR", ignoreCase = true) }

    SkillforgeCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(SkillforgeShapes.large)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(SkillforgeSpacing.medium))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "User directory",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Review accounts and manage instructor access.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            SafeFlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalSpacing = SkillforgeSpacing.small,
                verticalSpacing = SkillforgeSpacing.small
            ) {
                UserMetricPill(label = "Total", value = users.size.toString())
                UserMetricPill(label = "Active", value = activeUsers.toString())
                UserMetricPill(label = "Banned", value = bannedUsers.toString())
                UserMetricPill(label = "Instructors", value = instructors.toString())
            }

            Button(
                onClick = onAddInstructor,
                modifier = Modifier.fillMaxWidth(),
                shape = SkillforgeShapes.button
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(SkillforgeSpacing.small))
                Text("Add Instructor")
            }
        }
    }
}

@Composable
private fun UserMetricPill(label: String, value: String) {
    Surface(
        modifier = Modifier.defaultMinSize(minWidth = 124.dp),
        shape = SkillforgeShapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.36f)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = SkillforgeSpacing.medium,
                vertical = SkillforgeSpacing.small
            ),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun UserCard(user: User, onBanToggle: () -> Unit) {
    val isAdmin = user.role.equals("ADMIN", ignoreCase = true)

    SkillforgeCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                UserAvatar(name = user.fullName, email = user.email)
                Spacer(modifier = Modifier.width(SkillforgeSpacing.medium))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)
                ) {
                    Text(
                        text = user.fullName.ifBlank { "Unnamed user" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    SafeFlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = SkillforgeSpacing.xSmall),
                        horizontalSpacing = SkillforgeSpacing.small,
                        verticalSpacing = SkillforgeSpacing.small
                    ) {
                        UserBadge(
                            text = user.role.toDisplayRole(),
                            emphasis = BadgeEmphasis.Neutral
                        )
                        UserBadge(
                            text = if (user.isActive) "Active" else "Banned",
                            emphasis = if (user.isActive) BadgeEmphasis.Primary else BadgeEmphasis.Error
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isAdmin) {
                    Surface(
                        shape = SkillforgeShapes.chip,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f)
                    ) {
                        Text(
                            text = "Protected admin account",
                            modifier = Modifier.padding(
                                horizontal = SkillforgeSpacing.medium,
                                vertical = SkillforgeSpacing.small
                            ),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else if (user.isActive) {
                    OutlinedButton(
                        onClick = onBanToggle,
                        modifier = Modifier.defaultMinSize(minHeight = 36.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        contentPadding = PaddingValues(
                            horizontal = SkillforgeSpacing.medium,
                            vertical = SkillforgeSpacing.small
                        ),
                        shape = SkillforgeShapes.button
                    ) {
                        Text("Ban", style = MaterialTheme.typography.labelLarge)
                    }
                } else {
                    Button(
                        onClick = onBanToggle,
                        modifier = Modifier.defaultMinSize(minHeight = 36.dp),
                        contentPadding = PaddingValues(
                            horizontal = SkillforgeSpacing.medium,
                            vertical = SkillforgeSpacing.small
                        ),
                        shape = SkillforgeShapes.button
                    ) {
                        Text("Unban", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun UserAvatar(name: String, email: String) {
    val initial = (name.ifBlank { email }).trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private enum class BadgeEmphasis {
    Primary,
    Neutral,
    Error
}

@Composable
private fun UserBadge(
    text: String,
    emphasis: BadgeEmphasis
) {
    val containerColor = when (emphasis) {
        BadgeEmphasis.Primary -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        BadgeEmphasis.Neutral -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        BadgeEmphasis.Error -> MaterialTheme.colorScheme.error.copy(alpha = 0.10f)
    }
    val contentColor = when (emphasis) {
        BadgeEmphasis.Primary -> MaterialTheme.colorScheme.primary
        BadgeEmphasis.Neutral -> MaterialTheme.colorScheme.onSurfaceVariant
        BadgeEmphasis.Error -> MaterialTheme.colorScheme.error
    }

    Surface(
        shape = SkillforgeShapes.chip,
        color = containerColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun UsersLoadingCard(modifier: Modifier = Modifier) {
    SkillforgeCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(SkillforgeLayout.screenHorizontalPadding)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeLayout.cardContentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(SkillforgeSpacing.medium))
            Column {
                Text(
                    text = "Loading users",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Fetching the latest account directory.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun UsersMessageCard(
    title: String,
    message: String,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    SkillforgeCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AddInstructorDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)) {
                Text(
                    text = "Add Instructor",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Create an instructor account with the required profile details.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    placeholder = { Text("instructor@example.com") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = SkillforgeShapes.input
                )
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = SkillforgeShapes.input
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = SkillforgeShapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f)
                ) {
                    Text(
                        text = "Default password will be set to: Password123!",
                        modifier = Modifier.padding(SkillforgeSpacing.medium),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(email, fullName) },
                enabled = email.isNotBlank() && fullName.isNotBlank(),
                shape = SkillforgeShapes.button
            ) {
                Text("Add Instructor")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = SkillforgeShapes.large,
        containerColor = MaterialTheme.colorScheme.surface
    )
}

private fun String.toDisplayRole(): String {
    return lowercase()
        .replaceFirstChar { char -> char.uppercase() }
}
