package com.neilb.nonocraft.data.remote.dto

import com.google.gson.annotations.SerializedName

data class DailyPuzzleResponse(
    @SerializedName("puzzle") val puzzle: PuzzleDto,
    @SerializedName("dayNumber") val dayNumber: Int
)

data class StreakResponse(
    @SerializedName("currentStreak") val currentStreak: Int,
    @SerializedName("longestStreak") val longestStreak: Int
)

data class CalendarDay(
    @SerializedName("date") val date: String,
    @SerializedName("completed") val completed: Boolean
)

data class CalendarResponse(
    @SerializedName("days") val days: List<CalendarDay>
)
