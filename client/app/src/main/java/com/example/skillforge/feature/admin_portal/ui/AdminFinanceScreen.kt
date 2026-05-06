package com.example.skillforge.feature.admin_portal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.skillforge.data.remote.AdminFinanceSnapshotDto
import com.example.skillforge.data.remote.AdminFinanceSummaryDto
import com.example.skillforge.feature.admin_portal.viewmodel.AdminViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFinanceScreen(
    token: String,
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val summary by viewModel.financeSummary.collectAsState()
    val snapshots by viewModel.financeSnapshots.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(token) {
        viewModel.fetchFinance(token)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Finance") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading && summary == null && snapshots.isEmpty() -> {
                    FinanceLoadingCard(
                        message = "Loading finance snapshots",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                error != null && summary == null && snapshots.isEmpty() -> {
                    FinanceMessageCard(
                        title = "Unable to load finance data",
                        message = error.orEmpty(),
                        isError = true,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            FinancePageHeader()
                        }

                        summary?.let {
                            item {
                                FinanceSummarySection(summary = it)
                            }
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
private fun FinancePageHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Finance overview",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Review snapshot-based revenue values created during checkout.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FinanceSummarySection(summary: AdminFinanceSummaryDto) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionHeader(
            title = "Summary",
            subtitle = "Values are based on stored order financial snapshots."
        )
        FinanceMetricCard(
            title = "Gross Course Revenue",
            value = formatAmount(summary.grossRevenue),
            description = "Total customer-paid course revenue in the selected snapshot set."
        )
        FinanceMetricCard(
            title = "Platform Revenue",
            value = formatAmount(summary.netPlatformRevenue),
            description = "Platform revenue after platform coupon discount cost."
        )
        FinanceMetricCard(
            title = "Pending Instructor Revenue",
            value = formatAmount(summary.pendingInstructorBalance),
            description = "Instructor revenue not yet past the release date."
        )
        FinanceMetricCard(
            title = "Available Instructor Revenue",
            value = formatAmount(summary.availableInstructorBalance),
            description = "Instructor revenue past the release date."
        )
    }
}

@Composable
private fun FinanceMetricCard(
    title: String,
    value: String,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun FinanceSnapshotCard(snapshot: AdminFinanceSnapshotDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = snapshot.courseTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Order ${snapshot.orderId.take(8)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                SnapshotStatusBadge(text = snapshot.orderStatus)
            }

            if (!snapshot.couponCode.isNullOrBlank()) {
                Text(
                    text = "${snapshot.couponCode} - ${snapshot.couponScope ?: "Coupon"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "No coupon",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FinanceValueRow("Customer Paid", formatAmount(snapshot.customerPaidAmount))
                FinanceValueRow("Gross Course Revenue", formatAmount(snapshot.originalCoursePrice))
                FinanceValueRow("Platform Revenue", formatAmount(snapshot.platformNetRevenue))
                FinanceValueRow("Instructor Revenue", formatAmount(snapshot.instructorNetRevenue))
                FinanceValueRow(
                    "Platform Coupon Discount",
                    formatAmount(snapshot.discountAbsorbedByPlatform)
                )
                FinanceValueRow(
                    "Instructor Coupon Discount",
                    formatAmount(snapshot.discountAbsorbedByInstructor)
                )
            }
        }
    }
}

@Composable
private fun FinanceValueRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun SnapshotStatusBadge(text: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    subtitle: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
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
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = if (modifier == Modifier) 0.dp else 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
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
                color = if (isError) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
private fun FinanceLoadingCard(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.padding(20.dp)) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun formatAmount(value: Double): String {
    return String.format(Locale.US, "%,.0f VND", value)
}
