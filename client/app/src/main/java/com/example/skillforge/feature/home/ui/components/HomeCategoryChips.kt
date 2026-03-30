package com.example.skillforge.feature.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.skillforge.core.designsystem.*

@Composable
fun HomeCategoryChips(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SkillforgeSpacing.medium),
        contentPadding = PaddingValues(horizontal = SkillforgeLayout.screenHorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory

            Box(
                modifier = Modifier
                    .clip(SkillforgeShapes.chip) // Dùng pill shape từ hệ thống
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else ChipUnselectedBackgroundColor
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else ChipUnselectedBorderColor,
                        shape = SkillforgeShapes.chip
                    )
                    .clickable { onCategorySelected(category) }
                    .padding(
                        horizontal = SkillforgeSpacing.large,
                        vertical = SkillforgeSpacing.small
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else ChipUnselectedTextColor,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}