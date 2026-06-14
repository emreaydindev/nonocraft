package com.neilb.nonocraft.domain.use_case.local

import com.neilb.nonocraft.domain.model.Game
import com.neilb.nonocraft.domain.repository.PuzzleRepository
import javax.inject.Inject

class DeletePuzzle @Inject constructor(
    private val puzzleRepository: PuzzleRepository
) {

    suspend operator fun invoke(game: Game) {
        return puzzleRepository.deletePuzzle(game)
    }

}