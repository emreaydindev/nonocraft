package com.neilb.nonocraft.domain.use_case.auth

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.domain.repository.AuthRepository

class ResetPassword(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(token: String, password: String): Resource<Unit> {
        return repository.resetPassword(token, password)
    }
}