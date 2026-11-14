package com.neilb.nonocraft.data.remote.dto

import kotlinx.serialization.Serializable


@Serializable
data class DailyPuzzleResponse(
    val puzzle: PuzzleDto,
    val dayNumber: Int
)

@Serializable
data class StreakResponse(
    val currentStreak: Int,
    val longestStreak: Int
)

@Serializable
data class CalendarDay(
    val date: String,
    val completed: Boolean
)

@Serializable
data class CalendarResponse(
    val days: List<CalendarDay>
)
