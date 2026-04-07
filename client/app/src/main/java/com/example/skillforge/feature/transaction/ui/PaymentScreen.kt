package com.example.skillforge.feature.transaction.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
fun PaymentScreen(
    onBackClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thanh toán chuyển khoản", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryOrange)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Trang chủ", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.School, contentDescription = null) },
                    label = { Text("Khóa học", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = null) },
                    label = { Text("Đơn hàng", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryOrange,
                        selectedTextColor = PrimaryOrange,
                        indicatorColor = PrimaryOrange.copy(alpha = 0.1f)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Cá nhân", fontSize = 10.sp) }
                )
            }
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // Total Amount Section
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tổng số tiền cần thanh toán",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "2.450.000",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryOrange
                        )
                        Text(
                            text = "₫",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryOrange,
                            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        color = Color(0xFFE0F2F1),
                        shape = CircleShape
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = Color(0xFF00695C),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "GIAO DỊCH AN TOÀN",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00695C),
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }

            // Bank Details Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = PrimaryOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Thông tin tài khoản", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Bank Name
                        BankDetailItem(
                            label = "Ngân hàng",
                            value = "Techcombank",
                            showLogo = true
                        )
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
                        
                        // Account Number
                        BankDetailItem(
                            label = "Số tài khoản",
                            value = "1903 4567 8901 23",
                            isBold = true,
                            hasCopy = true
                        )
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
                        
                        // Account Name
                        BankDetailItem(
                            label = "Chủ tài khoản",
                            value = "NGUYEN VAN A",
                            isUppercase = true
                        )
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray.copy(alpha = 0.3f))
                        
                        // Transfer Content
                        BankDetailItem(
                            label = "Nội dung chuyển khoản",
                            value = "COURSE99281",
                            isBold = true,
                            valueColor = PrimaryOrange,
                            hasCopy = true
                        )
                    }
                }
            }

            // Instructions
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.DarkGray)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Hướng dẫn thanh toán", fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    InstructionStep(1, "Mở ứng dụng ngân hàng và chọn quét mã QR hoặc Chuyển tiền.")
                    InstructionStep(2, "Nhập chính xác số tiền và nội dung chuyển khoản như trên.")
                    InstructionStep(3, "Sau khi chuyển khoản thành công, hãy nhấn nút xác nhận bên dưới.")
                }
            }

            // QR Code Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(24.dp))
                        .border(1.dp, PrimaryOrange.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "QUÉT ĐỂ THANH TOÁN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryOrange,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .padding(8.dp)
                            .border(2.dp, PrimaryOrange, RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        // QR Code Placeholder
                        Image(
                            painter = painterResource(id = R.drawable.mock_course_thumbnail), // Replace with actual QR resource
                            contentDescription = "QR Code",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = PrimaryOrange)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Hỗ trợ tất cả ứng dụng\nngân hàng tại Việt Nam",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            lineHeight = 18.sp
                        )
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
                        Text("Xác nhận đã chuyển khoản", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                    
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Text("Thanh toán sau", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PrimaryOrange)
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun BankDetailItem(
    label: String,
    value: String,
    isBold: Boolean = false,
    isUppercase: Boolean = false,
    valueColor: Color = Color.Black,
    showLogo: Boolean = false,
    hasCopy: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Text(
                text = label.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isUppercase) value.uppercase() else value,
                fontSize = if (isBold) 18.sp else 16.sp,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
                color = valueColor
            )
        }
        if (showLogo) {
            Box(
                modifier = Modifier
                    .size(width = 48.dp, height = 32.dp)
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Bank Logo Placeholder
                Text("BANK", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            }
        }
        if (hasCopy) {
            IconButton(onClick = { }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = PrimaryOrange, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun InstructionStep(number: Int, text: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .background(PrimaryOrange.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryOrange
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = Color.DarkGray,
            lineHeight = 20.sp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, device = Devices.PIXEL_7)
@Composable
fun PaymentScreenPreview() {
    SkillforgeTheme {
        PaymentScreen()
    }
}
