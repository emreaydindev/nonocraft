package com.neilb.nonocraft.data.mapper

import com.neilb.nonocraft.data.remote.dto.*
import com.neilb.nonocraft.domain.model.*

fun PuzzleDto.toPuzzle(): Puzzle {
    return Puzzle(
        id = id,
        title = title,
        description = description,
        userId = userId,
        username = username,
        size = size,
        paletteHex = paletteHex,
        grid = grid,
        type = type,
        difficulty = difficulty,
        isPublic = isPublic ?: true,
        likeCount = likeCount ?: 0,
        commentCount = commentCount ?: 0,
        playCount = playCount ?: 0,
        isLiked = isLiked ?: false,
        isCompleted = isCompleted ?: false,
        createdAt = createdAt,
        dailyDate = dailyDate,
        timeLimitSeconds = timeLimitSeconds,
        maxLives = maxLives ?: 3
    )
}

fun CompleteResponse.toCompletionResult(): CompletionResult {
    return CompletionResult(
        starsAwarded = starsAwarded,
        totalStars = totalStars
    )
}

fun LikeResponse.toLikeResult(): LikeResult {
    return LikeResult(
        isLiked = liked ?: false,
        likeCount = likeCount ?: 0
    )
}

fun CommentDto.toComment(): Comment {
    return Comment(
        id = id,
        puzzleId = puzzleId,
        userId = userId,
        username = username,
        text = text,
        likeCount = likeCount ?: 0,
        isLiked = isLiked ?: false,
        createdAt = createdAt
    )
}
