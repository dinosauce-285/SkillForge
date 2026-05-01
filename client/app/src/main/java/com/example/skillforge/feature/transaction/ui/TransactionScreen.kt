package com.example.skillforge.feature.transaction.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import coil.compose.AsyncImage
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.core.designsystem.BackgroundColor
import com.example.skillforge.core.designsystem.SkillforgeTheme
import com.example.skillforge.feature.transaction.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreenRoute(
    courseId: String,
    token: String,
    viewModel: TransactionViewModel,
    onBackClick: () -> Unit = {},
    onPaymentSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(courseId) {
        viewModel.loadCourse(courseId)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetState()
        }
    }

    LaunchedEffect(uiState.orderSuccessful) {
        if (uiState.orderSuccessful) {
            onPaymentSuccess()
        }
    }

    TransactionScreen(
        uiState = uiState,
        token = token,
        viewModel = viewModel,
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    uiState: com.example.skillforge.feature.transaction.viewmodel.TransactionUiState,
    token: String,
    viewModel: TransactionViewModel,
    onBackClick: () -> Unit = {},
) {
    // Removed local promoCode state
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !uiState.isProcessing) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryOrange)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White.copy(alpha = 0.8f))
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .navigationBarsPadding()
                ) {
                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    Button(
                        onClick = { viewModel.confirmPayment(token) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !uiState.isProcessing && uiState.course != null
                    ) {
                        if (uiState.isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Confirm & Pay", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                }
            }
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = PrimaryOrange)
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header Section
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Checkout",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    Text(
                        text = "Complete your registration to start learning now.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                // Product Section
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Product", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Surface(
                            color = Color(0xFFE0F2F1),
                            shape = CircleShape
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.Verified,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp),
                                    tint = Color(0xFF00796B)
                                )
                                Text("Secure Checkout", fontSize = 12.sp, color = Color(0xFF00796B))
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            val thumbnailUrl = uiState.course?.thumbnailUrl?.takeIf { it.isNotBlank() }
                            if (thumbnailUrl != null) {
                                AsyncImage(
                                    model = thumbnailUrl,
                                    contentDescription = uiState.course?.title,
                                    modifier = Modifier
                                        .size(96.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(96.dp)
                                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                                )
                            }
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    uiState.course?.title ?: "Loading...",
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 20.sp,
                                    maxLines = 2
                                )
                                Text("Instructor: ${uiState.course?.instructorName}", fontSize = 14.sp, color = Color.Gray)
                                Text(formatPrice(uiState.course?.price ?: 0.0), fontWeight = FontWeight.Bold, color = PrimaryOrange)
                            }
                        }
                    }
                }

                // Promo Code Section
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Promo Code", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextField(
                            value = uiState.promoCode,
                            onValueChange = { viewModel.onPromoCodeChange(it) },
                            placeholder = { Text("Enter code...", fontSize = 14.sp) },
                            modifier = Modifier.weight(1.6f),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFF3F3F4),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                        Button(
                            onClick = { viewModel.applyPromoCode() },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Apply", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (uiState.promoMessage != null) {
                        Text(
                            text = uiState.promoMessage,
                            color = if (uiState.promoApplied) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                // Payment Method Section
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, PrimaryOrange)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFFF3F3F4), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_menu_agenda),
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }
                            Text("Bank Transfer", modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                            RadioButton(selected = true, onClick = {}, colors = RadioButtonDefaults.colors(selectedColor = PrimaryOrange))
                        }
                    }
                }

                // Payment Details Section
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF3F3F4),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Payment Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", color = Color.Gray, fontSize = 14.sp)
                            Text(formatPrice(uiState.course?.price ?: 0.0), fontSize = 14.sp)
                        }
                        
                        if (uiState.discountPercent > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Promo Code (${uiState.discountPercent}% off)", color = Color(0xFF2E7D32), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text("-${formatPrice((uiState.discountPercent / 100.0) * (uiState.course?.price ?: 0.0))}", color = Color(0xFF2E7D32), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.LightGray.copy(alpha = 0.3f))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text("Total", fontWeight = FontWeight.Bold)
                            Column(horizontalAlignment = Alignment.End) {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        formatPrice((uiState.course?.price ?: 0.0) * (1 - (uiState.discountPercent / 100.0))),
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = PrimaryOrange
                                    )
                                    Text(
                                        "",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryOrange,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Trust Footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "ENCRYPTED & SECURE TRANSACTION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

private fun formatPrice(price: Double): String {
    return String.format("$%.2f", price)
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun TransactionScreenPreview() {
    SkillforgeTheme {
        TransactionScreen(
            uiState = com.example.skillforge.feature.transaction.viewmodel.TransactionUiState(),
            token = "",
            viewModel = remember { com.example.skillforge.feature.transaction.viewmodel.TransactionViewModel(
                courseRepository = TODO(),
                orderRepository = TODO(),
                couponRepository = TODO(),
            ) }
        )
    }
}
