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
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.core.designsystem.SkillforgeLayout
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.SkillforgeTheme
import com.example.skillforge.data.remote.DiscussionDto
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
                discussions = lessonUiState.discussions,
                lessonLoading = lessonUiState.isLoading,
                lessonError = lessonUiState.errorMessage,
                onLessonSelected = onLessonSelected,
                onPostComment = { content, parentId ->
                    viewModel.postDiscussion(sessionToken, lessonId, content, parentId)
                },
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
    discussions: List<DiscussionDto>,
    lessonLoading: Boolean,
    lessonError: String?,
    onLessonSelected: (String) -> Unit,
    onPostComment: (String, String?) -> Unit,
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
                    Tab.DISCUSSION -> DiscussionPane(
                        lessonTitle = lesson?.title ?: "this lesson",
                        discussions = discussions, // Used here
                        onPostComment = onPostComment // Used here
                    )
                }
            }
        }
    }
}

@Composable
private fun VideoArea(lesson: LessonContent?, loading: Boolean, error: String?) {
    val video = lesson?.materials?.firstOrNull {
        it.type.equals("VIDEO", ignoreCase = true) ||
            it.fileUrl.substringBefore('?').lowercase().endsWith(".mp4") ||
            it.fileUrl.substringBefore('?').lowercase().endsWith(".m3u8")
    }
    when {
        loading && lesson == null -> LoadingState(Modifier.fillMaxWidth().height(240.dp))
        error != null && lesson == null -> ErrorState(error, Modifier.padding(16.dp))
        video != null && isYouTubeUrl(video.fileUrl) -> ExternalVideoCard(video.fileUrl)
        video != null -> VideoPlayer(video.fileUrl)
        else -> Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Color.Black), contentAlignment = Alignment.Center) {
            Text("This lesson has no video", color = Color.White.copy(alpha = 0.85f))
        }
    }
}

@Composable
private fun ExternalVideoCard(url: String) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "This video is hosted on YouTube.",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )
        Text(
            text = "Open it in YouTube or browser to watch.",
            color = Color.White.copy(alpha = 0.85f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
        )
        Button(
            onClick = {
                val videoId = extractYouTubeVideoId(url)
                val appIntent = if (videoId != null) {
                    Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
                } else {
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                }

                try {
                    context.startActivity(appIntent)
                } catch (_: Exception) {
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    } catch (_: Exception) {
                        // no-op: keep UI stable if no app can handle the URL
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
        ) {
            Text("Open Video")
        }
        Spacer(modifier = Modifier.weight(1f))
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
private fun DiscussionPane(
    lessonTitle: String,
    discussions: List<DiscussionDto>, // Pass data from ViewModel here
    onPostComment: (content: String, parentId: String?) -> Unit
) {
    var commentText by rememberSaveable { mutableStateOf("") }
    var replyingTo by remember { mutableStateOf<DiscussionDto?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Discussion", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(99.dp)) {
                Text(
                    text = discussions.size.toString(),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        if (discussions.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                Text("No discussions yet. Be the first to start one!", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            discussions.forEach { discussion ->
                DiscussionItem(
                    discussion = discussion,
                    depth = 0,
                    onReplyClick = { replyingTo = it }
                )
            }
        }

        // Input Area
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (replyingTo != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Replying to ${replyingTo!!.user.fullName}",
                            style = MaterialTheme.typography.labelMedium,
                            color = PrimaryOrange
                        )
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.clickable { replyingTo = null }
                        )
                    }
                }

                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    placeholder = { Text("Add a comment...") },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (commentText.isNotBlank()) {
                                    onPostComment(commentText, replyingTo?.id)
                                    commentText = ""
                                    replyingTo = null
                                }
                            }
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = PrimaryOrange)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun DiscussionItem(
    discussion: DiscussionDto,
    depth: Int,
    onReplyClick: (DiscussionDto) -> Unit
) {
    // Dynamic indentation: 0dp for top-level, 32dp for replies
    val maxVisualDepth = 1 // Limit indentation to 1 level (top-level and replies)
    val effectiveDepth = minOf(depth, maxVisualDepth)
    val paddingStart = (effectiveDepth * 32).dp

    Column(modifier = Modifier.fillMaxWidth().padding(start = paddingStart, top = 8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {

            // Avatar Placeholder
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(PrimaryOrange.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = discussion.user.fullName.take(1).uppercase(),
                    color = PrimaryOrange,
                    fontWeight = FontWeight.Bold
                )
            }

            // Comment Content Bubble
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 16.dp,
                        bottomEnd = 16.dp,
                        bottomStart = 16.dp
                    ))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = discussion.user.fullName,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelMedium
                    )

                    // ONLY show "Reply" button for top-level comments (depth == 0)
                    if (depth == 0) {
                        Text(
                            text = "Reply",
                            color = PrimaryOrange,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.clickable { onReplyClick(discussion) }
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))
                Text(
                    text = discussion.content,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Recursively render replies (these will have depth = 1)
        if (discussion.replies?.isNotEmpty() == true) {
            discussion.replies.forEach { reply ->
                DiscussionItem(
                    discussion = reply,
                    depth = depth + 1,
                    onReplyClick = onReplyClick
                )
            }
        }
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
    var playbackError by remember(url) { mutableStateOf<String?>(null) }
    val player = remember(url) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = false
            addListener(
                object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        playbackError = error.errorCodeName
                    }
                },
            )
        }
    }
    DisposableEffect(player) { onDispose { player.release() } }
    Box(Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Color.Black)) {
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    this.player = player
                    useController = true
                    controllerAutoShow = true
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        if (playbackError != null) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Unable to play this video ($playbackError)",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                )
                Button(
                    onClick = {
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        } catch (_: Exception) {
                            // no-op: keep UI stable if no app can handle the URL
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                ) {
                    Text("Open with another app")
                }
            }
        }
    }
}

private fun isYouTubeUrl(url: String): Boolean {
    val normalized = url.lowercase()
    return normalized.contains("youtube.com") || normalized.contains("youtu.be")
}

private fun extractYouTubeVideoId(url: String): String? {
    return when {
        url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore('?').substringBefore('&').ifBlank { null }
        url.contains("youtube.com/watch") -> url.substringAfter("v=").substringBefore('&').ifBlank { null }
        url.contains("youtube.com/shorts/") -> url.substringAfter("shorts/").substringBefore('?').substringBefore('&').ifBlank { null }
        else -> null
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
            discussions = emptyList(),
            lessonLoading = false,
            lessonError = null,
            onLessonSelected = {},
            onPostComment = { _, _ -> },
        )
    }
}
