package com.example.skillforge.core.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.skillforge.core.designsystem.SearchBarBackgroundColor
import com.example.skillforge.core.designsystem.SkillforgeComponentSizes
import com.example.skillforge.core.designsystem.SkillforgeLayout
import com.example.skillforge.core.designsystem.SkillforgeShapes

@Composable
fun SkillforgeSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit
) {
    OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = SkillforgeLayout.screenHorizontalPadding)
            .height(SkillforgeComponentSizes.textFieldHeight),
        placeholder = {
            Text(
                text = "Search for courses, topics...",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        shape = SkillforgeShapes.input, // Dùng shape chuẩn từ hệ thống (medium radius)
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = SearchBarBackgroundColor,
            focusedContainerColor = SearchBarBackgroundColor,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        singleLine = true
    )
}