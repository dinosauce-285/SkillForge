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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.skillforge.data.remote.AdminPlatformCouponDto
import com.example.skillforge.feature.admin_portal.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@Composable
fun AdminPlatformCouponsScreen(
    token: String,
    viewModel: AdminViewModel,
    onBack: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onNavigateToCoupons: () -> Unit,
    onNavigateToFinance: () -> Unit
) {
    val coupons by viewModel.platformCoupons.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var editingCoupon by remember { mutableStateOf<AdminPlatformCouponDto?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(token) {
        viewModel.fetchPlatformCoupons(token)
    }

    AdminScaffold(
        title = "Platform Coupons",
        selectedTab = AdminTab.Coupons,
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
                isLoading && coupons.isEmpty() -> {
                    CouponLoadingCard(modifier = Modifier.align(Alignment.Center))
                }

                !error.isNullOrBlank() && coupons.isEmpty() -> {
                    CouponMessageCard(
                        title = "Unable to load coupons",
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
                            CouponHeaderCard(
                                coupons = coupons,
                                onCreateClick = { showCreateDialog = true }
                            )
                        }

                        if (!error.isNullOrBlank()) {
                            item {
                                CouponMessageCard(
                                    title = "Some coupon data may be outdated",
                                    message = error.orEmpty(),
                                    isError = true
                                )
                            }
                        }

                        if (coupons.isEmpty()) {
                            item {
                                CouponMessageCard(
                                    title = "No platform coupons yet",
                                    message = "Create a coupon to make an admin-owned discount available across paid courses.",
                                    isError = false
                                )
                            }
                        } else {
                            item {
                                Text(
                                    text = "Coupons",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            items(coupons, key = { it.id }) { coupon ->
                                PlatformCouponCard(
                                    coupon = coupon,
                                    onEdit = { editingCoupon = coupon },
                                    onDeactivate = {
                                        viewModel.deactivatePlatformCoupon(token, coupon.id)
                                    },
                                    onActivate = {
                                        viewModel.updatePlatformCoupon(
                                            token = token,
                                            id = coupon.id,
                                            isActive = true
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        PlatformCouponDialog(
            title = "Create Platform Coupon",
            actionLabel = "Create",
            coupon = null,
            onDismiss = { showCreateDialog = false },
            onConfirm = { code, discountPercent, isActive ->
                viewModel.createPlatformCoupon(token, code, discountPercent, isActive)
                showCreateDialog = false
            }
        )
    }

    editingCoupon?.let { coupon ->
        PlatformCouponDialog(
            title = "Edit Platform Coupon",
            actionLabel = "Save",
            coupon = coupon,
            onDismiss = { editingCoupon = null },
            onConfirm = { code, discountPercent, isActive ->
                viewModel.updatePlatformCoupon(
                    token = token,
                    id = coupon.id,
                    code = code,
                    discountPercent = discountPercent,
                    isActive = isActive
                )
                editingCoupon = null
            }
        )
    }
}

@Composable
private fun CouponHeaderCard(
    coupons: List<AdminPlatformCouponDto>,
    onCreateClick: () -> Unit
) {
    val activeCount = coupons.count { it.isActive }
    val inactiveCount = coupons.size - activeCount

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
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(SkillforgeSpacing.medium))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Coupon management",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Create and manage platform-wide admin coupons.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            SafeFlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalSpacing = SkillforgeSpacing.small,
                verticalSpacing = SkillforgeSpacing.small
            ) {
                CouponMetricPill(label = "Total", value = coupons.size.toString())
                CouponMetricPill(label = "Active", value = activeCount.toString())
                CouponMetricPill(label = "Inactive", value = inactiveCount.toString())
            }

            Button(
                onClick = onCreateClick,
                modifier = Modifier.fillMaxWidth(),
                shape = SkillforgeShapes.button
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(SkillforgeSpacing.small))
                Text("Create Coupon")
            }
        }
    }
}

@Composable
private fun CouponMetricPill(label: String, value: String) {
    Surface(
        modifier = Modifier.defaultMinSize(minWidth = 124.dp),
        shape = SkillforgeShapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.36f)
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = SkillforgeSpacing.medium,
                vertical = SkillforgeSpacing.small
            ),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun PlatformCouponCard(
    coupon: AdminPlatformCouponDto,
    onEdit: () -> Unit,
    onDeactivate: () -> Unit,
    onActivate: () -> Unit
) {
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
                        text = coupon.code,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Platform coupon",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.width(SkillforgeSpacing.small))
                CouponStatusBadge(isActive = coupon.isActive)
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
                        text = "${coupon.discountPercent}% off",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Discount applied at checkout",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.small)) {
                CouponMetadataRow(label = "Scope", value = coupon.scope?.toDisplayLabel() ?: "Platform")
                CouponMetadataRow(label = "Created", value = formatCouponDateTime(coupon.createdAt))
            }

            SafeFlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalSpacing = SkillforgeSpacing.small,
                verticalSpacing = SkillforgeSpacing.small
            ) {
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit")
                }
                if (coupon.isActive) {
                    OutlinedButton(
                        onClick = onDeactivate,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = SkillforgeShapes.button
                    ) {
                        Text("Deactivate")
                    }
                } else {
                    Button(
                        onClick = onActivate,
                        shape = SkillforgeShapes.button
                    ) {
                        Text("Activate")
                    }
                }
            }
        }
    }
}

