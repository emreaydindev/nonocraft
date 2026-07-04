package com.neilb.nonocraft.data.remote.service

import com.neilb.nonocraft.data.remote.ApiResponse
import com.neilb.nonocraft.data.remote.dto.*
import retrofit2.http.GET
import retrofit2.http.POST

interface DailyApiService {
    @GET("daily/today")
    suspend fun getTodayPuzzle(): ApiResponse<DailyPuzzleResponse>

    @GET("daily/streak")
    suspend fun getStreak(): ApiResponse<StreakResponse>

    @GET("daily/calendar")
    suspend fun getCalendar(): ApiResponse<CalendarResponse>

    @POST("daily/complete")
    suspend fun completeDaily(): ApiResponse<CompleteResponse>
}
