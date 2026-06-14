package com.neilb.nonocraft.domain.use_case.local

import com.neilb.nonocraft.domain.repository.PuzzleRepository
import javax.inject.Inject

class GetPublicPuzzleSizeLocally @Inject constructor(
    private val puzzleRepository: PuzzleRepository
) {
    suspend operator fun invoke(): Int {
        return puzzleRepository.getPublicPuzzleSize() ?: 0
    }
}