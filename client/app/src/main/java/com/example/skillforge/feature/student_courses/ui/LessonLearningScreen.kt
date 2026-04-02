package com.example.skillforge.feature.student_courses.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.core.designsystem.SkillforgeLayout
import com.example.skillforge.core.designsystem.SkillforgeShapes
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.SkillforgeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonLearningScreen(
    onNavigateBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mastering UI Design with Jarvis",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            LessonBottomNavigation()
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Video Player Section
            item {
                VideoPlayerSection()
            }

            // Lesson Info
            item {
                LessonInfoSection()
            }

            // Tabs
            item {
                LessonTabsSection()
            }

            // Content Area
            item {
                LessonContentArea()
            }

            // Space at the bottom for safety
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun VideoPlayerSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .background(Color.Black)
    ) {
        // Thumbnail Image
        AsyncImage(
            model = com.example.skillforge.R.drawable.mock_course_thumbnail,
            placeholder = androidx.compose.ui.res.painterResource(id = com.example.skillforge.R.drawable.mock_course_thumbnail),
            contentDescription = "Video Thumbnail",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.8f
        )

        // Player Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.2f))
                .padding(SkillforgeSpacing.small)
        ) {
            // Settings Icon
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clickable { /* TODO */ }
            )

            // Play Button Middle
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(PrimaryOrange)
                    .clickable { /* TODO */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            // Bottom Controls
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                // Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.3f))
                        .clickable { /* TODO */ }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = 0.33f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        // Track
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(PrimaryOrange)
                        )
                        // Thumb
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .offset(x = 8.dp) // shift center to the end
                                .clip(CircleShape)
                                .background(PrimaryOrange)
                                .border(2.dp, Color.White, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "04:22 / 12:45",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Icon(
                            imageVector = Icons.Default.ClosedCaption,
                            contentDescription = "CC",
                            tint = Color.White,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { /* TODO */ }
                        )
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Fullscreen",
                            tint = Color.White,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { /* TODO */ }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LessonInfoSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SkillforgeLayout.screenHorizontalPadding)
            .padding(top = SkillforgeSpacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // Module Tag
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(PrimaryOrange.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "MODULE 2: LAYOUTS",
                    color = PrimaryOrange,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Lesson 4: Advanced Component Layouts",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { /* TODO */ }
        ) {
            Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = "Save",
                tint = PrimaryOrange
            )
            Text(
                text = "SAVE",
                color = PrimaryOrange,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun LessonTabsSection() {
    val tabs = listOf("Lesson Content", "Curriculum", "Discussion")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    TabRow(
        selectedTabIndex = selectedTabIndex,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = PrimaryOrange,
        divider = {
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
        }
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = selectedTabIndex == index
            Tab(
                selected = isSelected,
                onClick = { selectedTabIndex = index },
                text = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        color = if (isSelected) PrimaryOrange else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }
}

@Composable
fun LessonContentArea() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(SkillforgeLayout.screenHorizontalPadding)
            .padding(top = SkillforgeSpacing.medium)
    ) {
        // Text Content
        Text(
            text = "In this lesson, we delve into the complexities of building scalable component systems within the Jarvis Design System. We'll explore how to handle responsive breakpoints and dynamic content sizes while maintaining visual consistency.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 24.sp
        )

        Spacer(modifier = Modifier.height(SkillforgeSpacing.large))

        Text(
            text = "Key Takeaways",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(SkillforgeSpacing.small))

        val takeaways = listOf(
            "Understanding Flexbox vs Grid for component shells.",
            "Implementing container queries for truly portable components.",
            "Strategies for handling variable text lengths in headers."
        )

        takeaways.forEach { text ->
            Row(
                modifier = Modifier.padding(bottom = 12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PrimaryOrange,
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = 2.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(SkillforgeSpacing.medium))

        // PDF Card
        LessonPdfCard()

        Spacer(modifier = Modifier.height(SkillforgeSpacing.xLarge))

        // Discussion Area
        LessonDiscussionArea()
    }
}

@Composable
fun LessonPdfCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(SkillforgeShapes.card)
            .background(PrimaryOrange.copy(alpha = 0.05f))
            .border(1.dp, PrimaryOrange.copy(alpha = 0.2f), SkillforgeShapes.card)
            .padding(SkillforgeSpacing.medium)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(SkillforgeShapes.medium)
                            .background(PrimaryOrange.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF",
                            tint = PrimaryOrange
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Layout_Guidelines.pdf",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "2.4 MB • PDF Document",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = { /* TODO */ },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    shape = SkillforgeShapes.medium,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Download", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }

            Spacer(modifier = Modifier.height(SkillforgeSpacing.medium))

            // Preview Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .clip(SkillforgeShapes.medium)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = com.example.skillforge.R.drawable.mock_course_thumbnail,
                    placeholder = androidx.compose.ui.res.painterResource(id = com.example.skillforge.R.drawable.mock_course_thumbnail),
                    contentDescription = "PDF Preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    alpha = 0.5f
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Preview",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap to preview document",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun LessonDiscussionArea() {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Discussion",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "12",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(SkillforgeSpacing.medium))

        // Comment 1
        CommentItem(
            name = "Alex Rivera",
            time = "2H AGO",
            text = "This was so helpful! Especially the part about container queries. Can we get more examples for nested layouts?",
            avatarUrl = com.example.skillforge.R.drawable.mock_course_thumbnail
        )
        
        Spacer(modifier = Modifier.height(SkillforgeSpacing.medium))

        // Comment 2
        CommentItem(
            name = "Sarah Chen",
            time = "4H AGO",
            text = "The PDF resource link isn't loading for me. Anyone else having this issue?",
            avatarUrl = com.example.skillforge.R.drawable.mock_course_thumbnail
        )

        Spacer(modifier = Modifier.height(SkillforgeSpacing.large))

        // Input Field
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Add a comment...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = PrimaryOrange,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { /* TODO */ }
            )
        }
    }
}

@Composable
fun CommentItem(name: String, time: String, text: String, avatarUrl: Any) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        AsyncImage(
            model = avatarUrl,
            placeholder = androidx.compose.ui.res.painterResource(id = com.example.skillforge.R.drawable.mock_course_thumbnail),
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 12.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 12.dp))
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = name, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                Text(text = time, style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun LessonBottomNavigation() {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shadowElevation = 8.dp,
        shape = SkillforgeShapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = SkillforgeShapes.medium,
                modifier = Modifier.weight(1f).height(48.dp),
                border = borderStroke()
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Previous")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Previous", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryOrange,
                    contentColor = Color.White
                ),
                shape = SkillforgeShapes.medium,
                modifier = Modifier.weight(1f).height(48.dp)
            ) {
                Text("Next Lesson", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ChevronRight, contentDescription = "Next")
            }
        }
    }
}

@Composable
private fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)

@Preview(name = "Lesson Learning Screen", showBackground = true, showSystemUi = true, device = "id:pixel_6")
@Composable
fun LessonLearningScreenPreview() {
    SkillforgeTheme {
        LessonLearningScreen()
    }
}