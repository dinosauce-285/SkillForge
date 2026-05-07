package com.example.skillforge.feature.instructor_portal.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.skillforge.data.remote.CouponDto
import java.text.SimpleDateFormat
import java.util.*

// Colors mapped from design tokens
private val Primary = Color(0xFFAC3509)
private val PrimaryContainer = Color(0xFFFF7043)
private val OnSurface = Color(0xFF1A1C1C)
private val OnSurfaceVariant = Color(0xFF59413A)
private val SurfaceContainerLow = Color(0xFFF3F3F4)
private val SurfaceContainerLowest = Color(0xFFFFFFFF)
private val TertiaryContainer = Color(0xFF00ACBB)
private val OnTertiaryContainer = Color(0xFF003A3F)
private val Background = Color(0xFFF9F9F9)
private val ErrorColor = Color(0xFFBA1A1A)

// ---- Validation rules (synced with server CreateCouponDto) ----
private fun validateCode(code: String): String? = when {
    code.isBlank() -> "Coupon code is required"
    code.contains(' ') -> "Coupon code must not contain spaces"
    else -> null
}

private fun validateDiscount(discount: String): String? {
    val v = discount.toIntOrNull()
    return when {
        discount.isBlank() -> "Discount percentage is required"
        v == null -> "Must be a whole number"
        v < 1 -> "Must be greater than 0"
        v > 99 -> "Must be less than 100"
        else -> null
    }
}

private fun validateMaxUses(maxUses: String): String? {
    if (maxUses.isBlank()) return null
    val v = maxUses.toIntOrNull()
    return when {
        v == null -> "Must be a whole number"
        v < 1 -> "Must be at least 1"
        else -> null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCouponScreen(
    initialCoupon: CouponDto? = null,
    onBack: () -> Unit,
    onConfirm: (code: String, discountPercent: Int, description: String?, maxUses: Int?, expiresAt: String?) -> Unit
) {
    val isEditMode = initialCoupon != null

    // Pre-fill from existing coupon when editing
    var code by remember { mutableStateOf(initialCoupon?.code ?: "") }
    var discount by remember { mutableStateOf(initialCoupon?.discountPercent?.toString() ?: "") }
    var description by remember { mutableStateOf(initialCoupon?.description ?: "") }
    var maxUses by remember { mutableStateOf(initialCoupon?.maxUses?.toString() ?: "") }
    // Convert ISO date to yyyy-MM-dd for display
    var expiresAt by remember {
        mutableStateOf(
            initialCoupon?.expiresAt?.let { iso ->
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                        .also { it.timeZone = TimeZone.getTimeZone("UTC") }
                    val out = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    sdf.parse(iso)?.let { out.format(it) }
                } catch (e: Exception) { null }
            } ?: ""
        )
    }

    // Validation error states — only shown after first submit attempt
    var submitted by remember { mutableStateOf(false) }
    val codeError by remember(code, submitted) { derivedStateOf { if (submitted) validateCode(code) else null } }
    val discountError by remember(discount, submitted) { derivedStateOf { if (submitted) validateDiscount(discount) else null } }
    val maxUsesError by remember(maxUses, submitted) { derivedStateOf { if (submitted) validateMaxUses(maxUses) else null } }

    // Date picker state
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = null,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // Only allow future or today dates
                val todayStart = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                return utcTimeMillis >= todayStart
            }
        }
    )

    // Sync selected date to expiresAt string
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            expiresAt = sdf.format(Date(millis))
        }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "Edit Coupon" else "Add Coupon",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            fontSize = 22.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background.copy(alpha = 0.9f)
                )
            )
        }
    ) { innerPadding ->

        // Material3 DatePicker Dialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("OK", color = Primary, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { }
                        expiresAt = ""
                        showDatePicker = false
                    }) {
                        Text("Clear", color = OnSurfaceVariant)
                    }
                },
                colors = DatePickerDefaults.colors(
                    containerColor = SurfaceContainerLowest
                )
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = Primary,
                        todayDateBorderColor = Primary,
                        todayContentColor = Primary
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Editorial Header
            Column(modifier = Modifier.fillMaxWidth().padding(end = 48.dp)) {
                Text(
                    text = buildAnnotatedStringWithHighlight(
                        fullText = if (isEditMode) "Refine your existing incentive." else "Create a new incentive for your learners.",
                        highlightWord = "incentive",
                        highlightColor = Primary
                    ),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = OnSurface,
                        letterSpacing = (-0.5).sp,
                        lineHeight = 40.sp
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Define the parameters for your promotional campaign. These details will be applied immediately to eligible courses.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = OnSurfaceVariant,
                        lineHeight = 22.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ── Coupon Code ──────────────────────────────────────────────
            CouponInputLabel("Coupon Code")
            CouponTextField(
                value = code,
                // Strip spaces and uppercase on every keystroke
                onValueChange = { code = it.replace(" ", "").uppercase() },
                placeholder = "e.g. SUMMER2024",
                trailingIcon = Icons.Outlined.ConfirmationNumber,
                isError = codeError != null
            )
            AnimatedError(message = codeError)

            Spacer(modifier = Modifier.height(24.dp))

            // ── Discount Percentage ──────────────────────────────────────
            CouponInputLabel("Discount Percentage  (1 – 99)")
            CouponTextField(
                value = discount,
                onValueChange = {
                    // Only allow digits, max 2 chars
                    if (it.length <= 2 && (it.isEmpty() || it.all(Char::isDigit))) discount = it
                },
                placeholder = "e.g. 25",
                keyboardType = KeyboardType.Number,
                isError = discountError != null,
                trailingContent = {
                    Text(
                        text = "%",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (discountError != null) ErrorColor else Primary
                        ),
                        modifier = Modifier.padding(end = 20.dp)
                    )
                }
            )
            AnimatedError(message = discountError)

            Spacer(modifier = Modifier.height(24.dp))

            // ── Usage Limit ──────────────────────────────────────────────
            CouponInputLabel("Usage Limit  (optional)")
            CouponTextField(
                value = maxUses,
                onValueChange = { if (it.isEmpty() || it.all(Char::isDigit)) maxUses = it },
                placeholder = "Unlimited",
                keyboardType = KeyboardType.Number,
                trailingIcon = Icons.Outlined.PersonAdd,
                isError = maxUsesError != null
            )
            AnimatedError(message = maxUsesError)

            Spacer(modifier = Modifier.height(24.dp))

            // ── Expiry Date (DatePicker) ──────────────────────────────────
            CouponInputLabel("Expiry Date  (optional)")
            DatePickerField(
                value = expiresAt,
                onClick = { showDatePicker = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Description ───────────────────────────────────────────────
            CouponInputLabel("Short Description  (optional)")
            CouponTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = "e.g. Holiday enrollment campaign"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Security badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(TertiaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.VerifiedUser,
                        contentDescription = null,
                        tint = OnTertiaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "SECURE COUPON GENERATION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = OnSurfaceVariant,
                        letterSpacing = 1.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Primary Action Button ────────────────────────────────────
            Button(
                onClick = {
                    submitted = true
                    val d = discount.toIntOrNull()
                    val cErr = validateCode(code)
                    val dErr = validateDiscount(discount)
                    val mErr = validateMaxUses(maxUses)
                    if (cErr == null && dErr == null && mErr == null && d != null) {
                        onConfirm(
                            code,
                            d,
                            description.ifBlank { null },
                            maxUses.toIntOrNull(),
                            expiresAt.ifBlank { null }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(listOf(Primary, PrimaryContainer)),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (isEditMode) "Update Coupon" else "Create Coupon",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF641800),
                            fontSize = 18.sp
                        )
                    )
                }
            }
        }
    }
}

