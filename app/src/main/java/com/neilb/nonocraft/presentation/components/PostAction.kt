package com.neilb.nonocraft.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neilb.nonocraft.presentation.screens.dashboard.components.noRippleClickable
import com.neilb.nonocraft.presentation.theme.AppFont
import com.neilb.nonocraft.presentation.theme.TextPrimary

sealed interface PostAction {
    data class LikeClick(val postId: String) : PostAction
    data class CommentClick(val postId: String) : PostAction
    data class PlayClick(val postId: String) : PostAction
    data class MoreClick(val postId: String) : PostAction
    data class ProfileClick(val author: String) : PostAction
    data class FollowClick(val postId: String) : PostAction
}

@Composable
fun MenuRowItem(
    icon: ImageVector,
    text: String,
    tint: Color = TextPrimary,
    trailingIcon: ImageVector? = null,
    trailingIconTint: Color = tint,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable(onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = text,
                color = tint,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = AppFont
            )
        }

        if (trailingIcon != null) {
            Icon(
                imageVector = trailingIcon,
                contentDescription = null,
                tint = trailingIconTint,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}