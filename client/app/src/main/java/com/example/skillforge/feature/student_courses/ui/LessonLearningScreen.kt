package com.example.skillforge.feature.student_courses.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.core.designsystem.SkillforgeLayout
import com.example.skillforge.core.designsystem.SkillforgeShapes
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.SkillforgeTheme
import com.example.skillforge.core.designsystem.skillforgeElevatedCardColors
import com.example.skillforge.domain.model.LessonContent
import com.example.skillforge.domain.model.LessonMaterial
import com.example.skillforge.feature.student_courses.viewmodel.LessonContentUiState
import com.example.skillforge.feature.student_courses.viewmodel.StudentCoursesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonLearningScreen(
    sessionToken: String,
    lessonId: String,
    viewModel: StudentCoursesViewModel,
    onNavigateBack: () -> Unit = {},
) {
    val uiState by viewModel.lessonContentState.collectAsState()

    LaunchedEffect(lessonId, sessionToken) {
        viewModel.loadLessonContent(sessionToken, lessonId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.lesson?.courseTitle ?: "Lesson",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                ),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        val errorMessage = uiState.errorMessage
        val lesson = uiState.lesson
        when {
            uiState.isLoading -> LessonLoadingState(modifier = Modifier.padding(paddingValues))
            errorMessage != null -> LessonErrorState(
                message = errorMessage,
                modifier = Modifier.padding(paddingValues),
            )
            lesson != null -> LessonContentBody(
                lesson = lesson,
                modifier = Modifier.padding(paddingValues),
            )
        }
    }
}

@Composable
private fun LessonContentBody(
    lesson: LessonContent,
    modifier: Modifier = Modifier,
) {
    val videoMaterial = lesson.materials.firstOrNull { it.type == "VIDEO" }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = SkillforgeLayout.screenHorizontalPadding,
            vertical = SkillforgeSpacing.medium,
        ),
        verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium),
    ) {
        item {
            if (videoMaterial != null) {
                VideoPlayerSection(videoUrl = videoMaterial.fileUrl)
            } else {
                EmptyVideoState()
            }
        }

        item {
            LessonInfoCard(lesson = lesson)
        }

        item {
            LessonMaterialsCard(materials = lesson.materials)
        }
    }
}

@Composable
private fun LessonInfoCard(lesson: LessonContent) {
    ElevatedCard(shape = SkillforgeShapes.card, colors = skillforgeElevatedCardColors()) {
        Column(
            modifier = Modifier.padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small),
        ) {
            Text(
                text = lesson.chapterTitle.uppercase(),
                color = PrimaryOrange,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = lesson.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Course: ${lesson.courseTitle}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LessonMaterialsCard(materials: List<LessonMaterial>) {
    val context = LocalContext.current

    ElevatedCard(shape = SkillforgeShapes.card, colors = skillforgeElevatedCardColors()) {
        Column(
            modifier = Modifier.padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium),
        ) {
            Text(
                text = "Lesson Materials",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )

            if (materials.isEmpty()) {
                Text(
                    text = "No materials available for this lesson yet.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                materials.forEach { material ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(material.fileUrl))
                                context.startActivity(intent)
                            }
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(12.dp),
                            )
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = material.icon(),
                            contentDescription = null,
                            tint = PrimaryOrange,
                            modifier = Modifier.size(24.dp),
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = material.fileUrl.substringAfterLast('/'),
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = "${material.type} • ${material.status}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(material.fileUrl))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        ) {
                            Text("Open")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoPlayerSection(videoUrl: String) {
    val context = LocalContext.current
    val exoPlayer = androidx.compose.runtime.remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            playWhenReady = false
        }
    }

    androidx.compose.runtime.DisposableEffect(exoPlayer) {
        onDispose { exoPlayer.release() }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(Color.Black, shape = SkillforgeShapes.card),
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun EmptyVideoState() {
    ElevatedCard(shape = SkillforgeShapes.card, colors = CardDefaults.elevatedCardColors()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "No video material for this lesson",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LessonLoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = PrimaryOrange)
    }
}

@Composable
private fun LessonErrorState(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            textAlign = TextAlign.Center,
        )
    }
}

private fun LessonMaterial.icon() = when (type) {
    "VIDEO" -> Icons.Default.OndemandVideo
    "DOCUMENT" -> Icons.Default.PictureAsPdf
    else -> Icons.Default.Description
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_6")
@Composable
private fun LessonLearningScreenPreview() {
    SkillforgeTheme {
        LessonContentBody(
            lesson = LessonContent(
                id = "lesson-1",
                title = "Compose Foundations",
                chapterTitle = "Getting Started",
                courseTitle = "Modern Android UI with Compose",
                materials = listOf(
                    LessonMaterial(
                        id = "mat-1",
                        type = "VIDEO",
                        fileUrl = "https://storage.googleapis.com/exoplayer-test-media-1/mp4/android-screens-10s.mp4",
                        fileSize = 1024,
                        status = "READY",
                    ),
                ),
            ),
        )
    }
}
