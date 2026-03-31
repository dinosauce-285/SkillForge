package com.example.skillforge.feature.instructor_portal.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SkillForgePrimary = Color(0xFFD84B1E)
private val SkillForgePrimaryContainer = Color(0xFFFFEAD8)
private val SkillForgeOnPrimary = Color.White
private val SkillForgeSurfaceVariant = Color(0xFFF0F0F0)

enum class SkillforgeInstructorRoute(val title: String, val icon: ImageVector) {
    Dashboard("Dashboard", Icons.Default.Home),
    Courses("Courses", Icons.AutoMirrored.Filled.List),
    Analytics("Analytics", Icons.Default.Info),
    Account("Account", Icons.Default.AccountCircle)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillforgeInstructorDashboardScreen(
    onNavigateToCreateCourse: () -> Unit = {},
    onNavigateToUploadMaterial: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var selectedRoute by remember { mutableStateOf(SkillforgeInstructorRoute.Dashboard) }

    Scaffold(
        topBar = { SkillforgeInstructorTopBar() },
        bottomBar = {
            SkillforgeInstructorBottomBar(
                selectedRoute = selectedRoute,
                onRouteSelected = { selectedRoute = it }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateCourse, // Nút nổi cũng dẫn tới tạo khóa học
                containerColor = SkillForgePrimary,
                contentColor = SkillForgeOnPrimary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                Text("INSTRUCTOR PORTAL", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                Text(
                    "Good Morning,\nCurator.",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 40.sp
                )
            }

            // HÀNG NÚT BẤM ĐIỀU HƯỚNG CHÍNH
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        onClick = onNavigateToUploadMaterial,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Upload Files")
                    }
                    Button(
                        onClick = onNavigateToCreateCourse,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SkillForgePrimary,
                            contentColor = SkillForgeOnPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Create New")
                    }
                }
            }

            item {
                SkillforgeStatCard(
                    title = "Ready to expand?",
                    icon = Icons.Default.Star,
                    description = "Launch a new learning module or curate your existing materials into a specialized workshop.",
                    actionText = "Quick Start",
                    hasBadge = false
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SkillforgeStatCard(
                        title = "Total Students",
                        icon = Icons.Default.Person,
                        modifier = Modifier.weight(1f),
                        value = "1,284",
                        badgeText = "+12%",
                        badgeColor = SkillForgePrimaryContainer
                    )
                    SkillforgeStatCard(
                        title = "Earnings",
                        icon = Icons.Default.ShoppingCart,
                        modifier = Modifier.weight(1f),
                        value = "$42.1k",
                        badgeText = "Record",
                        badgeColor = SkillForgePrimary,
                        badgeTextColor = SkillForgeOnPrimary
                    )
                }
            }

            item {
                SkillforgeStatCard(
                    title = "Active Courses",
                    icon = Icons.AutoMirrored.Filled.List,
                    value = "14",
                    hasBadge = false
                )
            }

            item {
                SectionHeader(title = "Recent Activity", actionText = "View History")
            }

            items(3) { index ->
                SkillforgeActivityItem(index = index)
            }

            item {
                SectionHeader(title = "Performance")
            }

            item {
                SkillforgePerformanceCard()
            }
        }
    }
}

@Composable
fun SkillforgeInstructorTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Instructor Avatar",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Digital Curator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }
        BadgedBox(badge = { Badge { Text(" ") } }) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
        }
    }
}

@Composable
fun SkillforgeInstructorBottomBar(
    selectedRoute: SkillforgeInstructorRoute,
    onRouteSelected: (SkillforgeInstructorRoute) -> Unit
) {
    NavigationBar(
        containerColor = SkillForgeSurfaceVariant,
        contentColor = Color.Black
    ) {
        SkillforgeInstructorRoute.entries.forEach { route ->
            NavigationBarItem(
                selected = selectedRoute == route,
                onClick = { onRouteSelected(route) },
                label = { Text(route.title) },
                icon = { Icon(route.icon, contentDescription = null) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SkillForgePrimary,
                    selectedTextColor = SkillForgePrimary,
                    indicatorColor = SkillForgePrimaryContainer
                )
            )
        }
    }
}

@Composable
fun SkillforgeStatCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    value: String? = null,
    description: String? = null,
    actionText: String? = null,
    hasBadge: Boolean = true,
    badgeText: String? = null,
    badgeColor: Color = Color.LightGray,
    badgeTextColor: Color = Color.Black
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = SkillForgePrimary
                )
                if (hasBadge && badgeText != null) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text(badgeText, color = badgeTextColor, style = MaterialTheme.typography.labelSmall) },
                        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = badgeColor),
                        border = null
                    )
                }
            }
            Text(title, style = MaterialTheme.typography.titleLarge)
            if (value != null) {
                Text(
                    value,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = SkillForgePrimary
                )
            }
            if (description != null) {
                Text(description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            if (actionText != null) {
                Text(actionText, style = MaterialTheme.typography.labelLarge, color = SkillForgePrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SkillforgeActivityItem(index: Int) {
    val items = listOf(
        ActivityData(Icons.Default.Email, "New Discussion in\n\"Advanced Typography\"", "Elena Rossi commented on\nLesson 4", "2m ago"),
        ActivityData(Icons.Default.CheckCircle, "Project Submission", "14 students submitted \"Brand\nIdentity Design\"", "1h ago"),
        ActivityData(Icons.Default.Star, "5-Star Review Received", "\"The most comprehensive\ncourse on UI/UX yet.\"", "4h ago")
    )
    val data = items[index]

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SkillForgeSurfaceVariant, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = data.icon,
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(SkillForgePrimaryContainer)
                .padding(4.dp),
            tint = SkillForgePrimary
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(data.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(data.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Text(data.time, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

data class ActivityData(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val time: String
)

@Composable
fun SkillforgePerformanceCard() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            PerformanceMetric(title = "Course Completion", value = "78%", progress = 0.78f)
            Spacer(modifier = Modifier.height(16.dp))
            PerformanceMetric(title = "Student Retention", value = "92%", progress = 0.92f)
            Spacer(modifier = Modifier.height(24.dp))
            Text("You're in the top 5% of instructors this month.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}

@Composable
fun PerformanceMetric(title: String, value: String, progress: Float) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = SkillForgePrimary)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = SkillForgePrimary,
            trackColor = SkillForgePrimaryContainer,
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun SectionHeader(title: String, actionText: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        if (actionText != null) {
            Text(actionText, style = MaterialTheme.typography.labelLarge, color = SkillForgePrimary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SkillforgeInstructorDashboardPreview() {
    MaterialTheme {
        SkillforgeInstructorDashboardScreen()
    }
}