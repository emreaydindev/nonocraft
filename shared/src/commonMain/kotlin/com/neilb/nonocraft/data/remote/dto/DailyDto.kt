package com.neilb.nonocraft.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DailyPuzzleResponse(
    val puzzle: PuzzleDto,
    val dayNumber: Int = 0
)

@Serializable
data class StreakResponse(
    @SerialName("streakCount") val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastStreakDate: String? = null
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
