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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayLesson
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.SkillforgeTheme
import com.example.skillforge.domain.model.CourseChapter
import com.example.skillforge.domain.model.CourseDetails
import com.example.skillforge.domain.model.CourseLesson
import com.example.skillforge.domain.model.LessonContent
import com.example.skillforge.domain.model.LessonMaterial
import com.example.skillforge.feature.student_courses.ui.components.StudentBottomNavigationBar
import com.example.skillforge.feature.student_courses.viewmodel.StudentCoursesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonLearningScreen(
    sessionToken: String,
    courseId: String,
    lessonId: String,
    viewModel: StudentCoursesViewModel,
    onLessonSelected: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onNavigateToDiscover: () -> Unit = {},
    onNavigateToLearning: () -> Unit = {},
    onNavigateToWishlist: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
) {
    val lessonUiState by viewModel.lessonContentState.collectAsState()
    val courseUiState by viewModel.courseDetailsState.collectAsState()
    val course = courseUiState.course

    LaunchedEffect(courseId, sessionToken) { viewModel.loadCourseDetails(courseId, sessionToken) }
    LaunchedEffect(lessonId, sessionToken) { viewModel.loadLessonContent(sessionToken, lessonId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = courseUiState.course?.title ?: lessonUiState.lesson?.courseTitle ?: "Lesson",
                        fontWeight = FontWeight.Bold,
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        bottomBar = {
            Column {
                courseUiState.course?.let {
                    LessonNavBar(it, lessonId, onLessonSelected)
                }
                StudentBottomNavigationBar(
                    currentRoute = "Learning",
                    onNavigateToDiscover = onNavigateToDiscover,
                    onNavigateToLearning = onNavigateToLearning,
                    onNavigateToWishlist = onNavigateToWishlist,
                    onNavigateToProfile = onNavigateToProfile,
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        when {
            courseUiState.isLoading && course == null -> LoadingState(Modifier.padding(padding))
            courseUiState.errorMessage != null && course == null -> ErrorState(courseUiState.errorMessage, Modifier.padding(padding))
            course != null -> LessonBody(
                course = course,
                lessonId = lessonId,
                lesson = lessonUiState.lesson,
                lessonLoading = lessonUiState.isLoading,
                lessonError = lessonUiState.errorMessage,
                onLessonSelected = onLessonSelected,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

private enum class Tab(val label: String) { CONTENT("Lesson Content"), CURRICULUM("Curriculum"), DISCUSSION("Discussion") }

@Composable
private fun LessonBody(
    course: CourseDetails,
    lessonId: String,
    lesson: LessonContent?,
    lessonLoading: Boolean,
    lessonError: String?,
    onLessonSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by rememberSaveable { mutableStateOf(Tab.CONTENT) }
    var expandedIds by rememberSaveable(course.id) { mutableStateOf(setOf<String>()) }
    var comment by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(course.id, lessonId) {
        course.chapters.firstOrNull { c -> c.lessons.any { it.id == lessonId } }?.id?.let { expandedIds = expandedIds + it }
    }

    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
        item { VideoArea(lesson, lessonLoading, lessonError) }
        item { lesson?.let { LessonInfo(it) } }
        item { Tabs(selectedTab) { selectedTab = it } }
        item {
            Column(
                modifier = Modifier.padding(horizontal = SkillforgeLayout.screenHorizontalPadding, vertical = SkillforgeSpacing.medium),
                verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.large),
            ) {
                when (selectedTab) {
                    Tab.CONTENT -> LessonContentPane(lesson, lessonLoading, lessonError)
                    Tab.CURRICULUM -> CurriculumPane(course, lessonId, expandedIds, { id ->
                        expandedIds = if (id in expandedIds) expandedIds - id else expandedIds + id
                    }, onLessonSelected)
                    Tab.DISCUSSION -> DiscussionPane(lesson?.title ?: "this lesson", comment) { comment = it }
                }
            }
        }
    }
}

@Composable
private fun VideoArea(lesson: LessonContent?, loading: Boolean, error: String?) {
    val video = lesson?.materials?.firstOrNull { it.type == "VIDEO" }
    when {
        loading && lesson == null -> LoadingState(Modifier.fillMaxWidth().height(240.dp))
        error != null && lesson == null -> ErrorState(error, Modifier.padding(16.dp))
        video != null -> VideoPlayer(video.fileUrl)
        else -> Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Color.Black), contentAlignment = Alignment.Center) {
            Text("This lesson has no video", color = Color.White.copy(alpha = 0.85f))
        }
    }
}

@Composable
private fun LessonInfo(lesson: LessonContent) {
    Row(
        modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(color = PrimaryOrange.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                Text(lesson.chapterTitle, color = PrimaryOrange, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
            }
            Text(lesson.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        IconButton(onClick = {}) { Icon(Icons.Default.BookmarkBorder, contentDescription = "Save", tint = PrimaryOrange) }
    }
}

@Composable
private fun Tabs(selected: Tab, onSelect: (Tab) -> Unit) {
    Row(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))) {
        Tab.entries.forEach { tab ->
            Column(
                modifier = Modifier.weight(1f).clickable { onSelect(tab) },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(tab.label, color = if (selected == tab) PrimaryOrange else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = if (selected == tab) FontWeight.Bold else FontWeight.Medium, modifier = Modifier.padding(vertical = 14.dp))
                Box(Modifier.fillMaxWidth().height(2.dp).background(if (selected == tab) PrimaryOrange else Color.Transparent))
            }
        }
    }
}

@Composable
private fun LessonContentPane(lesson: LessonContent?, loading: Boolean, error: String?) {
    when {
        loading && lesson == null -> LoadingState()
        error != null && lesson == null -> ErrorState(error)
        lesson != null -> {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("In ${lesson.title}, we focus on practical decisions and a clear walkthrough of ${lesson.chapterTitle.lowercase()}.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyLarge)
                Text("Key Takeaways", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                listOf(
                    "Understand the main goal of ${lesson.title.lowercase()}.",
                    "Break the topic into reusable steps you can apply in projects.",
                    "Use the attached resources to review the lesson later.",
                ).forEach {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(18.dp))
                        Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                    }
                }
                lesson.materials.filter { it.type != "VIDEO" }.ifEmpty { emptyList() }.forEach { ResourceCard(it) }
                if (lesson.materials.none { it.type != "VIDEO" }) {
                    Text("No additional resources for this lesson yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun ResourceCard(material: LessonMaterial) {
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(PrimaryOrange.copy(alpha = 0.06f)).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(PrimaryOrange.copy(alpha = 0.18f)), contentAlignment = Alignment.Center) {
                    Icon(material.icon(), contentDescription = null, tint = PrimaryOrange)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(material.fileName(), fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("${material.displaySize()} • ${material.typeLabel()}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
            }
            Button(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(material.fileUrl))) }, colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)) {
                Text("Download")
            }
        }
        Box(
            modifier = Modifier.fillMaxWidth().aspectRatio(3f / 4f).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)).clickable { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(material.fileUrl))) },
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Visibility, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(28.dp))
                Spacer(Modifier.height(8.dp))
                Text("Tap to preview document", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun CurriculumPane(course: CourseDetails, lessonId: String, expandedIds: Set<String>, onToggle: (String) -> Unit, onLessonSelected: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Course Outline", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("${course.chapters.size} chapters • ${course.chapters.sumOf { it.lessons.size }} lessons", color = MaterialTheme.colorScheme.onSurfaceVariant)
        course.chapters.forEachIndexed { index, chapter ->
            ChapterBlock(chapter, index + 1, lessonId, chapter.id in expandedIds, { onToggle(chapter.id) }, onLessonSelected)
        }
    }
}

@Composable
private fun ChapterBlock(chapter: CourseChapter, number: Int, lessonId: String, expanded: Boolean, onToggle: () -> Unit, onLessonSelected: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth().clickable(onClick = onToggle).padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
            Box(Modifier.padding(top = 6.dp).size(10.dp).background(PrimaryOrange, RoundedCornerShape(99.dp)))
            Spacer(Modifier.width(2.dp))
            Column(Modifier.weight(1f)) {
                Text("Chapter $number", color = PrimaryOrange, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text(chapter.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            Icon(if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (expanded) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Box(Modifier.padding(start = 4.dp).width(2.dp).height((chapter.lessons.size * 58).dp).background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)))
                Column(Modifier.padding(start = 22.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    chapter.lessons.forEachIndexed { idx, lesson -> LessonRow(lesson, idx + 1, lesson.id == lessonId) { onLessonSelected(lesson.id) } }
                }
            }
        }
    }
}

@Composable
private fun LessonRow(lesson: CourseLesson, number: Int, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).clip(RoundedCornerShape(12.dp)).background(if (selected) PrimaryOrange.copy(alpha = 0.1f) else Color.Transparent).padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.width(12.dp).height(2.dp).background(if (selected) PrimaryOrange else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(99.dp)))
        Icon(Icons.Default.PlayLesson, contentDescription = null, tint = if (selected) PrimaryOrange else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        Column(Modifier.weight(1f)) {
            Text("Lesson $number", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
            Text(lesson.title, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = if (selected) PrimaryOrange else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun DiscussionPane(lessonTitle: String, comment: String, onCommentChange: (String) -> Unit) {
    val comments = remember(lessonTitle) {
        listOf(
            "Alex Rivera" to "This lesson on $lessonTitle was really helpful. More examples like this would be great.",
            "Sarah Chen" to "The attached document clarified the tricky parts for me. Thanks for including it.",
        )
    }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Discussion", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(99.dp)) {
                Text(comments.size.toString(), modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
            }
        }
        comments.forEachIndexed { index, pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
                Box(Modifier.size(32.dp).clip(CircleShape).background(PrimaryOrange.copy(alpha = 0.18f)), contentAlignment = Alignment.Center) {
                    Text(pair.first.take(1), color = PrimaryOrange, fontWeight = FontWeight.Bold)
                }
                Column(Modifier.weight(1f).clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).padding(12.dp)) {
                    Text(pair.first, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Text(if (index == 0) "2h ago" else "4h ago", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
                    Spacer(Modifier.height(4.dp))
                    Text(pair.second, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        OutlinedTextField(
            value = comment,
            onValueChange = onCommentChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(999.dp),
            placeholder = { Text("Add a comment...") },
            trailingIcon = { IconButton(onClick = {}) { Icon(Icons.Default.Send, contentDescription = "Send", tint = PrimaryOrange) } },
        )
    }
}

@Composable
private fun LessonNavBar(course: CourseDetails, lessonId: String, onLessonSelected: (String) -> Unit) {
    val lessons = course.chapters.flatMap { it.lessons }
    val currentIndex = lessons.indexOfFirst { it.id == lessonId }
    val previous = lessons.getOrNull(currentIndex - 1)
    val next = lessons.getOrNull(currentIndex + 1)
    Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { previous?.let { onLessonSelected(it.id) } },
                enabled = previous != null,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Previous")
            }
            Button(onClick = { next?.let { onLessonSelected(it.id) } }, enabled = next != null, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)) {
                Text("Next Lesson")
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Default.ChevronRight, contentDescription = null)
            }
        }
    }
}

@Composable
private fun VideoPlayer(url: String) {
    val context = LocalContext.current
    val player = remember(url) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = false
        }
    }
    DisposableEffect(player) { onDispose { player.release() } }
    Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Color.Black)) {
        AndroidView(factory = { PlayerView(it).apply { this.player = player } }, modifier = Modifier.fillMaxSize())
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.18f)))))
        Row(Modifier.align(Alignment.TopEnd).padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Default.Settings, contentDescription = null, tint = Color.White)
        }
        Row(Modifier.align(Alignment.BottomEnd).padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Default.Subtitles, contentDescription = null, tint = Color.White)
            Icon(Icons.Default.Fullscreen, contentDescription = null, tint = Color.White)
        }
        Box(Modifier.align(Alignment.Center).size(64.dp).clip(CircleShape).background(PrimaryOrange.copy(alpha = 0.92f)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(34.dp))
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryOrange) }
}

