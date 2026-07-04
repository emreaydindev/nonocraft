package com.neilb.nonocraft.domain.use_case.user

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.domain.repository.UserRepository
import javax.inject.Inject

class DeleteAccount @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Resource<Unit> {
        return repository.deleteAccount()
    }
}
