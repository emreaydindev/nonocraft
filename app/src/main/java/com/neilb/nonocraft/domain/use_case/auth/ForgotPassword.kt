package com.neilb.nonocraft.domain.use_case.auth

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.domain.repository.AuthRepository
import javax.inject.Inject

class ForgotPassword @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String): Resource<Unit> {
        return repository.forgotPassword(email)
    }
}