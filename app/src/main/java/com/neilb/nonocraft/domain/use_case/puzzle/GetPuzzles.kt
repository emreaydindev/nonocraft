package com.neilb.nonocraft.domain.use_case.puzzle

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.domain.model.Puzzle
import com.neilb.nonocraft.domain.repository.PuzzleRepository
import javax.inject.Inject

class GetPuzzles @Inject constructor(
    private val repository: PuzzleRepository
) {
    suspend operator fun invoke(
        limit: Int? = null,
        offset: Int? = null,
        type: String? = null,
        difficulty: String? = null
    ): Resource<List<Puzzle>> {
        return repository.getPuzzles(limit, offset, type, difficulty)
    }
}
