package com.example.skillforge.feature.admin_portal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.skillforge.core.designsystem.SkillforgeLayout
import com.example.skillforge.core.designsystem.SkillforgeShapes
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.core.designsystem.components.SafeFlowRow
import com.example.skillforge.core.designsystem.components.SkillforgeCard
import com.example.skillforge.data.remote.AdminFinanceSnapshotDto
import com.example.skillforge.data.remote.AdminFinanceSummaryDto
import com.example.skillforge.feature.admin_portal.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun AdminFinanceScreen(
    token: String,
    viewModel: AdminViewModel,
    onBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToCoupons: () -> Unit,
    onNavigateToFinance: () -> Unit
) {
    val summary by viewModel.financeSummary.collectAsState()
    val snapshots by viewModel.financeSnapshots.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(token) {
        viewModel.fetchFinance(token)
    }

    AdminScaffold(
        title = "Finance",
        selectedTab = AdminTab.Finance,
        onNavigateToDashboard = onNavigateToDashboard,
        onNavigateToUsers = onNavigateToUsers,
        onNavigateToQueue = onNavigateToQueue,
        onNavigateToCoupons = onNavigateToCoupons,
        onNavigateToFinance = onNavigateToFinance,
        onBack = onBack
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading && summary == null && snapshots.isEmpty() -> {
                    FinanceLoadingCard(modifier = Modifier.align(Alignment.Center))
                }

                error != null && summary == null && snapshots.isEmpty() -> {
                    FinanceMessageCard(
                        title = "Unable to load finance data",
                        message = error.orEmpty(),
                        isError = true,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(SkillforgeLayout.screenHorizontalPadding)
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = SkillforgeLayout.screenHorizontalPadding,
                            vertical = SkillforgeLayout.screenVerticalPadding
                        ),
                        verticalArrangement = Arrangement.spacedBy(SkillforgeLayout.listItemGap)
                    ) {
                        item {
                            FinanceHeaderCard(summary = summary)
                        }

                        if (!error.isNullOrBlank()) {
                            item {
                                FinanceMessageCard(
                                    title = "Some finance data could not be refreshed",
                                    message = error.orEmpty(),
                                    isError = true
                                )
                            }
                        }

                        summary?.let {
                            item {
                                FinanceSummarySection(summary = it)
                            }
                        }

                        item {
                            SectionHeader(
                                title = "Order Snapshots",
                                subtitle = "${snapshots.size} records loaded"
                            )
                        }

                        if (snapshots.isEmpty()) {
                            item {
                                FinanceMessageCard(
                                    title = "No financial snapshots yet",
                                    message = "Snapshots will appear after checkout creates order finance records.",
                                    isError = false
                                )
                            }
                        } else {
                            items(snapshots, key = { it.id }) { snapshot ->
                                FinanceSnapshotCard(snapshot = snapshot)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FinanceHeaderCard(summary: AdminFinanceSummaryDto?) {
    SkillforgeCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(SkillforgeShapes.large)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(SkillforgeSpacing.medium))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Finance overview",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Review snapshot-based revenue values created during checkout.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = SkillforgeShapes.medium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
            ) {
                Column(
                    modifier = Modifier.padding(SkillforgeSpacing.medium),
                    verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)
                ) {
                    Text(
                        text = "Platform Revenue",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = summary?.netPlatformRevenue?.let { formatAmount(it) } ?: "Not available",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Net platform revenue after platform coupon discount cost.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun FinanceSummarySection(summary: AdminFinanceSummaryDto) {
    Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small)) {
        SectionHeader(
            title = "Summary",
            subtitle = "Supporting values from stored order financial snapshots."
        )
        SafeFlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalSpacing = SkillforgeSpacing.small,
            verticalSpacing = SkillforgeSpacing.small
        ) {
            FinanceMetricPill(
                title = "Gross Course Revenue",
                value = formatAmount(summary.grossRevenue)
            )
            FinanceMetricPill(
                title = "Pending Instructor Revenue",
                value = formatAmount(summary.pendingInstructorBalance)
            )
            FinanceMetricPill(
                title = "Available Instructor Revenue",
                value = formatAmount(summary.availableInstructorBalance)
            )
        }
    }
}

@Composable
private fun FinanceMetricPill(
    title: String,
    value: String
) {
    Surface(
        modifier = Modifier.defaultMinSize(minWidth = 152.dp),
        shape = SkillforgeShapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = SkillforgeSpacing.medium,
                vertical = SkillforgeSpacing.small
            ),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FinanceSnapshotCard(snapshot: AdminFinanceSnapshotDto) {
    SkillforgeCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)
                ) {
                    Text(
                        text = snapshot.courseTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Order ${snapshot.orderId}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    snapshot.orderCreatedAt?.takeIf { it.isNotBlank() }?.let { createdAt ->
                        Text(
                            text = formatFinanceDateTime(createdAt),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.width(SkillforgeSpacing.small))
                SnapshotStatusBadge(text = snapshot.orderStatus)
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = SkillforgeShapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f)
            ) {
                Column(
                    modifier = Modifier.padding(SkillforgeSpacing.medium),
                    verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)
                ) {
                    Text(
                        text = "Customer Paid",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = formatAmount(snapshot.customerPaidAmount),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            SafeFlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalSpacing = SkillforgeSpacing.small,
                verticalSpacing = SkillforgeSpacing.small
            ) {
                SnapshotValuePill(
                    label = "Platform Revenue",
                    value = formatAmount(snapshot.platformNetRevenue)
                )
                SnapshotValuePill(
                    label = "Instructor Revenue",
                    value = formatAmount(snapshot.instructorNetRevenue)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small)) {
                FinanceDetailRow("Gross Course Revenue", formatAmount(snapshot.originalCoursePrice))
                FinanceDetailRow("Coupon", snapshot.couponLabel())
                FinanceDetailRow("Total Discount", formatAmount(snapshot.discountAmount))
                FinanceDetailRow(
                    "Discount Split",
                    "Platform ${formatAmount(snapshot.discountAbsorbedByPlatform)} / Instructor ${formatAmount(snapshot.discountAbsorbedByInstructor)}"
                )
                FinanceDetailRow(
                    "Share Rate",
                    "Platform ${snapshot.platformShareRate}% / Instructor ${snapshot.instructorShareRate}%"
                )
                snapshot.pendingReleaseDate?.takeIf { it.isNotBlank() }?.let { releaseDate ->
                    FinanceDetailRow("Pending Release", formatFinanceDateTime(releaseDate))
                }
            }
        }
    }
}

@Composable
private fun SnapshotValuePill(label: String, value: String) {
    Surface(
        modifier = Modifier.defaultMinSize(minWidth = 152.dp),
        shape = SkillforgeShapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = SkillforgeSpacing.medium,
                vertical = SkillforgeSpacing.small
            ),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun FinanceDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.42f),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(SkillforgeSpacing.small))
        Text(
            text = value,
            modifier = Modifier.weight(0.58f),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SnapshotStatusBadge(text: String) {
    Surface(
        shape = SkillforgeShapes.chip,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    ) {
        Text(
            text = text.toDisplayLabel(),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FinanceMessageCard(
    title: String,
    message: String,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    SkillforgeCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeLayout.cardContentPadding),
            verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FinanceLoadingCard(modifier: Modifier = Modifier) {
    SkillforgeCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(SkillforgeLayout.screenHorizontalPadding)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SkillforgeLayout.cardContentPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(SkillforgeSpacing.medium))
            Column {
                Text(
                    text = "Loading finance snapshots",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Fetching revenue summary and order records.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun AdminFinanceSnapshotDto.couponLabel(): String {
    return if (!couponCode.isNullOrBlank()) {
        "${couponCode} - ${couponScope?.toDisplayLabel() ?: "Coupon"}"
    } else {
        "No coupon"
    }
}

private fun String.toDisplayLabel(): String {
    return lowercase()
        .split("_", "-", " ")
        .filter { it.isNotBlank() }
        .joinToString(" ") { part ->
            part.replaceFirstChar { char -> char.uppercase() }
        }
}

private fun formatAmount(value: Double): String {
    return String.format(Locale.US, "%,.0f VND", value)
}

private fun formatFinanceDateTime(value: String?): String {
    val rawValue = value?.trim().orEmpty()
    if (rawValue.isBlank()) return "Unknown date"

    val inputFormats = listOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        },
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        },
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US),
        SimpleDateFormat("yyyy-MM-dd", Locale.US)
    )
    val parsedDate = inputFormats.firstNotNullOfOrNull { format ->
        runCatching { format.parse(rawValue) }.getOrNull()
    } ?: return rawValue

    val outputPattern = if (rawValue.contains("T")) {
        "dd/MM/yyyy, HH:mm"
    } else {
        "dd/MM/yyyy"
    }
    return SimpleDateFormat(outputPattern, Locale.getDefault()).format(parsedDate)
}
