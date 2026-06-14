package com.neilb.nonocraft.domain.use_case.local

import com.neilb.nonocraft.domain.repository.PuzzleRepository
import javax.inject.Inject

class DeleteAllPublicPuzzles @Inject constructor(
    private val puzzleRepository: PuzzleRepository
) {

    suspend operator fun invoke() {
        return puzzleRepository.deleteAllPublicPuzzles()
    }

}