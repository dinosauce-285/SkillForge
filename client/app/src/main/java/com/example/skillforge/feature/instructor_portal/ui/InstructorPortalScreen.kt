package com.example.skillforge.feature.instructor_portal.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.skillforge.core.designsystem.*
import com.example.skillforge.data.remote.CourseSummaryDto
import com.example.skillforge.data.remote.InstructorDashboardDto
import com.example.skillforge.data.remote.InstructorAnalyticsDto
import com.example.skillforge.feature.profile.viewmodel.ProfileViewModel
import com.example.skillforge.feature.profile.ui.ProfileScreen

enum class SkillforgeInstructorRoute(val title: String, val icon: ImageVector) {
    Dashboard("Dashboard", Icons.Default.Home),
    Courses("Courses", Icons.AutoMirrored.Filled.List),
    Coupons("Coupons", Icons.Default.Payments),
    Analytics("Analytics", Icons.Default.Info),
    Account("Account", Icons.Default.AccountCircle)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkillforgeInstructorDashboardScreen(
    courses: List<CourseSummaryDto> = emptyList(),
    analyticsData: InstructorAnalyticsDto? = null,
    token: String,
    profileViewModel: ProfileViewModel,
    dashboardData: InstructorDashboardDto? = null,
    isLoading: Boolean = false,
    onNavigateToCreateCourse: () -> Unit = {},
    onCourseClick: (String) -> Unit = {},
    onNavigateToUploadMaterial: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    var selectedRoute by remember { mutableStateOf(SkillforgeInstructorRoute.Dashboard) }

    Scaffold(
        topBar = { 
            if (selectedRoute != SkillforgeInstructorRoute.Account) {
                SkillforgeInstructorTopBar() 
            }
        },
        bottomBar = {
            SkillforgeInstructorBottomBar(
                selectedRoute = selectedRoute,
                onRouteSelected = { selectedRoute = it }
            )
        },
        floatingActionButton = {
            if (selectedRoute == SkillforgeInstructorRoute.Dashboard || selectedRoute == SkillforgeInstructorRoute.Courses) {
                FloatingActionButton(
                    onClick = onNavigateToCreateCourse,
                    containerColor = PrimaryOrange,
                    contentColor = SurfaceColor,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Course")
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize().background(BackgroundColor)) {
            when (selectedRoute) {
                SkillforgeInstructorRoute.Dashboard -> {
                    DashboardTabContent(
                        dashboardData = dashboardData,
                        onNavigateToReports = { selectedRoute = SkillforgeInstructorRoute.Analytics },
                        onNavigateToCreateCourse = onNavigateToCreateCourse
                    )
                }
                SkillforgeInstructorRoute.Courses -> {
                    CourseListTabContent(courses, isLoading, onCourseClick)
                }
                SkillforgeInstructorRoute.Coupons -> InstructorCouponScreen(token = token)
                SkillforgeInstructorRoute.Analytics -> {
                    AnalyticsTabContent(dashboardData = dashboardData, isLoading = isLoading)
                }
                SkillforgeInstructorRoute.Account -> {
                    ProfileScreen(
                        token = token,
                        viewModel = profileViewModel,
                        onLogoutClick = onLogout
                    )
                }
            }
        }
    }
}

// ... inside InstructorPortalScreen.kt

@Composable
fun DashboardTabContent(
    dashboardData: InstructorDashboardDto?,
    onNavigateToReports: () -> Unit,
    onNavigateToCreateCourse: () -> Unit
) {
    if (dashboardData == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryOrange)
        }
        return
    }

    val stats = dashboardData.stats
    val chartData = dashboardData.chartData

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("INSTRUCTOR PORTAL", fontSize = 12.sp, color = PrimaryOrange, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text("Welcome back", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.ExtraBold, color = TextPrimaryColor)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onNavigateToCreateCourse,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange, contentColor = SurfaceColor),
                    shape = RoundedCornerShape(24.dp)
                ) { Text("New Course", fontWeight = FontWeight.Bold) }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                DashboardStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Total Students",
                    value = String.format("%,d", stats.totalStudents),
                    icon = Icons.Default.People
                )
                DashboardStatCard(
                    modifier = Modifier.weight(1f),
                    title = "Active Courses",
                    value = stats.activeCourses.toString(),
                    icon = Icons.Default.School
                )
            }
        }

        item {
            DashboardStatCard(
                modifier = Modifier.fillMaxWidth(),
                title = "Total Earnings",
                value = "$${String.format("%,.2f", stats.totalEarnings)}",
                icon = Icons.Default.Payments
            )
        }

        item {
            Text("Enrollments", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimaryColor, modifier = Modifier.padding(top = 16.dp))

            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceColor)
            ) {
                Box(modifier = Modifier.padding(24.dp).fillMaxWidth().height(200.dp)) {
                    if (chartData.isEmpty()) {
                        Text("No data available", modifier = Modifier.align(Alignment.Center))
                    } else {
                        val maxCount = chartData.maxOfOrNull { it.count }?.toFloat()?.coerceAtLeast(1f) ?: 1f

                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            chartData.forEach { dataPoint ->
                                val heightFraction = (dataPoint.count.toFloat() / maxCount)

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = dataPoint.count.toString(), fontSize = 10.sp, color = TextSecondaryColor)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.6f)
                                            .fillMaxHeight(heightFraction.coerceAtLeast(0.05f))
                                            .background(PrimaryOrange, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(text = dataPoint.month, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryColor)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Updated Stat Card (Removed hardcoded badges for simplicity)
@Composable
fun DashboardStatCard(modifier: Modifier = Modifier, title: String, value: String, icon: ImageVector) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = SurfaceColor)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Surface(shape = CircleShape, color = BackgroundColor, modifier = Modifier.size(40.dp)) {
                Icon(icon, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, color = TextSecondaryColor, fontSize = 12.sp)
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = TextPrimaryColor)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseListTabContent(
    courses: List<CourseSummaryDto>,
    isLoading: Boolean,
    onCourseClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All Courses") }
    val filters = listOf("All Courses", "Published", "Drafts", "Under Review")

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Text("Curate Your Knowledge", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextPrimaryColor)
        Spacer(modifier = Modifier.height(4.dp))
        Text("Manage your educational gallery. Review performance metrics and refine your course offerings from a single editorial lens.", color = TextSecondaryColor, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search your catalog...", color = TextSecondaryColor) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondaryColor) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SearchBarBackgroundColor,
                unfocusedContainerColor = SearchBarBackgroundColor,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = PrimaryOrange
            ),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            filters.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryOrange,
                        selectedLabelColor = SurfaceColor,
                        containerColor = ChipUnselectedBackgroundColor,
                        labelColor = TextSecondaryColor
                    ),
                    border = BorderStroke(1.dp, if (selectedFilter == filter) PrimaryOrange else ChipUnselectedBorderColor),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = PrimaryOrange) }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
                val filteredCourses = courses.filter { it.title.contains(searchQuery, ignoreCase = true) }
                items(filteredCourses) { course ->
                    InstructorCourseItemCard(course = course, onClick = { onCourseClick(course.id) })
                }
            }
        }
    }
}

