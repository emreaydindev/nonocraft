package com.neilb.nonocraft.presentation.model


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
    /** ARGB, e.g. 0xFFE24B4A. */
    val accentColor: Long,
    val pattern: List<List<Int>>
)
