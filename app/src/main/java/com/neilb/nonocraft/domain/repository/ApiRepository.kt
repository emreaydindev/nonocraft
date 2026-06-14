package com.neilb.nonocraft.domain.repository

import com.neilb.nonocraft.data.model.request.GameRequest
import com.neilb.nonocraft.data.model.request.ReportRequest
import com.neilb.nonocraft.data.model.response.GameResponse
import com.neilb.nonocraft.data.model.response.GetPuzzlesResponse
import com.neilb.nonocraft.data.model.response.PuzzleSizeResponse

interface ApiRepository {

    suspend fun getPublicPuzzles(limit: Int? = null, skip: Int? = null) : Result<GetPuzzlesResponse>

    suspend fun addPublicPuzzle(request: GameRequest) : Result<GameResponse>

    suspend fun getPuzzleSize() : Result<PuzzleSizeResponse>

    suspend fun reportPuzzle(request: ReportRequest) : Result<GameResponse>

}