@Composable
fun InstructorCourseItemCard(course: CourseSummaryDto, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                AsyncImage(
                    model = course.thumbnailUrl ?: "https://via.placeholder.com/400x200?text=No+Cover",
                    contentDescription = "Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // --- DYNAMIC STATUS BADGE LOGIC ---
                val statusText = course.status.uppercase()
                val statusColor = when (statusText) {
                    "PUBLISHED" -> Color(0xFF4CAF50) // Green
                    "DRAFT" -> Color(0xFFFF9800)     // Orange
                    "ARCHIVED" -> Color(0xFF9E9E9E)  // Grey
                    else -> PrimaryOrange
                }

                Surface(
                    modifier = Modifier.align(Alignment.BottomStart).padding(12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor
                ) {
                    Text(
                        text = statusText,
                        color = SurfaceColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                // ---------------------------------

                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceColor
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = RatingStarColor, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(course.averageRating.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = course.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimaryColor)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = course.summary ?: "No description available.", style = MaterialTheme.typography.bodySmall, color = TextSecondaryColor, maxLines = 2)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Column {
                        Text("STUDENTS", fontSize = 10.sp, color = TextSecondaryColor, fontWeight = FontWeight.SemiBold)
                        Text(course.counts.enrollments.toString(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimaryColor)
                    }
                    Text(text = "Manage", color = PrimaryOrange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun AnalyticsTabContent(dashboardData: InstructorDashboardDto?, isLoading: Boolean) {
    if (isLoading || dashboardData == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryOrange)
        }
        return
    }

    val stats = dashboardData.stats
    val chartData = dashboardData.chartData

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text("PERFORMANCE OVERSIGHT", fontSize = 12.sp, color = PrimaryOrange, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text("Institutional\nIntelligence", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextPrimaryColor, lineHeight = 36.sp)
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            AnalyticsMetricCard(
                title = "Total Revenue",
                value = "$${String.format("%,.2f", stats.totalEarnings)}",
                growth = "Lifetime Earnings",
                progressColor = Color(0xFFD32F2F),
                progress = 1.0f,
                isGrowthPositive = true
            )
        }

        item {
            AnalyticsMetricCard(
                title = "Total Students",
                value = "${String.format("%,d", stats.totalStudents)}",
                growth = "Across ${stats.activeCourses} active courses",
                progressColor = Color(0xFF1976D2),
                progress = 0.8f,
                isGrowthPositive = true
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Enrollment Dynamics", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimaryColor)
                            Text("Monthly student acquisition", fontSize = 12.sp, color = TextSecondaryColor)
                        }
                        Surface(color = SearchBarBackgroundColor, shape = RoundedCornerShape(8.dp)) {
                            Text("Last 6 Months", fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp), color = TextPrimaryColor)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // --- REAL BAR CHART IMPL ---
                    Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                        if (chartData.isEmpty()) {
                            Text("No data available yet.", color = TextSecondaryColor, modifier = Modifier.align(Alignment.Center))
                        } else {
                            val maxCount = chartData.maxOfOrNull { it.count }?.toFloat()?.coerceAtLeast(1f) ?: 1f

                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                chartData.forEach { dataPoint ->
                                    val heightFraction = (dataPoint.count.toFloat() / maxCount)

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(text = dataPoint.count.toString(), fontSize = 10.sp, color = TextSecondaryColor, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(0.5f)
                                                .fillMaxHeight(heightFraction.coerceAtLeast(0.05f))
                                                .background(PrimaryOrange, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(text = dataPoint.month, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimaryColor)
                                    }
                                }
                            }
                        }
                    }
                    // ---------------------------
                }
            }
        }
    }
}

