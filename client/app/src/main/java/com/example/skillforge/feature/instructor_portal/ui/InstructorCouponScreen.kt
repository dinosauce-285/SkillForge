package com.example.skillforge.feature.instructor_portal.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.feature.instructor_portal.viewmodel.CouponViewModel
import com.example.skillforge.feature.instructor_portal.viewmodel.CouponViewModelFactory
import com.example.skillforge.core.di.AppContainer
import androidx.compose.ui.platform.LocalContext
import com.example.skillforge.SkillforgeApplication

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

    var showDialog by remember { mutableStateOf(false) }
    var code by remember { mutableStateOf("") }
    var discountPercent by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchCoupons()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = PrimaryOrange
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Coupon")
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (coupons.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No coupons created yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(coupons) { coupon ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(coupon.code, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text("${coupon.discountPercent}% Discount", style = MaterialTheme.typography.bodyMedium)
                                Text(if (coupon.isActive) "Active" else "Inactive", color = if (coupon.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                            }
                            IconButton(onClick = { viewModel.deleteCoupon(coupon.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Coupon", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Create New Coupon") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it },
                            label = { Text("Coupon Code") }
                        )
                        OutlinedTextField(
                            value = discountPercent,
                            onValueChange = { discountPercent = it },
                            label = { Text("Discount Percent (1-100)") }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val percent = discountPercent.toIntOrNull()
                        if (code.isNotBlank() && percent != null && percent in 1..100) {
                            viewModel.createCoupon(code, percent, true)
                            showDialog = false
                            code = ""
                            discountPercent = ""
                        }
                    }) {
                        Text("Create")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
