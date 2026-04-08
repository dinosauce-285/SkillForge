package com.example.skillforge.feature.transaction.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillforge.R
import com.example.skillforge.core.designsystem.BackgroundColor
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.core.designsystem.SkillforgeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order Details", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryOrange)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Status Header
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(PrimaryOrange.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = PrimaryOrange,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Payment Successful",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    Text(
                        text = "Order ID: #ORD-88291",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Transaction Info Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "TRANSACTION INFO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryOrange,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Date & Time", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                                Text("May 14, 2024 • 14:30", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Method", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Credit Card", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }

            // Purchased Items
            item {
                Column {
                    Text(
                        text = "PURCHASED ITEMS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryOrange,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PurchasedItemRow(
                        title = "Professional Communication Skills",
                        instructor = "Le Hong Nam",
                        price = "950,000 VND",
                        imageRes = R.drawable.mock_course_thumbnail
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PurchasedItemRow(
                        title = "UI/UX Design Masterclass 2024",
                        instructor = "Sarah Chen",
                        price = "1,800,000 VND",
                        imageRes = R.drawable.mock_course_thumbnail
                    )
                }
            }

            // Summary Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.3f))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal", color = Color.Gray, fontSize = 14.sp)
                        Text("2,750,000 VND", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Discount (MAYPROMO)", color = Color(0xFF00796B), fontSize = 14.sp)
                        Text("-300,000 VND", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color(0xFF00796B))
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("TOTAL PAYMENT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
                            Text(
                                text = "2,450,000 VND",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = PrimaryOrange
                            )
                        }
                    }
                }
            }

            // Action Buttons
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Download Invoice (PDF)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Icon(Icons.Default.HelpOutline, contentDescription = null, tint = Color.DarkGray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Contact Support", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.DarkGray)
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun PurchasedItemRow(
    title: String,
    instructor: String,
    price: String,
    imageRes: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 2)
            Text(text = "Instructor: $instructor", fontSize = 12.sp, color = Color.Gray)
        }
        Text(text = price, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun TransactionDetailScreenPreview() {
    SkillforgeTheme {
        TransactionDetailScreen()
    }
}
