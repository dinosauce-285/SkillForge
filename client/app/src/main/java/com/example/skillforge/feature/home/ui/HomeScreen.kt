package com.example.skillforge.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.skillforge.core.designsystem.SkillforgeSpacing
import com.example.skillforge.feature.home.ui.components.*
import com.example.skillforge.feature.home.ui.mock.HomeMockData
import androidx.compose.ui.tooling.preview.Preview
import com.example.skillforge.core.designsystem.SkillforgeTheme

@Composable
fun HomeScreen() {
    // Tạm thời lưu state ở UI để test giao diện.
    // Sau này các state này sẽ được chuyển sang HomeViewModel.
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    Scaffold(
        bottomBar = {
            // Tạm thời đặt BottomNavigationBar ở đây để giống thiết kế
            // Thực tế bạn nên đưa nó ra cấp ngoài cùng (MainActivity hoặc MainScreen)
            HomeBottomNavigationBar()
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()) // Cho phép cuộn toàn bộ trang
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 1. Top Bar
            HomeTopBar(
                onMenuClick = { /* TODO: Xử lý mở Drawer/Menu */ },
                onCartClick = { /* TODO: Mở màn hình giỏ hàng */ }
            )

            Spacer(modifier = Modifier.height(SkillforgeSpacing.small))

            // 2. Thanh tìm kiếm
            HomeSearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it }
            )

            // 3. Danh sách các Category (Chips)
            HomeCategoryChips(
                categories = HomeMockData.mockCategories,
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )

            Spacer(modifier = Modifier.height(SkillforgeSpacing.small))

            // 4. Danh sách khóa học phổ biến
            PopularCourseList(
                courses = HomeMockData.mockCourses,
                onSeeAllClick = { /* TODO: Chuyển hướng sang danh sách đầy đủ */ }
            )

            // Khoảng trống dưới cùng để không bị sát vào BottomNav
            Spacer(modifier = Modifier.height(SkillforgeSpacing.large))
        }
    }
}

/**
 * Component giả lập Bottom Navigation Bar dựa trên hình ảnh thiết kế.
 * Lưu ý: Trong project thực tế, thanh này thường được đặt ở NavHost / MainScreen
 * để hiển thị xuyên suốt các tab, chứ không nằm cục bộ trong HomeScreen.
 */
@Composable
private fun HomeBottomNavigationBar() {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = SkillforgeSpacing.small
    ) {
        val items = listOf("Discover", "Learning", "Wishlist", "Profile")
        val icons = listOf(
            Icons.Rounded.Explore,
            Icons.Default.PlayCircleOutline,
            Icons.Default.FavoriteBorder,
            Icons.Default.PersonOutline
        )

        items.forEachIndexed { index, item ->
            val isSelected = index == 0 // Fix cứng "Discover" đang được chọn để test UI

            NavigationBarItem(
                selected = isSelected,
                onClick = { /* TODO: Xử lý chuyển tab */ },
                icon = {
                    Icon(
                        imageVector = icons[index],
                        contentDescription = item
                    )
                },
                label = {
                    Text(
                        text = item,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = MaterialTheme.colorScheme.surface // Ẩn nền bao quanh icon mặc định của MD3
                )
            )
        }
    }
}

@Preview(
    name = "Home Screen Light Mode",
    showBackground = true,
    showSystemUi = true,
    device = "id:pixel_6" // Có thể đổi thành thiết bị bạn muốn
)
@Composable
fun HomeScreenPreview() {
    SkillforgeTheme {
        HomeScreen()
    }
}