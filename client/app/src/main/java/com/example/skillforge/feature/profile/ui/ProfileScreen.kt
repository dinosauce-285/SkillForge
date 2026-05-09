package com.example.skillforge.feature.profile.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skillforge.core.designsystem.BackgroundColor
import com.example.skillforge.core.designsystem.SkillforgeLayout
import com.example.skillforge.core.designsystem.SkillforgeShapes
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.SurfaceColor
import com.example.skillforge.core.designsystem.components.SkillforgePrimaryButton
import com.example.skillforge.feature.profile.ui.components.ProfileAvatarHeader
import com.example.skillforge.feature.profile.ui.components.ProfileBasicInfoCard
import com.example.skillforge.feature.profile.ui.components.ProfileGoalsCard
import com.example.skillforge.feature.profile.ui.components.ProfileSkillsCard
import com.example.skillforge.feature.profile.viewmodel.ProfileUiState
import com.example.skillforge.feature.profile.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    token: String,
    viewModel: ProfileViewModel,
    onLogoutClick: () -> Unit,
    onNavigateToPurchaseHistory: () -> Unit = {},
    onBecomeInstructorClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    var showDropdownMenu by remember { mutableStateOf(false) }
    var newSkillText by remember { mutableStateOf("") }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val successState = uiState as? ProfileUiState.Success
    val isEditing = successState?.isEditing == true
    val isBusy = successState?.isSaving == true || successState?.isUploadingAvatar == true

    val context = LocalContext.current
    val contentResolver = context.contentResolver

    // Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.uploadAvatar(
                    uri = uri,
                    contentResolver = contentResolver
                )
            }
        }
    )

    // Initial load trigger
    LaunchedEffect(token) {
        viewModel.loadProfile()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Edit Profile" else "Profile",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    if (successState != null) {
                        IconButton(
                            onClick = {
                                if (isEditing) {
                                    viewModel.cancelEditing()
                                    newSkillText = ""
                                } else {
                                    viewModel.startEditing()
                                }
                            },
                            enabled = !isBusy
                        ) {
                            Icon(
                                imageVector = if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                                contentDescription = if (isEditing) "Cancel Edit" else "Edit Profile"
                            )
                        }
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
                                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
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
                    containerColor = SurfaceColor,
                    scrolledContainerColor = SurfaceColor,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            Column {
                AnimatedVisibility(
                    visible = successState?.isEditing == true,
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
                            Column(
                                verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small)
                            ) {
                                successState?.inlineErrorMessage?.let { message ->
                                    Text(
                                        text = message,
                                        color = MaterialTheme.colorScheme.error,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                SkillforgePrimaryButton(
                                    text = if (successState?.isSaving == true) "Saving..." else "Save Changes",
                                    onClick = { viewModel.saveChanges() },
                                    enabled = !isBusy
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        when (uiState) {
            is ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
            is ProfileUiState.Success -> {
                val state = uiState as ProfileUiState.Success
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = paddingValues.calculateTopPadding(),
                            bottom = paddingValues.calculateBottomPadding()
                        ),
                    verticalArrangement = Arrangement.spacedBy(SkillforgeLayout.sectionGap),
                    contentPadding = PaddingValues(bottom = SkillforgeSpacing.xxLarge)
                ) {
                    item {
                        ProfileAvatarHeader(
                            fullName = state.fullName,
                            headline = state.headline,
                            role = state.role,
                            avatarUrl = state.avatarUrl,
                            skillsCount = state.skills.size,
                            hasLearningGoals = state.learningGoals.isNotBlank(),
                            isEditMode = state.isEditing,
                            isUploadingAvatar = state.isUploadingAvatar,
                            onEditAvatarClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        )
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .padding(horizontal = SkillforgeLayout.screenHorizontalPadding),
                            verticalArrangement = Arrangement.spacedBy(SkillforgeLayout.listItemGap)
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = SkillforgeShapes.extraLarge,
                                color = SurfaceColor,
                                tonalElevation = 1.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(SkillforgeSpacing.large)
                                ) {
                                    ProfileBasicInfoCard(
                                        fullName = state.fullName,
                                        headline = state.headline,
                                        onFullNameChange = { viewModel.onFullNameChange(it) },
                                        isEditMode = state.isEditing
                                    )
                                    HorizontalDivider(modifier = Modifier.padding(vertical = SkillforgeSpacing.large))
                                    ProfileSkillsCard(
                                        skills = state.skills,
                                        newSkillText = newSkillText,
                                        onNewSkillChange = { newSkillText = it },
                                        onAddSkillClick = {
                                            viewModel.addSkill(newSkillText)
                                            newSkillText = ""
                                        },
                                        onRemoveSkillClick = { viewModel.removeSkill(it) },
                                        isEditMode = state.isEditing
                                    )
                                    HorizontalDivider(modifier = Modifier.padding(vertical = SkillforgeSpacing.large))
                                    ProfileGoalsCard(
                                        learningGoals = state.learningGoals,
                                        onLearningGoalsChange = { viewModel.onLearningGoalsChange(it) },
                                        isEditMode = state.isEditing
                                    )
                                }
                            }

                            AccountActionsSection(
                                role = state.role,
                                enabled = !state.isSaving,
                                onNavigateToPurchaseHistory = onNavigateToPurchaseHistory,
                                onBecomeInstructorClick = onBecomeInstructorClick
                            )
                        }
                    }
                }
            }
            is ProfileUiState.Error -> {
                InitialProfileError(
                    message = (uiState as ProfileUiState.Error).message,
                    onRetryClick = viewModel::retryLoadProfile,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {}
        }
    }
}

@Composable
private fun AccountActionsSection(
    role: String,
    enabled: Boolean,
    onNavigateToPurchaseHistory: () -> Unit,
    onBecomeInstructorClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = SkillforgeShapes.extraLarge,
        color = SurfaceColor,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(SkillforgeSpacing.large),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
        ) {
            Text(
                text = "Account Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            OutlinedButton(
                onClick = onNavigateToPurchaseHistory,
                enabled = enabled,
                modifier = Modifier.fillMaxWidth(),
                shape = SkillforgeShapes.button,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null)
                Spacer(modifier = Modifier.width(SkillforgeSpacing.small))
                Text("Purchase History")
            }

            if (role.equals("STUDENT", ignoreCase = true)) {
                Button(
                    onClick = onBecomeInstructorClick,
                    enabled = enabled,
                    modifier = Modifier.fillMaxWidth(),
                    shape = SkillforgeShapes.button,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Default.School, contentDescription = null)
                    Spacer(modifier = Modifier.width(SkillforgeSpacing.small))
                    Text("Become an Instructor")
                }
            }
        }
    }
}

@Composable
private fun InitialProfileError(
    message: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(SkillforgeLayout.screenHorizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedButton(
                onClick = onRetryClick,
                shape = SkillforgeShapes.button
            ) {
                Text("Retry")
            }
        }
    }
}
