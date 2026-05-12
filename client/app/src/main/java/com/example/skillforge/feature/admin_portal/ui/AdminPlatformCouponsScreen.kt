package com.example.skillforge.feature.admin_portal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillforge.data.remote.AdminPlatformCouponDto
import com.example.skillforge.feature.admin_portal.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
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
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingCoupon by remember { mutableStateOf<AdminPlatformCouponDto?>(null) }

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
        onBack = onBack,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = Color(0xFFAC3509),
                contentColor = Color.White,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Outlined.ConfirmationNumber, contentDescription = "Add Coupon", modifier = Modifier.size(28.dp))
            }
        }
    ) { paddingValues ->
        var isRefreshing by remember { mutableStateOf(false) }
        LaunchedEffect(isLoading) { if (!isLoading) isRefreshing = false }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { isRefreshing = true; viewModel.fetchPlatformCoupons(token) },
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(bottom = 100.dp, top = 16.dp)
            ) {
                item { AdminStatsHeader(coupons.count { it.isActive }) }

                if (isLoading && !isRefreshing && coupons.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(400.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = Color(0xFFAC3509)) }
                    }
                } else if (coupons.isEmpty()) {
                    item { AdminEmptyState() }
                } else {
                    items(coupons, key = { it.id }) { coupon ->
                        AdminPlatformCouponCard(
                            coupon = coupon,
                            onEdit = { editingCoupon = coupon },
                            onToggle = {
                                if (coupon.isActive) {
                                    viewModel.deactivatePlatformCoupon(token, coupon.id)
                                } else {
                                    viewModel.updatePlatformCoupon(token, coupon.id, isActive = true)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Dialogs
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

// ─── Stats header ─────────────────────────────────────────────────────────────

@Composable
fun AdminStatsHeader(activeCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFFF7043))
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.align(Alignment.CenterStart)) {
            Text(
                "Active Promos",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF641800)
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Manage platform-wide\ncoupons and campaigns.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF641800).copy(alpha = 0.8f),
                    lineHeight = 18.sp
                ),
                modifier = Modifier.width(180.dp)
            )
        }

        Column(
            modifier = Modifier.align(Alignment.CenterEnd),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                "$activeCount",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF641800),
                    fontSize = 56.sp
                )
            )
            Text(
                "TOTAL ACTIVE",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF641800).copy(alpha = 0.7f),
                    letterSpacing = 1.5.sp
                )
            )
        }

        Icon(
            imageVector = Icons.Outlined.LocalOffer,
            contentDescription = null,
            tint = Color(0xFF641800).copy(alpha = 0.1f),
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 40.dp, y = 40.dp)
        )
    }
}

// ─── Coupon card ──────────────────────────────────────────────────────────────

@Composable
fun AdminPlatformCouponCard(
    coupon: AdminPlatformCouponDto,
    onEdit: () -> Unit,
    onToggle: () -> Unit
) {
    val isDisabled = !coupon.isActive

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, Color(0xFFE2E2E2).copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // ── Upper part ──
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFDBD0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.ConfirmationNumber, contentDescription = null, tint = Color(0xFF8F4C37), modifier = Modifier.size(24.dp))
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Edit button
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit", tint = Color(0xFF59413A))
                    }

                    // Active toggle
                    Switch(
                        checked = coupon.isActive,
                        onCheckedChange = { onToggle() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFFFF7043),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFE2E2E2)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        coupon.code,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A1C1C),
                            letterSpacing = (-0.5).sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFF7043))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "${coupon.discountPercent}% OFF",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF641800),
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Text(
                    "Scope: ${coupon.scope ?: "Platform-wide"}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF59413A),
                        lineHeight = 18.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // ── Dashed separator ──
            AdminDashedSeparator()

            // ── Lower part ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "CREATED ON",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF59413A).copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        formatAdminDate(coupon.createdAt) ?: "Unknown",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1C1C))
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "STATUS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF59413A).copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        if (coupon.isActive) "ACTIVE" else "DISABLED",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold, 
                            color = if (coupon.isActive) Color(0xFF1A1C1C) else Color(0xFFBA1A1A)
                        )
                    )
                }
            }

            // ── Disabled banner ──
            if (isDisabled) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFBA1A1A).copy(alpha = 0.1f))
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "DISABLED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBA1A1A),
                            letterSpacing = 1.sp
                        )
                    )
                }
            }
        }
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

@Composable
fun AdminDashedSeparator() {
    Box(modifier = Modifier.fillMaxWidth().height(24.dp)) {
        Box(
            modifier = Modifier
                .size(16.dp, 24.dp)
                .align(Alignment.CenterStart)
                .offset(x = (-8).dp)
                .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                .background(Color(0xFFF9F9F9))
        )
        AdminCanvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.Center)
                .padding(horizontal = 16.dp)
        ) {
            drawLine(
                color = Color(0xFFE2E2E2),
                start = Offset(0f, 0f),
                end = Offset(size.width, 0f),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                strokeWidth = 2f
            )
        }
        Box(
            modifier = Modifier
                .size(16.dp, 24.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 8.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                .background(Color(0xFFF9F9F9))
        )
    }
}

@Composable
fun AdminCanvas(modifier: Modifier, onDraw: androidx.compose.ui.graphics.drawscope.DrawScope.() -> Unit) {
    androidx.compose.foundation.Canvas(modifier = modifier, onDraw = onDraw)
}

@Composable
fun AdminEmptyState() {
    Column(
        modifier = Modifier.fillMaxWidth().height(400.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Outlined.ConfirmationNumber, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color(0xFFE2E2E2))
        Spacer(modifier = Modifier.height(16.dp))
        Text("No coupons created yet.", style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF59413A)))
        Text("Tap the button to create a platform coupon.", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF59413A).copy(alpha = 0.6f)))
    }
}

fun formatAdminDate(dateString: String?): String? {
    if (dateString == null) return null
    return try {
        val parsers = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).also { it.timeZone = TimeZone.getTimeZone("UTC") },
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        )
        val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        parsers.firstNotNullOfOrNull { p -> runCatching { p.parse(dateString)?.let { formatter.format(it) } }.getOrNull() }
    } catch (e: Exception) { dateString }
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
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Coupon code") },
                    placeholder = { Text("SUMMER25") },
                    supportingText = { Text("Codes are normalized by the server.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = discountPercent,
                    onValueChange = { discountPercent = it },
                    label = { Text("Discount percent") },
                    trailingIcon = {
                        Text(
                            text = "%",
                            modifier = Modifier.padding(end = 16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    supportingText = { Text("Enter a whole number from 1 to 100.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
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
                        Spacer(modifier = Modifier.width(8.dp))
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
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(actionLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
