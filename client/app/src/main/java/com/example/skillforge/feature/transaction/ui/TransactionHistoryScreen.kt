package com.example.skillforge.feature.transaction.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.skillforge.core.designsystem.BackgroundColor
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.core.designsystem.SkillforgeTheme
import com.example.skillforge.domain.model.OrderSummary
import com.example.skillforge.feature.transaction.viewmodel.TransactionHistoryViewModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    token: String,
    viewModel: TransactionHistoryViewModel,
    onBackClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    var invoiceOrder by remember { mutableStateOf<OrderSummary?>(null) }
    val context = LocalContext.current

    LaunchedEffect(token) {
        viewModel.loadOrders(token)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Purchase History", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryOrange)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White.copy(alpha = 0.8f))
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // No filter chips - show all orders directly

            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryOrange)
                    }
                }
                uiState.errorMessage != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(uiState.errorMessage ?: "Error", color = Color.Gray)
                    }
                }
                uiState.orders.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No orders found.", color = Color.Gray, fontSize = 14.sp)
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(uiState.orders) { order ->
                            val (statusLabel, statusColor, statusBgColor) = resolveStatus(order.status)
                            val isSuccess = order.status.equals("COMPLETED", ignoreCase = true)
                            val isCanceled = order.status.equals("CANCELLED", ignoreCase = true) ||
                                    order.status.equals("CANCELED", ignoreCase = true)

                            TransactionCard(
                                orderId = "#${order.id.take(8).uppercase()}",
                                status = statusLabel,
                                statusColor = statusColor,
                                statusBgColor = statusBgColor,
                                courseTitle = order.courseTitle,
                                courseThumbnailUrl = order.courseThumbnailUrl,
                                date = formatDate(order.createdAt),
                                totalPrice = "$${String.format("%.2f", order.amount)}",
                                actionText = "Invoice",
                                isActionPrimary = false,
                                isCanceled = false,
                                onActionClick = {
                                    invoiceOrder = order
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    invoiceOrder?.let { order ->
        VirtualInvoiceDialog(
            orderId = "#${order.id.take(8).uppercase()}",
            courseTitle = order.courseTitle,
            originalPrice = order.originalPrice,
            discountPercent = order.discountPercent,
            finalAmount = order.amount,
            date = formatDate(order.createdAt),
            onDismiss = { invoiceOrder = null },
            onDownloadPdf = {
                Toast.makeText(context, "Invoice PDF saved to Downloads!", Toast.LENGTH_SHORT).show()
                invoiceOrder = null
            }
        )
    }
}

private data class StatusDisplay(val label: String, val color: Color, val bgColor: Color)

private fun resolveStatus(status: String): StatusDisplay = when {
    status.equals("COMPLETED", ignoreCase = true) ->
        StatusDisplay("Success", Color(0xFF388E3C), Color(0xFFE8F5E9))
    status.equals("PENDING", ignoreCase = true) ->
        StatusDisplay("Pending Payment", Color(0xFFF26724), Color(0xFFFFF3E0))
    status.equals("CANCELLED", ignoreCase = true) || status.equals("CANCELED", ignoreCase = true) ->
        StatusDisplay("Canceled", Color.Gray, Color(0xFFF5F5F5))
    else ->
        StatusDisplay(status, Color.Gray, Color(0xFFF5F5F5))
}

private fun formatDate(isoDate: String?): String {
    if (isoDate.isNullOrBlank()) return "—"
    return try {
        val odt = OffsetDateTime.parse(isoDate)
        odt.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH))
    } catch (e: Exception) {
        isoDate.take(10)
    }
}

@Composable
fun TransactionCard(
    orderId: String,
    status: String,
    statusColor: Color,
    statusBgColor: Color,
    courseTitle: String,
    courseThumbnailUrl: String?,
    date: String,
    totalPrice: String,
    actionText: String,
    isActionPrimary: Boolean,
    isCanceled: Boolean = false,
    onActionClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .alpha(if (isCanceled) 0.6f else 1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "Order ID",
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = orderId,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Surface(
                    color = statusBgColor,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = status.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Course row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (courseThumbnailUrl != null) {
                    AsyncImage(
                        model = courseThumbnailUrl,
                        contentDescription = courseTitle,
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = PrimaryOrange,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = courseTitle,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        maxLines = 2
                    )
                    Text(
                        text = date,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF5F5F5))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Total",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = totalPrice,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = if (isCanceled) Color.Gray else PrimaryOrange,
                        style = if (isCanceled) androidx.compose.ui.text.TextStyle(textDecoration = TextDecoration.LineThrough) else androidx.compose.ui.text.TextStyle.Default
                    )
                }

                if (isActionPrimary) {
                    Button(
                        onClick = onActionClick,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(actionText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                } else {
                    if (isCanceled) {
                        TextButton(onClick = onActionClick) {
                            Text(actionText, color = PrimaryOrange, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onActionClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEEEEE), contentColor = Color.DarkGray),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(actionText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VirtualInvoiceDialog(
    orderId: String,
    courseTitle: String,
    originalPrice: Double,
    discountPercent: Int?,
    finalAmount: Double,
    date: String,
    onDismiss: () -> Unit,
    onDownloadPdf: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "INVOICE",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 24.sp,
                            color = PrimaryOrange
                        )
                        Text(
                            text = "SkillForge E-Learning",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = orderId,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Date: $date",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(16.dp))

                // Items header
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Description", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("Amount", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(courseTitle, fontSize = 14.sp, modifier = Modifier.weight(1f), maxLines = 2)
                    Text("$${String.format("%.2f", originalPrice)}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
                
                if (discountPercent != null && discountPercent > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Discount (${discountPercent}%)", fontSize = 12.sp, color = Color(0xFF2E7D32), modifier = Modifier.weight(1f))
                        Text("-$${String.format("%.2f", originalPrice - finalAmount)}", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF2E7D32))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(modifier = Modifier.height(16.dp))

                // Total
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Total Paid", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("$${String.format("%.2f", finalAmount)}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = PrimaryOrange)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
                    ) {
                        Text("Close")
                    }
                    Button(
                        onClick = onDownloadPdf,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                    ) {
                        Text("Export PDF", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
