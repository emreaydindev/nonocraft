package com.neilb.nonocraft.domain.use_case.puzzle

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.domain.model.Puzzle
import com.neilb.nonocraft.domain.repository.PuzzleRepository

class GetPuzzle(
    private val repository: PuzzleRepository
) {
    suspend operator fun invoke(id: String): Resource<Puzzle> {
        return repository.getPuzzleById(id)
    }
}
