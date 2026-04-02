package com.example.skillforge.feature.student_courses.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.skillforge.core.designsystem.SkillforgeSpacing

@Composable
fun StudentBottomNavigationBar(currentRoute: String = "Learning") {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = SkillforgeSpacing.xSmall,
    ) {
        val colors = NavigationBarItemDefaults.colors(
            indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )

        NavigationBarItem(
            selected = currentRoute == "Discover",
            onClick = { /* TODO: Chuyển sang Discover / Home */ },
            icon = { Icon(Icons.Default.Explore, contentDescription = "Discover") },
            label = { Text("Discover") },
            colors = colors
        )
        NavigationBarItem(
            selected = currentRoute == "Learning",
            onClick = { /* TODO: Màn hình My Courses / Learning */ },
            icon = { Icon(Icons.Default.PlayCircleOutline, contentDescription = "Learning") },
            label = { Text("Learning") },
            colors = colors
        )
        NavigationBarItem(
            selected = currentRoute == "Wishlist",
            onClick = { /* TODO: Chuyển sang Favorite */ },
            icon = { Icon(Icons.Default.FavoriteBorder, contentDescription = "Wishlist") },
            label = { Text("Wishlist") },
            colors = colors
        )
        NavigationBarItem(
            selected = currentRoute == "Profile",
            onClick = { /* TODO: Chuyển sang Profile */ },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = colors
        )
    }
}