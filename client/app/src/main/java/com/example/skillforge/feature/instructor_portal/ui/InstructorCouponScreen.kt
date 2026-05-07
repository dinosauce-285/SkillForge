package com.example.skillforge.feature.instructor_portal.ui

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skillforge.SkillforgeApplication
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.data.remote.CouponDto
import com.example.skillforge.feature.instructor_portal.viewmodel.CouponViewModel
import com.example.skillforge.feature.instructor_portal.viewmodel.CouponViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructorCouponScreen(
    token: String,
    viewModel: CouponViewModel = viewModel(
        factory = CouponViewModelFactory(
            (LocalContext.current.applicationContext as SkillforgeApplication).container.couponRepository
        )
    )
) {
    val coupons by viewModel.coupons.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // null  → list view
    // non-null CouponDto → edit mode for that coupon
    // special sentinel: use a state that distinguishes "add" vs "edit"
    var editTarget by remember { mutableStateOf<CouponDto?>(null) }
    var showAddScreen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.fetchCoupons() }

    // ── Full-screen overlays (cover the whole tab area) ──────────────────────
    if (showAddScreen) {
        AddCouponScreen(
            onBack = { showAddScreen = false },
            onConfirm = { code, discount, desc, max, expiry ->
                viewModel.createCoupon(code, discount, desc, max, expiry, true)
                showAddScreen = false
            }
        )
        return
    }

    if (editTarget != null) {
        val coupon = editTarget!!
        AddCouponScreen(
            initialCoupon = coupon,
            onBack = { editTarget = null },
            onConfirm = { code, discount, desc, max, expiry ->
                viewModel.updateCoupon(coupon.id, code, discount, desc, max, expiry)
                editTarget = null
            }
        )
        return
    }

    // ── List view ─────────────────────────────────────────────────────────────
    Scaffold(
        containerColor = Color(0xFFF9F9F9),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddScreen = true },
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
            onRefresh = { isRefreshing = true; viewModel.fetchCoupons() },
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                contentPadding = PaddingValues(bottom = 100.dp, top = 16.dp)
            ) {
                item { StatsHeader(coupons.count { it.isActive }) }

                if (isLoading && !isRefreshing && coupons.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(400.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(color = Color(0xFFAC3509)) }
                    }
                } else if (coupons.isEmpty()) {
                    item { EmptyState() }
                } else {
                    items(coupons, key = { it.id }) { coupon ->
                        CouponCard(
                            coupon = coupon,
                            onEdit = { editTarget = coupon },
                            onToggle = { viewModel.toggleCoupon(coupon.id) },
                            onDelete = { viewModel.deleteCoupon(coupon.id) }
                        )
                    }
                }
            }
        }
    }
}

// ─── Stats header ─────────────────────────────────────────────────────────────

@Composable
fun StatsHeader(activeCount: Int) {
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
                "Manage your seasonal campaigns\nand student discounts.",
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
fun CouponCard(
    coupon: CouponDto,
    onEdit: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val isExpired = coupon.expiresAt != null && runCatching {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).also { it.timeZone = TimeZone.getTimeZone("UTC") }
            .parse(coupon.expiresAt)?.before(Date())
    }.getOrNull() == true

    val isDisabled = !coupon.isActive || isExpired

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
                    coupon.description ?: "No description provided.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF59413A),
                        lineHeight = 18.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // ── Dashed separator ──
            DashedSeparator()

            // ── Lower part ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .let { if (isDisabled) it else it },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "REDEMPTIONS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF59413A).copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "${coupon.usedCount}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1A1C1C))
                        )
                        Text(
                            " / ${coupon.maxUses ?: "∞"} uses left",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF59413A)),
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "EXPIRES ON",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF59413A).copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        formatDate(coupon.expiresAt) ?: "Never",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1C1C))
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
                        if (isExpired) "EXPIRED" else "DISABLED",
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

// ─── Dashed separator ─────────────────────────────────────────────────────────

@Composable
fun DashedSeparator() {
    Box(modifier = Modifier.fillMaxWidth().height(24.dp)) {
        Box(
            modifier = Modifier
                .size(16.dp, 24.dp)
                .align(Alignment.CenterStart)
                .offset(x = (-8).dp)
                .clip(RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp))
                .background(Color(0xFFF9F9F9))
        )
        Canvas(
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
fun Canvas(modifier: Modifier, onDraw: androidx.compose.ui.graphics.drawscope.DrawScope.() -> Unit) {
    androidx.compose.foundation.Canvas(modifier = modifier, onDraw = onDraw)
}

// ─── Empty state ──────────────────────────────────────────────────────────────

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxWidth().height(400.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Outlined.ConfirmationNumber, contentDescription = null, modifier = Modifier.size(80.dp), tint = Color(0xFFE2E2E2))
        Spacer(modifier = Modifier.height(16.dp))
        Text("No coupons created yet.", style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFF59413A)))
        Text("Tap + to create your first promotion.", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF59413A).copy(alpha = 0.6f)))
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

fun formatDate(dateString: String?): String? {
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
