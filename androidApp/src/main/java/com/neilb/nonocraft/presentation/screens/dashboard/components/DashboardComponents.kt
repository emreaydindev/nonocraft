package com.neilb.nonocraft.presentation.screens.dashboard.components

import com.neilb.nonocraft.presentation.text.uppercaseForDisplay
import androidx.compose.foundation.background
import androidx.annotation.StringRes
import com.neilb.nonocraft.R
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neilb.nonocraft.presentation.theme.AppFont
import com.neilb.nonocraft.presentation.theme.BorderDark
import com.neilb.nonocraft.presentation.theme.PurpleAccent
import com.neilb.nonocraft.presentation.theme.SurfaceDark
import com.neilb.nonocraft.presentation.theme.TextMuted
import com.neilb.nonocraft.presentation.theme.icons.AppIcons

data class DashboardTabItem(
    @param:StringRes val label: Int,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
)

enum class DashboardTab(val tabItem: DashboardTabItem) {
    PLAYGROUND(DashboardTabItem(R.string.tab_playground, Icons.Filled.SportsEsports, Icons.Outlined.SportsEsports)),
    FEED(DashboardTabItem(R.string.tab_explore, Icons.Filled.Explore, Icons.Outlined.Explore)),
    LIBRARY(DashboardTabItem(R.string.tab_library, AppIcons.CardsStar.Filled, AppIcons.CardsStar.Outlined)),
    ACCOUNT(DashboardTabItem(R.string.tab_profile, Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle))
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
                        contentDescription = null, // the label below names the tab
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { NavLabel(stringResource(tabItem.label), isSelected) },
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

/**
 * Navigation rail for medium and expanded windows (tablets, unfolded foldables, Chromebooks): the same
 * tabs and colours as [DashboardBottomNavBar], placed along the start edge so content gets the height.
 */
@Composable
fun DashboardNavRail(
    currentTab: DashboardTab,
    onTabSelected: (DashboardTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxHeight()) {
        NavigationRail(
            containerColor = SurfaceDark,
            contentColor = TextMuted,
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
            ) {
                DashboardTab.entries.forEach { tab ->
                    val isSelected = tab == currentTab
                    val tabItem = tab.tabItem
                    NavigationRailItem(
                        selected = isSelected,
                        onClick = { onTabSelected(tab) },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tabItem.filledIcon else tabItem.outlinedIcon,
                                contentDescription = null, // the label below names the tab
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { NavLabel(stringResource(tabItem.label), isSelected) },
                        colors = NavigationRailItemDefaults.colors(
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
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(0.5.dp)
                .background(BorderDark)
        )
    }
}

/**
 * Tab label on one line. Its growth with the font scale is capped at 130 % so four labels still fit
 * side by side at the largest settings (the icons carry the meaning; TalkBack reads the full label).
 */
@Composable
private fun NavLabel(text: String, isSelected: Boolean) {
    val fontScale = LocalDensity.current.fontScale
    Text(
        text = text,
        fontSize = 11.sp * (minOf(fontScale, 1.3f) / fontScale),
        fontFamily = AppFont,
        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercaseForDisplay(),
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

/** Clickable with the app ripple, read by TalkBack as a button. */
fun Modifier.rippleClickable(onClick: () -> Unit): Modifier = rippleClickable(Role.Button, onClick)

/** Clickable with the app ripple, read by TalkBack as [role] (null for none). */
fun Modifier.rippleClickable(role: Role?, onClick: () -> Unit): Modifier = this.clickable(
    interactionSource = MutableInteractionSource(),
    indication = ripple(color = Color(0xFF0D0D10)),
    role = role,
    onClick = onClick,
)