package com.example.skillforge.feature.instructor_portal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

import com.example.skillforge.data.remote.CategoryDto
import com.example.skillforge.domain.model.Category
import com.example.skillforge.feature.instructor_portal.viewmodel.CourseFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillforgeCourseFormScreen(
    categories: List<Category>,
    isEditMode: Boolean = false,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    uiState: CourseFormState = CourseFormState.Idle,
    onNavigateBack: () -> Unit = {},
    onSaveClick: (title: String, summary: String, price: String, categoryId: String) -> Unit = { _, _, _, _ -> }
) {
    var courseTitle by remember { mutableStateOf("") }
    var courseSummary by remember { mutableStateOf("") }
    var coursePrice by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf("") }

    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is CourseFormState.Success) {
            showSuccessDialog = true
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { /* Keep open until user confirms */ },
            title = { Text("Success!", fontWeight = FontWeight.Bold) },
            text = { Text("Your course has been published successfully.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onNavigateBack()
                    },
                    // translated comment
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Great")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Course" else "Create New Course", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, enabled = !isLoading) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Column {
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = { onSaveClick(courseTitle, courseSummary, coursePrice, selectedCategoryId) },
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading && courseTitle.isNotBlank() && coursePrice.isNotBlank() && selectedCategoryId.isNotBlank(),
                            // translated comment
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(if (isEditMode) "Save Changes" else "Publish Course")
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // translated comment
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(2.dp, MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp))
                    .clickable(enabled = !isLoading) { /* Handle image selection later */ },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Upload cover",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary // translated comment
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Upload Course Cover",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            OutlinedTextField(
                value = courseTitle, onValueChange = { courseTitle = it },
                label = { Text("Course Title") }, enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = courseSummary, onValueChange = { courseSummary = it },
                label = { Text("Course Summary") }, enabled = !isLoading,
                modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = coursePrice, onValueChange = { coursePrice = it },
                label = { Text("Price") }, enabled = !isLoading,
                leadingIcon = { Text("$", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(start = 8.dp)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(12.dp)
            )

            // translated comment
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                if (categories.isEmpty()) {
                    Text("Loading categories...", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(categories) { category ->
                            FilterChip(
                                selected = selectedCategoryId == category.id,
                                enabled = !isLoading,
                                onClick = { selectedCategoryId = category.id },
                                label = { Text(category.name) },
                                // translated comment
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
