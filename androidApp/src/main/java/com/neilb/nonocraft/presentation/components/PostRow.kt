package com.neilb.nonocraft.presentation.components

import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.res.pluralStringResource
import com.neilb.nonocraft.R
import com.neilb.nonocraft.presentation.text.authorName
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neilb.nonocraft.presentation.model.PostItem
import com.neilb.nonocraft.presentation.theme.AppFont
import com.neilb.nonocraft.presentation.theme.BackgroundDark
import com.neilb.nonocraft.presentation.theme.BorderDark
import com.neilb.nonocraft.presentation.theme.DangerRed
import com.neilb.nonocraft.presentation.theme.NeonCyan
import com.neilb.nonocraft.presentation.theme.SurfaceDark
import com.neilb.nonocraft.presentation.theme.SurfaceVariantDark
import com.neilb.nonocraft.presentation.theme.TextMuted
import com.neilb.nonocraft.presentation.theme.TextPrimary
import com.neilb.nonocraft.presentation.theme.TextSecondary
import kotlin.math.min

/**
 * Compact post row for the list pane of a two-pane layout: pattern thumbnail, title, author and likes.
 * The full [PostCard] is shown next to it as the preview.
 */
@Composable
fun PostRow(
    post: PostItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val likesDescription = pluralStringResource(R.plurals.post_likes_count, post.likes, post.likes)
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (isSelected) SurfaceVariantDark else SurfaceDark)
            .border(if (isSelected) 1.5.dp else 0.5.dp, if (isSelected) NeonCyan else BorderDark, shape)
            .selectable(selected = isSelected, role = Role.Tab, onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PostPatternThumbnail(
            pattern = post.pattern,
            accent = Color(post.accentColor),
            modifier = Modifier.size(56.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = post.title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = AppFont,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = post.author?.let { "@$it" } ?: authorName(null),
                color = TextSecondary,
                fontSize = 13.sp,
                fontFamily = AppFont,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clearAndSetSemantics { contentDescription = likesDescription },
            ) {
                Icon(
                    imageVector = if (post.isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = null,
                    tint = if (post.isLiked) DangerRed else TextMuted,
                    modifier = Modifier.size(14.dp),
                )
                Text(
                    text = post.likes.toString(),
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontFamily = AppFont,
                )
            }
        }
    }
}

/** The post's pixel pattern drawn small, for thumbnails. */
@Composable
fun PostPatternThumbnail(
    pattern: List<List<Int>>,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(BackgroundDark)
            .padding(6.dp)
    ) {
        val rows = pattern.size
        val cols = pattern.maxOfOrNull { it.size } ?: 0
        if (rows == 0 || cols == 0) return@Canvas
        val cell = min(size.width / cols, size.height / rows)
        val originX = (size.width - cell * cols) / 2f
        val originY = (size.height - cell * rows) / 2f
        val inset = cell * 0.06f
        pattern.forEachIndexed { row, values ->
            values.forEachIndexed { col, value ->
                drawRect(
                    color = if (value == 1) accent else SurfaceVariantDark,
                    topLeft = Offset(originX + col * cell + inset, originY + row * cell + inset),
                    size = Size(cell - 2 * inset, cell - 2 * inset),
                )
            }
        }
    }
}
