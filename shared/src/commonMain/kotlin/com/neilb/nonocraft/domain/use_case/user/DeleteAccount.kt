package com.neilb.nonocraft.domain.use_case.user

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.domain.repository.UserRepository

class DeleteAccount(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Resource<Unit> {
        return repository.deleteAccount()
    }
}
