package com.example.skillforge.feature.transaction.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.skillforge.core.designsystem.BackgroundColor
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.core.designsystem.SkillforgeTheme
import com.example.skillforge.feature.transaction.viewmodel.TransactionUiState
import com.example.skillforge.feature.transaction.viewmodel.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionRoute(
    sessionToken: String,
    courseId: String,
    viewModel: TransactionViewModel,
    onBackClick: () -> Unit = {},
    onCheckoutSuccess: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            kotlinx.coroutines.delay(1500)
            onCheckoutSuccess()
        }
    }

    LaunchedEffect(courseId) {
        viewModel.loadCourse(courseId)
    }

    TransactionScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onPromoCodeChange = viewModel::onPromoCodeChange,
        onApplyPromoCode = viewModel::applyPromoCode,
        onConfirmClick = { viewModel.confirmPayment(sessionToken, courseId) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    uiState: TransactionUiState,
    onBackClick: () -> Unit = {},
    onPromoCodeChange: (String) -> Unit = {},
    onApplyPromoCode: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryOrange)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White.copy(alpha = 0.8f)),
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                shadowElevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .navigationBarsPadding(),
                ) {
                    Button(
                        onClick = onConfirmClick,
                        enabled = !uiState.isLoading && !uiState.isSubmitting && uiState.course != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Confirm & Pay", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                }
            }
        },
        containerColor = BackgroundColor,
    ) { paddingValues ->
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
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            AsyncImage(
                                model = uiState.course.thumbnailUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .height(96.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(uiState.course.title, fontWeight = FontWeight.Bold, lineHeight = 20.sp)
                                Text("Instructor: ${uiState.course.instructorName}", fontSize = 14.sp, color = Color.Gray)
                                Text(vnd(uiState.basePrice), fontWeight = FontWeight.Bold, color = PrimaryOrange)
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Promo Code", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextField(
                            value = uiState.promoCode,
                            onValueChange = onPromoCodeChange,
                            placeholder = { Text("Enter code...", fontSize = 14.sp) },
                            modifier = Modifier.weight(1.6f),
                            shape = RoundedCornerShape(12.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFF3F3F4),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                            ),
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                "UI/UX Design Basic Course",
                                fontWeight = FontWeight.Bold,
                                lineHeight = 20.sp
                            )
                            Text("Instructor: Nguyen Van A", fontSize = 14.sp, color = Color.Gray)
                            Text("1,500,000 VND", fontWeight = FontWeight.Bold, color = PrimaryOrange)
                        }
                    }
                }

            // Promo Code Section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Promo Code", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(
                        value = "",
                        onValueChange = {},
                        placeholder = { Text("Enter code...", fontSize = 14.sp) },
                        modifier = Modifier.weight(1.6f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, PrimaryOrange),
                    ) {
                        Text("Apply", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                    }
                }

            // Payment Method Section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFF3F3F4),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFF3F3F4), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = android.R.drawable.ic_menu_agenda), // Replace with bank icon
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                        Text("Bank Transfer", modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                        RadioButton(selected = true, onClick = {}, colors = RadioButtonDefaults.colors(selectedColor = PrimaryOrange))
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
                        Text("1,500,000 VND", fontSize = 14.sp)
                    }
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Promo Code", color = Color(0xFF2E7D32), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Text("-200,000 VND", color = Color(0xFF2E7D32), fontSize = 14.sp, fontWeight = FontWeight.Medium)
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
                                    "1,300,000",
                                    fontSize = 30.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = PrimaryOrange
                                )
                                Text(
                                    " VND",
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
        }
    }
}

@Composable
private fun PriceRow(label: String, value: String, highlight: Boolean = false, emphasize: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = label,
            color = if (highlight) Color(0xFF2E7D32) else Color.Gray,
            fontSize = if (emphasize) 16.sp else 14.sp,
            fontWeight = if (emphasize || highlight) FontWeight.Bold else FontWeight.Normal,
        )
        Text(
            text = value,
            color = if (highlight || emphasize) PrimaryOrange else Color.Black,
            fontSize = if (emphasize) 18.sp else 14.sp,
            fontWeight = if (emphasize || highlight) FontWeight.Bold else FontWeight.Normal,
        )
    }
}

private fun vnd(amount: Double): String = "%,.0f VND".format(amount)

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun TransactionScreenPreview() {
    SkillforgeTheme {
        TransactionScreen(
            uiState = TransactionUiState(
                course = com.example.skillforge.feature.student_courses.ui.StudentCourseMockData.courseDetails,
                promoCode = "CSC13009",
                discountRate = 0.5,
            ),
        )
    }
}
