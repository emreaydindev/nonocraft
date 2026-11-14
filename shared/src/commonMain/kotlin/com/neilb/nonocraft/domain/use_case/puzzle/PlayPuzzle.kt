package com.neilb.nonocraft.domain.use_case.puzzle

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.domain.repository.PuzzleRepository

class PlayPuzzle(
    private val repository: PuzzleRepository
) {
    suspend operator fun invoke(id: String): Resource<Unit> {
        return repository.playPuzzle(id)
    }
}
