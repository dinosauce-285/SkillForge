package com.example.skillforge.feature.subscription.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skillforge.core.designsystem.BackgroundColor
import com.example.skillforge.core.designsystem.PrimaryOrange
import com.example.skillforge.core.designsystem.SurfaceColor
import com.example.skillforge.core.designsystem.TextPrimaryColor
import com.example.skillforge.core.designsystem.TextSecondaryColor
import com.example.skillforge.feature.subscription.viewmodel.InstructorSubscriptionUiState
import com.example.skillforge.feature.subscription.viewmodel.InstructorSubscriptionViewModel

private const val MOCK_PLAN_AMOUNT = "9.99"
private const val MOCK_PLAN_CURRENCY = "USD"

@Composable
fun InstructorSubscriptionRoute(
    viewModel: InstructorSubscriptionViewModel,
    onBackClick: () -> Unit,
    onSubscriptionSuccess: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoading = uiState is InstructorSubscriptionUiState.Loading

    BackHandler(enabled = isLoading) {}

    LaunchedEffect(uiState) {
        if (uiState is InstructorSubscriptionUiState.Success) {
            onSubscriptionSuccess()
        }
    }

    InstructorSubscriptionScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onConfirmClick = viewModel::confirmMockPayment,
        onDismissError = viewModel::clearError,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstructorSubscriptionScreen(
    uiState: InstructorSubscriptionUiState,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit,
    onDismissError: () -> Unit,
) {
    val isLoading = uiState is InstructorSubscriptionUiState.Loading

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = { Text("Instructor Subscription", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !isLoading) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryOrange,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceColor),
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SurfaceColor,
                shadowElevation = 8.dp,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (uiState is InstructorSubscriptionUiState.Error) {
                        Text(
                            text = uiState.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Button(
                        onClick = {
                            onDismissError()
                            onConfirmClick()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Confirm Mock Payment", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Become an Instructor",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimaryColor,
            )
            Text(
                text = "Start creating courses and managing your instructor workspace after this mock subscription is activated.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondaryColor,
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceColor),
                border = BorderStroke(1.dp, PrimaryOrange.copy(alpha = 0.25f)),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = PrimaryOrange.copy(alpha = 0.12f),
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = PrimaryOrange,
                                modifier = Modifier.padding(12.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Instructor Mock Plan", fontWeight = FontWeight.Bold)
                            Text("One-time demo activation", color = TextSecondaryColor)
                        }
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        Text("Total", color = TextSecondaryColor)
                        Text(
                            text = "$MOCK_PLAN_AMOUNT $MOCK_PLAN_CURRENCY",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryOrange,
                        )
                    }
                }
            }

            DemoPaymentNotice()

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SubscriptionBenefit("Create and manage your own courses")
                SubscriptionBenefit("Access the instructor portal immediately")
                SubscriptionBenefit("No admin approval required for this MVP")
            }
        }
    }
}

@Composable
private fun DemoPaymentNotice() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFFF3E0),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, PrimaryOrange.copy(alpha = 0.3f)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Default.Payments, contentDescription = null, tint = PrimaryOrange)
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Demo payment only. No real gateway or money transfer is used.",
                color = TextPrimaryColor,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun SubscriptionBenefit(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceColor, RoundedCornerShape(12.dp))
            .padding(PaddingValues(horizontal = 14.dp, vertical = 12.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryOrange)
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, color = TextPrimaryColor)
    }
}
