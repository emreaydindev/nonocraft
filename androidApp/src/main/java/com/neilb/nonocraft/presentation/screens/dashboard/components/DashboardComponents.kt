package com.neilb.nonocraft.presentation.screens.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neilb.nonocraft.presentation.theme.AppFont
import com.neilb.nonocraft.presentation.theme.BorderDark
import com.neilb.nonocraft.presentation.theme.PurpleAccent
import com.neilb.nonocraft.presentation.theme.SurfaceDark
import com.neilb.nonocraft.presentation.theme.TextMuted
import com.neilb.nonocraft.presentation.theme.icons.AppIcons

data class DashboardTabItem(
    val label: String,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
)

enum class DashboardTab(val tabItem: DashboardTabItem) {
    PLAYGROUND(DashboardTabItem("Oyun Alanı", Icons.Filled.SportsEsports, Icons.Outlined.SportsEsports)),
    FEED(DashboardTabItem("Keşfet", Icons.Filled.Explore, Icons.Outlined.Explore)),
    LIBRARY(DashboardTabItem("Koleksiyon", AppIcons.CardsStar.Filled, AppIcons.CardsStar.Outlined)),
    ACCOUNT(DashboardTabItem("Profil", Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle))
}

@Composable
fun DashboardBottomNavBar(
    currentTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier.border(width = 0.5.dp, color = BorderDark),
        containerColor = SurfaceDark,
        contentColor = TextMuted,
        tonalElevation = 0.dp
    ) {
        DashboardTab.entries.forEach { tab ->
            val isSelected = tab == currentTab
            val tabItem = tab.tabItem

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) tabItem.filledIcon else tabItem.outlinedIcon,
                        contentDescription = tabItem.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = tabItem.label,
                        fontSize = 11.sp,
                        fontFamily = AppFont,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PurpleAccent,
                    selectedTextColor = PurpleAccent,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted,
                    indicatorColor = PurpleAccent.copy(alpha = 0.15f)
                )
            )
        }
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        color = TextMuted,
        fontSize = 11.sp,
        letterSpacing = 1.2.sp,
        fontFamily = AppFont,
        fontWeight = FontWeight.Medium,
        modifier = modifier.padding(bottom = 8.dp),
    )
}

@Composable
fun DashboardCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = SurfaceDark,
    borderColor: Color = BorderDark,
    cornerRadius: Int = 16,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius.dp))
            .background(backgroundColor)
            .border(0.5.dp, borderColor, RoundedCornerShape(cornerRadius.dp)),
    ) {
        content()
    }
}

fun Modifier.rippleClickable(onClick: () -> Unit): Modifier = this.clickable(
    interactionSource = MutableInteractionSource(),
    indication = ripple(color = Color(0xFF0D0D10)),
    onClick = onClick,
)