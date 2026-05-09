package com.example.skillforge.feature.admin_portal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.skillforge.core.designsystem.SkillforgeLayout
import com.example.skillforge.core.designsystem.SkillforgeShapes
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.components.SkillforgeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPortalScreen(
    onNavigateToUsers: () -> Unit,
    onNavigateToCourses: () -> Unit,
    onNavigateToCoupons: () -> Unit,
    onNavigateToFinance: () -> Unit,
    onLogout: () -> Unit
) {
    AdminScaffold(
        title = "Admin Dashboard",
        selectedTab = AdminTab.Dashboard,
        onNavigateToDashboard = {},
        onNavigateToUsers = onNavigateToUsers,
        onNavigateToQueue = onNavigateToCourses,
        onNavigateToCoupons = onNavigateToCoupons,
        onNavigateToFinance = onNavigateToFinance,
        onLogout = onLogout
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = SkillforgeLayout.screenHorizontalPadding,
                    vertical = SkillforgeSpacing.medium
                ),
            verticalArrangement = Arrangement.spacedBy(SkillforgeLayout.sectionGap)
        ) {
            AdminDashboardHeader()

            AdminActionSection(
                title = "Operations",
                description = "People and access controls",
            ) {
                AdminActionCard(
                    title = "Manage Users",
                    description = "Review accounts, access status, and instructor setup.",
                    icon = Icons.Default.Person,
                    onClick = onNavigateToUsers
                )
            }

            AdminActionSection(
                title = "Review",
                description = "Publishing workflow",
            ) {
                AdminActionCard(
                    title = "Course Queue",
                    description = "Review pending courses before publishing.",
                    icon = Icons.AutoMirrored.Filled.List,
                    onClick = onNavigateToCourses
                )
            }

            AdminActionSection(
                title = "Commerce",
                description = "Coupons and finance snapshots",
            ) {
                AdminActionCard(
                    title = "Platform Coupons",
                    description = "Create and manage admin-owned discounts.",
                    icon = Icons.Default.Add,
                    onClick = onNavigateToCoupons
                )
                AdminActionCard(
                    title = "Finance",
                    description = "Review snapshot-based revenue and order records.",
                    icon = Icons.AutoMirrored.Filled.List,
                    onClick = onNavigateToFinance
                )
            }
        }
    }
}

@Composable
private fun AdminDashboardHeader() {
    SkillforgeCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeLayout.cardContentPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
        ) {
            Box(
                modifier = Modifier
                    .size(SkillforgeSpacing.xxLarge)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)) {
                Text(
                    text = "Admin workspace",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Manage platform operations, coupon campaigns, and finance snapshots.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AdminActionSection(
    title: String,
    description: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        content()
    }
}

@Composable
private fun AdminActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    SkillforgeCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeLayout.cardContentPadding),
            horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(SkillforgeSpacing.xxLarge),
                shape = SkillforgeShapes.medium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(SkillforgeSpacing.small),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(SkillforgeSpacing.xSmall))
            Text(
                text = "Open",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
