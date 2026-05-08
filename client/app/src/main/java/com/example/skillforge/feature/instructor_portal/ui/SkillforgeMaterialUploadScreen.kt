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
import androidx.compose.material.icons.filled.UploadFile
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

    var pendingUploadType by remember { mutableStateOf("VIDEO") }
    var videoUri by remember { mutableStateOf<Uri?>(null) }
    var videoFileName by remember { mutableStateOf<String?>(null) }
    var materialUri by remember { mutableStateOf<Uri?>(null) }
    var materialFileName by remember { mutableStateOf<String?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            if (pendingUploadType == "VIDEO") {
                videoUri = it
                videoFileName = getFileName(context, it)
            } else {
                materialUri = it
                materialFileName = getFileName(context, it)
            }
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
                    OutlinedButton(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth(), enabled = !isLoading) { Text("Cancel") }
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            UploadSection(
                title = "Lesson Video",
                subtitle = "One MP4 video only. Use web/mobile export: H.264/AAC, 720p or 1080p.",
                icon = Icons.Default.PlayCircle,
                selectedFileName = videoFileName,
                pickLabel = "Choose video",
                uploadLabel = "Upload Video",
                enabled = !isLoading,
                onPickFile = {
                    pendingUploadType = "VIDEO"
                    filePickerLauncher.launch(arrayOf("video/mp4"))
                },
                onUpload = { onUploadClick("VIDEO", videoUri) },
                canUpload = videoUri != null
            )

            UploadSection(
                title = "Materials",
                subtitle = "PDFs, documents, archives, source files, or any other attachment.",
                icon = Icons.Default.Description,
                selectedFileName = materialFileName,
                pickLabel = "Choose material",
                uploadLabel = "Upload Material",
                enabled = !isLoading,
                onPickFile = {
                    pendingUploadType = "DOCUMENT"
                    filePickerLauncher.launch(arrayOf("*/*"))
                },
                onUpload = { onUploadClick("DOCUMENT", materialUri) },
                canUpload = materialUri != null
            )
        }
    }
}

@Composable
private fun UploadSection(
    title: String,
    subtitle: String,
    icon: ImageVector,
    selectedFileName: String?,
    pickLabel: String,
    uploadLabel: String,
    enabled: Boolean,
    onPickFile: () -> Unit,
    onUpload: () -> Unit,
    canUpload: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(14.dp))
                    .clickable(enabled = enabled, onClick = onPickFile),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = if (selectedFileName == null) Icons.Default.CloudUpload else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = selectedFileName ?: pickLabel,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Button(
                onClick = onUpload,
                enabled = enabled && canUpload,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.UploadFile, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(uploadLabel)
            }
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
