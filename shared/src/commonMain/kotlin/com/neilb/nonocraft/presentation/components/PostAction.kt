package com.neilb.nonocraft.presentation.components

sealed interface PostAction {
    data class LikeClick(val postId: String) : PostAction
    data class CommentClick(val postId: String) : PostAction
    data class PlayClick(val postId: String) : PostAction
    data class MoreClick(val postId: String) : PostAction
    data class ProfileClick(val author: String) : PostAction
    data class FollowClick(val postId: String) : PostAction
}
