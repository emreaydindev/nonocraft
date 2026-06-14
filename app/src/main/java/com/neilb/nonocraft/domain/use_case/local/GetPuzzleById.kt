package com.neilb.nonocraft.domain.use_case.local

import com.neilb.nonocraft.domain.model.Game
import com.neilb.nonocraft.domain.repository.PuzzleRepository
import javax.inject.Inject

class GetPuzzleById @Inject constructor(
    private val puzzleRepository: PuzzleRepository
) {

    suspend operator fun invoke(id: String): Game? {
        return puzzleRepository.getPuzzleById(id)
    }

}