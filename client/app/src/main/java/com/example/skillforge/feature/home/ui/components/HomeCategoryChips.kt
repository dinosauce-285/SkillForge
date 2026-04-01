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
import com.example.skillforge.domain.model.Category

@Composable
fun HomeCategoryChips(
    categories: List<Category>,
    selectedCategoryId: String?,
    onCategorySelected: (String?) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SkillforgeSpacing.medium),
        contentPadding = PaddingValues(horizontal = SkillforgeLayout.screenHorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(SkillforgeSpacing.medium)
    ) {
        item {
            val isAllSelected = selectedCategoryId == null
            CategoryChipItem(
                text = "All",
                isSelected = isAllSelected,
                onClick = { onCategorySelected(null) }
            )
        }

        items(categories) { category ->
            val isSelected = category.id == selectedCategoryId
            CategoryChipItem(
                text = category.name,
                isSelected = isSelected,
                onClick = { onCategorySelected(category.id) } // Trả về ID
            )
        }
    }
}

// Tách Box ra thành một component private nhỏ để tái sử dụng cho gọn code
@Composable
private fun CategoryChipItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(SkillforgeShapes.chip)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary
                else ChipUnselectedBackgroundColor
            )
            .border(
                width = 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else ChipUnselectedBorderColor,
                shape = SkillforgeShapes.chip
            )
            .clickable { onClick() }
            .padding(
                horizontal = SkillforgeSpacing.large,
                vertical = SkillforgeSpacing.small
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else ChipUnselectedTextColor,
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}