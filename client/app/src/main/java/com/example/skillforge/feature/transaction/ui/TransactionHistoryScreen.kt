package com.example.skillforge.feature.transaction.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillforge.core.designsystem.BackgroundColor
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.core.designsystem.SkillforgeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    onBackClick: () -> Unit = {}
) {
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
            // Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = true,
                        onClick = { },
                        label = { Text("All") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryOrange,
                            selectedLabelColor = Color.White
                        ),
                        border = null,
                        shape = RoundedCornerShape(20.dp)
                    )
                }
                item {
                    FilterChip(
                        selected = false,
                        onClick = { },
                        label = { Text("Success") },
                        shape = RoundedCornerShape(20.dp)
                    )
                }
                item {
                    FilterChip(
                        selected = false,
                        onClick = { },
                        label = { Text("Pending") },
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Successful Transaction
                item {
                    TransactionCard(
                        orderId = "#ORD-88291",
                        status = "Success",
                        statusColor = Color(0xFF388E3C),
                        statusBgColor = Color(0xFFE8F5E9),
                        items = listOf(
                            TransactionItemData("Professional Communication Skills for Managers", "May 22, 2024", Icons.Default.School),
                            TransactionItemData("UI/UX Design Masterclass 2024", "May 22, 2024", Icons.Default.DesignServices)
                        ),
                        totalPrice = "$24.50",
                        actionText = "Invoice",
                        isActionPrimary = false
                    )
                }

                // Pending Transaction
                item {
                    TransactionCard(
                        orderId = "#ORD-99102",
                        status = "Pending Payment",
                        statusColor = Color(0xFFF26724),
                        statusBgColor = Color(0xFFFFF3E0),
                        items = listOf(
                            TransactionItemData("Basic Zero-Cost Marketing Course", "Just now", Icons.Default.MenuBook)
                        ),
                        totalPrice = "$8.90",
                        actionText = "Pay Now",
                        isActionPrimary = true
                    )
                }

                // Canceled Transaction
                item {
                    TransactionCard(
                        orderId = "#ORD-77621",
                        status = "Canceled",
                        statusColor = Color.Gray,
                        statusBgColor = Color(0xFFF5F5F5),
                        items = listOf(
                            TransactionItemData("Systems Thinking for Individuals", "Apr 15, 2024", Icons.Default.Psychology)
                        ),
                        totalPrice = "$12.00",
                        actionText = "Re-purchase",
                        isActionPrimary = false,
                        isCanceled = true
                    )
                }
            }
        }
    }
}

data class TransactionItemData(
    val title: String,
    val date: String,
    val icon: ImageVector
)

@Composable
fun TransactionCard(
    orderId: String,
    status: String,
    statusColor: Color,
    statusBgColor: Color,
    items: List<TransactionItemData>,
    totalPrice: String,
    actionText: String,
    isActionPrimary: Boolean,
    isCanceled: Boolean = false
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

            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFFFFF3E0), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = PrimaryOrange,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = item.title,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                        Text(
                            text = item.date,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
                if (index < items.size - 1) {
                    Spacer(modifier = Modifier.height(12.dp))
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
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(actionText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                } else {
                    if (isCanceled) {
                        TextButton(onClick = { }) {
                            Text(actionText, color = PrimaryOrange, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = { },
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

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun TransactionHistoryScreenPreview() {
    SkillforgeTheme {
        TransactionHistoryScreen()
    }
}
