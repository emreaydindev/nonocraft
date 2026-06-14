package com.neilb.nonocraft.domain.use_case.local

import com.neilb.nonocraft.domain.model.ProgressInGame
import com.neilb.nonocraft.domain.repository.PuzzleRepository
import javax.inject.Inject

class GetProgressById @Inject constructor(
    private val puzzleRepository: PuzzleRepository
) {

    suspend operator fun invoke(id: String): ProgressInGame? {
        return puzzleRepository.getProgressById(id)
    }

}