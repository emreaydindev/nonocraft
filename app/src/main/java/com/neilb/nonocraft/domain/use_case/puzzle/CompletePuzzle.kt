package com.neilb.nonocraft.domain.use_case.puzzle

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.domain.model.CompletionResult
import com.neilb.nonocraft.domain.repository.PuzzleRepository
import javax.inject.Inject

class CompletePuzzle @Inject constructor(
    private val repository: PuzzleRepository
) {
    suspend operator fun invoke(id: String): Resource<CompletionResult> {
        return repository.completePuzzle(id)
    }
}