// ── Reusable composables ───────────────────────────────────────────────────────

@Composable
private fun CouponInputLabel(label: String) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(
            fontWeight = FontWeight.SemiBold,
            color = OnSurfaceVariant
        ),
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun AnimatedError(message: String?) {
    if (message != null) {
        Text(
            text = "$message",
            style = MaterialTheme.typography.labelSmall.copy(
                color = ErrorColor,
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(start = 6.dp, top = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CouponTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: ImageVector? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                placeholder,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = OnSurfaceVariant.copy(alpha = 0.4f)
                )
            )
        },
        trailingIcon = when {
            trailingContent != null -> trailingContent
            trailingIcon != null -> ({
                Icon(
                    trailingIcon,
                    contentDescription = null,
                    tint = if (isError) ErrorColor else OnSurfaceVariant.copy(alpha = 0.35f)
                )
            })
            else -> null
        },
        isError = isError,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = SurfaceContainerLow,
            focusedContainerColor = SurfaceContainerLowest,
            errorContainerColor = ErrorColor.copy(alpha = 0.05f),
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Primary,
            errorBorderColor = ErrorColor,
            focusedTextColor = OnSurface,
            unfocusedTextColor = OnSurface
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = OnSurface,
            fontWeight = FontWeight.Normal
        )
    )
}

/** Read-only field that opens the date picker dialog when tapped. */
@Composable
private fun DatePickerField(
    value: String,
    onClick: () -> Unit
) {
    val displayText = if (value.isBlank()) "" else {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val fmt = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            fmt.format(sdf.parse(value)!!)
        } catch (e: Exception) { value }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceContainerLow)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = displayText.ifBlank { "Select a date (optional)" },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (displayText.isBlank()) OnSurfaceVariant.copy(alpha = 0.4f) else OnSurface,
                    fontWeight = FontWeight.Normal
                )
            )
            Icon(
                Icons.Outlined.CalendarMonth,
                contentDescription = "Pick date",
                tint = OnSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

private fun buildAnnotatedStringWithHighlight(
    fullText: String,
    highlightWord: String,
    highlightColor: Color
): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        val start = fullText.indexOf(highlightWord)
        if (start < 0) {
            append(fullText)
        } else {
            append(fullText.substring(0, start))
            withStyle(style = SpanStyle(color = highlightColor)) {
                append(highlightWord)
            }
            append(fullText.substring(start + highlightWord.length))
        }
    }
}
