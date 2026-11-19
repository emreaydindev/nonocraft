package com.neilb.nonocraft.presentation.model

import kotlin.time.Clock
import kotlin.time.Duration

data class PostItem(
    val id: String,
    val title: String,
    /** Username; null when unknown (the UI shows a localized "Anonymous"). */
    val author: String?,
    /** When it was posted; the UI formats it as relative time. */
    val createdAtEpochMillis: Long?,
    val likes: Int,
    val comments: Int,
    val isLiked: Boolean,
    val isFollowed: Boolean = false,
    val difficulty: Difficulty?,
    /** Board side length; the UI formats it (e.g. "5×5"). */
    val size: Int,
    /** ARGB, e.g. 0xFFE24B4A. */
    val accentColor: Long,
    val pattern: List<List<Int>>
)

enum class Difficulty {
    EASY, MEDIUM, HARD;

    companion object {
        /** The backend's `easy` / `medium` / `hard`; null for anything else. */
        fun fromApi(value: String?): Difficulty? =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
    }
}

/** Epoch millis [ago] before now, for demo data. */
internal fun epochMillisAgo(ago: Duration): Long =
    (Clock.System.now() - ago).toEpochMilliseconds()