@Composable
private fun ErrorState(message: String?, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
        Text(message ?: "Something went wrong", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
    }
}

private fun LessonMaterial.icon() = when (type) {
    "VIDEO" -> Icons.Default.OndemandVideo
    "DOCUMENT" -> Icons.Default.PictureAsPdf
    "FILE" -> Icons.AutoMirrored.Filled.InsertDriveFile
    else -> Icons.Default.Description
}

private fun LessonMaterial.fileName(): String = fileUrl.substringAfterLast('/').ifBlank { "Attachment" }
private fun LessonMaterial.typeLabel(): String = if (type == "DOCUMENT") "PDF Document" else type.replace('_', ' ').lowercase().replaceFirstChar { it.uppercase() }
private fun LessonMaterial.displaySize(): String = when {
    fileSize >= 1024 * 1024 -> String.format("%.1f MB", fileSize / (1024f * 1024f))
    fileSize >= 1024 -> String.format("%.1f KB", fileSize / 1024f)
    else -> "$fileSize B"
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_6")
@Composable
private fun LessonLearningPreview() {
    SkillforgeTheme {
        LessonBody(
            course = StudentCourseMockData.courseDetails,
            lessonId = "lesson-1",
            lesson = LessonContent(
                id = "lesson-1",
                title = "Advanced Component Layouts",
                chapterTitle = "Module 2: Layouts",
                courseTitle = "Mastering UI Design with Jarvis",
                materials = listOf(
                    LessonMaterial("mat-1", "VIDEO", "https://storage.googleapis.com/exoplayer-test-media-1/mp4/android-screens-10s.mp4", 1024, "READY"),
                    LessonMaterial("mat-2", "DOCUMENT", "https://example.com/Layout_Guidelines.pdf", 2_400_000, "READY"),
                ),
            ),
            lessonLoading = false,
            lessonError = null,
            onLessonSelected = {},
        )
    }
}
