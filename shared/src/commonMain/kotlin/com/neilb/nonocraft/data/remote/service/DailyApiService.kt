package com.neilb.nonocraft.data.remote.service

import com.neilb.nonocraft.data.remote.ApiResponse
import com.neilb.nonocraft.data.remote.dto.*
import com.neilb.nonocraft.data.remote.map
import com.neilb.nonocraft.data.remote.unitBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody

class DailyApiService(private val client: HttpClient) {

    suspend fun getTodayPuzzle(): ApiResponse<DailyPuzzleResponse> =
        client.get("daily/today").body<ApiResponse<PuzzleDto>>().map { DailyPuzzleResponse(puzzle = it) }

    suspend fun getStreak(): ApiResponse<StreakResponse> =
        client.get("daily/streak").body()

    suspend fun getCalendar(): ApiResponse<CalendarResponse> =
        client.get("daily/calendar").body<ApiResponse<List<String>>>()
            .map { dates -> CalendarResponse(dates.map { CalendarDay(date = it, completed = true) }) }

    suspend fun completeDaily(date: String): ApiResponse<CompleteResponse> =
        client.post("daily/$date/complete").body()
}
