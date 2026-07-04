package com.neilb.nonocraft.presentation.model

import androidx.compose.ui.graphics.Color

data class PostItem(
    val id: String,
    val title: String,
    val author: String,
    val timeAgo: String,
    val likes: Int,
    val comments: Int,
    val isLiked: Boolean,
    val isFollowed: Boolean = false,
    val difficulty: String,
    val size: String,
    val accentColor: Color,
    val pattern: List<List<Int>>
)
