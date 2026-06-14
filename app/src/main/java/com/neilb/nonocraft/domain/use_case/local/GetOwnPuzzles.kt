package com.neilb.nonocraft.domain.use_case.local

import com.neilb.nonocraft.domain.model.Game
import com.neilb.nonocraft.domain.repository.PuzzleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOwnPuzzles @Inject constructor(
    private val puzzleRepository: PuzzleRepository
) {

    operator fun invoke(limit: Int, skip: Int): Flow<List<Game>> {
        return puzzleRepository.getOwnPuzzles(limit, skip)
    }

}