@Composable
private fun CouponMetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.35f),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            modifier = Modifier.weight(0.65f),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun CouponStatusBadge(isActive: Boolean) {
    Surface(
        shape = SkillforgeShapes.chip,
        color = if (isActive) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        }
    ) {
        Text(
            text = if (isActive) "Active" else "Inactive",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (isActive) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PlatformCouponDialog(
    title: String,
    actionLabel: String,
    coupon: AdminPlatformCouponDto?,
    onDismiss: () -> Unit,
    onConfirm: (String, Int, Boolean) -> Unit
) {
    var code by remember(coupon?.id) { mutableStateOf(coupon?.code.orEmpty()) }
    var discountPercent by remember(coupon?.id) {
        mutableStateOf(coupon?.discountPercent?.toString().orEmpty())
    }
    var isActive by remember(coupon?.id) { mutableStateOf(coupon?.isActive ?: true) }
    val parsedDiscount = discountPercent.toIntOrNull()
    val canSubmit = code.isNotBlank() && parsedDiscount != null && parsedDiscount in 1..100

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.xSmall)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Platform coupons are owned by admins and apply during checkout.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
            ) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Coupon code") },
                    placeholder = { Text("SUMMER25") },
                    supportingText = { Text("Codes are normalized by the server.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = SkillforgeShapes.input
                )
                OutlinedTextField(
                    value = discountPercent,
                    onValueChange = { discountPercent = it },
                    label = { Text("Discount percent") },
                    trailingIcon = {
                        Text(
                            text = "%",
                            modifier = Modifier.padding(end = SkillforgeSpacing.medium),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    supportingText = { Text("Enter a whole number from 1 to 100.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = SkillforgeShapes.input
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = SkillforgeShapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(SkillforgeSpacing.medium),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isActive) "Active" else "Inactive",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Inactive coupons cannot be applied at checkout.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(SkillforgeSpacing.small))
                        Switch(checked = isActive, onCheckedChange = { isActive = it })
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = canSubmit,
                onClick = {
                    parsedDiscount?.let { discount ->
                        onConfirm(code, discount, isActive)
                    }
                },
                shape = SkillforgeShapes.button
            ) {
                Text(actionLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = SkillforgeShapes.large,
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Composable
private fun CouponMessageCard(
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
private fun CouponLoadingCard(modifier: Modifier = Modifier) {
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
                    text = "Loading coupons",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Fetching platform discount settings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
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

private fun formatCouponDateTime(value: String?): String {
    val rawValue = value?.trim().orEmpty()
    if (rawValue.isBlank()) return "Not available"

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
