package com.neilb.nonocraft.domain.use_case.local

import com.neilb.nonocraft.domain.model.ProgressInGame
import com.neilb.nonocraft.domain.repository.PuzzleRepository
import javax.inject.Inject

class AddProgress @Inject constructor(
    private val puzzleRepository: PuzzleRepository
) {

    suspend operator fun invoke(progressInGame: ProgressInGame) {
        return puzzleRepository.insertProgress(progressInGame)
    }

}