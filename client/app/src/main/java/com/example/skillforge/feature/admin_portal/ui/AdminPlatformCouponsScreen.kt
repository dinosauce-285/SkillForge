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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.skillforge.data.remote.AdminPlatformCouponDto
import com.example.skillforge.feature.admin_portal.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPlatformCouponsScreen(
    token: String,
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val coupons by viewModel.platformCoupons.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var editingCoupon by remember { mutableStateOf<AdminPlatformCouponDto?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }

    LaunchedEffect(token) {
        viewModel.fetchPlatformCoupons(token)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Platform Coupons") },
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
                isLoading && coupons.isEmpty() -> {
                    LoadingStateCard(
                        message = "Loading platform coupons",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(20.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            CouponPageHeader(
                                onCreateClick = { showCreateDialog = true }
                            )
                        }

                        item {
                            CouponSummarySection(coupons = coupons)
                        }

                        if (!error.isNullOrBlank()) {
                            item {
                                MessageCard(
                                    title = "Unable to load coupons",
                                    message = error.orEmpty(),
                                    isError = true
                                )
                            }
                        }

                        if (coupons.isEmpty() && error.isNullOrBlank()) {
                            item {
                                MessageCard(
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
                                    fontWeight = FontWeight.SemiBold
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
private fun CouponPageHeader(onCreateClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Coupon management",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Create and manage platform-wide admin coupons.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Button(
            onClick = onCreateClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create Coupon")
        }
    }
}

@Composable
private fun CouponSummarySection(coupons: List<AdminPlatformCouponDto>) {
    val activeCount = coupons.count { it.isActive }
    val inactiveCount = coupons.size - activeCount

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CouponSummaryCard(label = "Total", value = coupons.size.toString())
        CouponSummaryCard(label = "Active", value = activeCount.toString())
        CouponSummaryCard(label = "Inactive", value = inactiveCount.toString())
    }
}

@Composable
private fun CouponSummaryCard(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = coupon.code,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Platform coupon",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                CouponStatusBadge(isActive = coupon.isActive)
            }

            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.40f)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "${coupon.discountPercent}% off",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Discount applied at checkout",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
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
                        )
                    ) {
                        Text("Deactivate")
                    }
                } else {
                    Button(onClick = onActivate) {
                        Text("Activate")
                    }
                }
            }
        }
    }
}

@Composable
private fun CouponStatusBadge(isActive: Boolean) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (isActive) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
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
            }
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
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Coupon Code") },
                    supportingText = { Text("Codes are normalized by the server.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = discountPercent,
                    onValueChange = { discountPercent = it },
                    label = { Text("Discount Percent") },
                    supportingText = { Text("Enter a whole number from 1 to 100.") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.32f)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(checked = isActive, onCheckedChange = { isActive = it })
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isActive) "Active" else "Inactive",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Inactive coupons cannot be applied at checkout.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
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
                }
            ) {
                Text(actionLabel)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun MessageCard(
    title: String,
    message: String,
    isError: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
private fun LoadingStateCard(
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
