package com.example.skillforge.feature.instructor_portal.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.skillforge.core.utils.getFileName

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillforgeMaterialUploadScreen(
    lessonId: String = "123",
    isLoading: Boolean = false,
    onNavigateBack: () -> Unit = {},
    onUploadClick: (type: String, fileUri: Uri?) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current

    var selectedType by remember { mutableStateOf("Video") }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        selectedFileUri = uri
        uri?.let {
            selectedFileName = getFileName(context, it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Upload Material", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, enabled = !isLoading) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(onClick = onNavigateBack, modifier = Modifier.weight(1f), enabled = !isLoading) { Text("Cancel") }
                    Button(
                        onClick = { onUploadClick(selectedType, selectedFileUri) },
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading && selectedFileUri != null
                    ) {
                        if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        else Text("Upload")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            // Material Type
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Material Type",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MaterialTypeCard(
                        title = "Video",
                        icon = Icons.Default.PlayCircle,
                        isSelected = selectedType == "Video",
                        onClick = {
                            if (!isLoading) {
                                selectedType = "Video"
                                selectedFileUri = null
                                selectedFileName = null
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                    MaterialTypeCard(
                        title = "Document",
                        icon = Icons.Default.Description,
                        isSelected = selectedType == "Document",
                        onClick = {
                            if (!isLoading) {
                                selectedType = "Document"
                                selectedFileUri = null
                                selectedFileName = null
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // translated comment
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Upload File",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(enabled = !isLoading) {
                            if (selectedType == "Video") filePickerLauncher.launch(arrayOf("video/*"))
                            else filePickerLauncher.launch(arrayOf("application/pdf", "application/msword", "application/zip"))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (selectedFileUri == null) Icons.Default.CloudUpload else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(text = selectedFileName ?: "Tap to browse files", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
fun MaterialTypeCard(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = contentColor)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SkillforgeMaterialUploadPreview() {
    MaterialTheme {
        SkillforgeMaterialUploadScreen()
    }
}
