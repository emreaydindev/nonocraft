package com.neilb.nonocraft.domain.repository

import com.neilb.nonocraft.domain.model.Game
import com.neilb.nonocraft.domain.model.ProgressInGame
import kotlinx.coroutines.flow.Flow

interface PuzzleRepository {

    fun getPublicPuzzles(limit: Int, skip: Int): Flow<List<Game>>

    fun getOwnPuzzles(limit: Int, skip: Int): Flow<List<Game>>

    suspend fun getPuzzleById(id: String): Game?

    suspend fun insertPuzzle(game: Game)

    suspend fun deletePuzzle(game: Game)

    suspend fun deleteAllPublicPuzzles()

    suspend fun getProgressById(id: String): ProgressInGame?

    suspend fun insertProgress(progressInGame: ProgressInGame)

    suspend fun getPublicPuzzleSize(): Int?

    suspend fun getOwnPuzzleSize(): Int?

}