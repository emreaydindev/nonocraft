package com.neilb.nonocraft.domain.repository

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.remote.dto.*

interface DailyRepository {
    suspend fun getTodayPuzzle(): Resource<DailyPuzzleResponse>
    suspend fun getStreak(): Resource<StreakResponse>
    suspend fun getCalendar(): Resource<CalendarResponse>
    suspend fun completeDaily(date: String): Resource<CompleteResponse>
}
