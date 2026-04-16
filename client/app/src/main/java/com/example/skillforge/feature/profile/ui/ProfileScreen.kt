package com.example.skillforge.feature.profile.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skillforge.core.designsystem.BackgroundColor
import com.example.skillforge.core.designsystem.SkillforgeLayout
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.SurfaceColor
import com.example.skillforge.core.designsystem.components.SkillforgePrimaryButton
import com.example.skillforge.feature.profile.ui.components.ProfileAvatarHeader
import com.example.skillforge.feature.profile.ui.components.ProfileBasicInfoCard
import com.example.skillforge.feature.profile.ui.components.ProfileGoalsCard
import com.example.skillforge.feature.profile.ui.components.ProfileSkillsCard
import com.example.skillforge.feature.profile.viewmodel.ProfileViewModel
import androidx.compose.material3.rememberTopAppBarState




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    token: String,
    viewModel: ProfileViewModel,
    onLogoutClick: () -> Unit,
    onBackClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    var isEditMode by remember { mutableStateOf(false) }
    var showDropdownMenu by remember { mutableStateOf(false) }
    var newSkillText by remember { mutableStateOf("") }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    // Initial load trigger
    LaunchedEffect(token) {
        viewModel.loadProfile(token)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { isEditMode = !isEditMode }) {
                        Icon(
                            imageVector = if (isEditMode) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = if (isEditMode) "Cancel Edit" else "Edit Profile"
                        )
                    }
                    Box {
                        IconButton(onClick = { showDropdownMenu = true }) {
                            Icon(imageVector = Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = showDropdownMenu,
                            onDismissRequest = { showDropdownMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Logout", color = MaterialTheme.colorScheme.error) },
                                onClick = {
                                    showDropdownMenu = false
                                    onLogoutClick()
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.ExitToApp,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Column {
                AnimatedVisibility(
                    visible = isEditMode,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Surface(
                        shadowElevation = 8.dp,
                        color = SurfaceColor
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SkillforgeSpacing.medium)
                        ) {
                            SkillforgePrimaryButton(
                                text = "Save Changes",
                                onClick = {
                                    viewModel.saveProfile(token)
                                    isEditMode = false
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding()),
                verticalArrangement = Arrangement.spacedBy(SkillforgeLayout.sectionGap),
                contentPadding = PaddingValues(bottom = SkillforgeSpacing.xxLarge)
            ) {
                item {
                    ProfileAvatarHeader(
                        fullName = uiState.fullName,
                        headline = "Student", // Or from state
                        avatarUrl = uiState.avatarUrl,
                        isEditMode = isEditMode,
                        onEditAvatarClick = { /* Handle image pick */ }
                    )
                }

                item {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = SkillforgeLayout.screenHorizontalPadding),
                        verticalArrangement = Arrangement.spacedBy(SkillforgeLayout.listItemGap)
                    ) {
                        ProfileBasicInfoCard(
                            fullName = uiState.fullName,
                            onFullNameChange = { viewModel.updateFullName(it) },
                            isEditMode = isEditMode
                        )

                        ProfileSkillsCard(
                            skills = uiState.skills,
                            newSkillText = newSkillText,
                            onNewSkillChange = { newSkillText = it },
                            onAddSkillClick = {
                                viewModel.addSkill(newSkillText)
                                newSkillText = ""
                            },
                            onRemoveSkillClick = { viewModel.removeSkill(it) },
                            isEditMode = isEditMode
                        )

                        ProfileGoalsCard(
                            learningGoals = uiState.learningGoals,
                            onLearningGoalsChange = { viewModel.updateLearningGoals(it) },
                            isEditMode = isEditMode
                        )
                    }
                }
            }
        }
    }
}
