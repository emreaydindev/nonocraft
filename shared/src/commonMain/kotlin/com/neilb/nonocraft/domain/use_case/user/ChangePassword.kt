package com.neilb.nonocraft.domain.use_case.user

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.domain.repository.UserRepository

class ChangePassword(
    private val repository: UserRepository
) {
    suspend operator fun invoke(oldPassword: String, newPassword: String): Resource<Unit> {
        return repository.changePassword(oldPassword, newPassword)
    }
}
