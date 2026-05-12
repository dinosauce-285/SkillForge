package com.example.skillforge.feature.admin_portal.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.example.skillforge.core.designsystem.SkillforgeSpacing

enum class AdminTab {
    Dashboard,
    Users,
    Queue,
    Coupons,
    Finance
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScaffold(
    title: String,
    selectedTab: AdminTab,
    onNavigateToDashboard: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToCoupons: () -> Unit,
    onNavigateToFinance: () -> Unit,
    onLogout: (() -> Unit)? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AdminTopBar(
                title = title,
                onLogout = onLogout,
                onBack = onBack,
                actions = actions
            )
        },
        bottomBar = {
            AdminBottomNavigationBar(
                selectedTab = selectedTab,
                onNavigateToDashboard = onNavigateToDashboard,
                onNavigateToUsers = onNavigateToUsers,
                onNavigateToQueue = onNavigateToQueue,
                onNavigateToCoupons = onNavigateToCoupons,
                onNavigateToFinance = onNavigateToFinance
            )
        },
        floatingActionButton = floatingActionButton,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDetailScaffold(
    title: String,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            AdminTopBar(
                title = title,
                onLogout = null,
                onBack = onBack,
                actions = actions
            )
        },
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminTopBar(
    title: String,
    onLogout: (() -> Unit)?,
    onBack: (() -> Unit)?,
    actions: @Composable RowScope.() -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            actions()
            if (onLogout != null) {
                TextButton(onClick = onLogout) {
                    Text(
                        text = "Logout",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    )
}

@Composable
fun AdminBottomNavigationBar(
    selectedTab: AdminTab,
    onNavigateToDashboard: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToCoupons: () -> Unit,
    onNavigateToFinance: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = SkillforgeSpacing.xSmall
    ) {
        val colors = NavigationBarItemDefaults.colors(
            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )

        AdminBottomNavigationItem(
            tab = AdminTab.Dashboard,
            selectedTab = selectedTab,
            label = "Dashboard",
            icon = Icons.Default.AdminPanelSettings,
            onClick = onNavigateToDashboard,
            colors = colors
        )
        AdminBottomNavigationItem(
            tab = AdminTab.Users,
            selectedTab = selectedTab,
            label = "Users",
            icon = Icons.Default.Person,
            onClick = onNavigateToUsers,
            colors = colors
        )
        AdminBottomNavigationItem(
            tab = AdminTab.Queue,
            selectedTab = selectedTab,
            label = "Queue",
            icon = Icons.AutoMirrored.Filled.List,
            onClick = onNavigateToQueue,
            colors = colors
        )
        AdminBottomNavigationItem(
            tab = AdminTab.Coupons,
            selectedTab = selectedTab,
            label = "Coupons",
            icon = Icons.Default.Add,
            onClick = onNavigateToCoupons,
            colors = colors
        )
        AdminBottomNavigationItem(
            tab = AdminTab.Finance,
            selectedTab = selectedTab,
            label = "Finance",
            icon = Icons.AutoMirrored.Filled.List,
            onClick = onNavigateToFinance,
            colors = colors
        )
    }
}

@Composable
private fun RowScope.AdminBottomNavigationItem(
    tab: AdminTab,
    selectedTab: AdminTab,
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    colors: androidx.compose.material3.NavigationBarItemColors
) {
    NavigationBarItem(
        selected = selectedTab == tab,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) },
        colors = colors
    )
}