@Composable
fun AnalyticsMetricCard(title: String, value: String, growth: String, progressColor: Color, progress: Float, isGrowthPositive: Boolean = true) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, color = TextSecondaryColor, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextPrimaryColor)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = growth,
                    color = if (isGrowthPositive) Color(0xFF388E3C) else PrimaryOrange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(4.dp),
                color = progressColor,
                trackColor = SearchBarBackgroundColor,
                strokeCap = StrokeCap.Round
            )
        }
    }
}
@Composable
fun SkillforgeInstructorTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth().background(BackgroundColor).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Instructor Avatar",
                modifier = Modifier.size(44.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text("Digital Curator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimaryColor)
        }
        Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = TextPrimaryColor)
    }
}

@Composable
fun SkillforgeInstructorBottomBar(selectedRoute: SkillforgeInstructorRoute, onRouteSelected: (SkillforgeInstructorRoute) -> Unit) {
    NavigationBar(containerColor = SurfaceColor, contentColor = TextPrimaryColor, tonalElevation = 8.dp) {
        SkillforgeInstructorRoute.entries.forEach { route ->
            NavigationBarItem(
                selected = selectedRoute == route,
                onClick = { onRouteSelected(route) },
                label = { Text(route.title, fontSize = 10.sp, fontWeight = if (selectedRoute == route) FontWeight.Bold else FontWeight.Normal) },
                icon = { Icon(route.icon, contentDescription = null) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = SurfaceColor,
                    selectedTextColor = PrimaryOrange,
                    indicatorColor = PrimaryOrange,
                    unselectedIconColor = TextSecondaryColor,
                    unselectedTextColor = TextSecondaryColor
                )
            )
        }
    }
}
