package com.neilb.nonocraft.domain.use_case.auth

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.domain.model.AuthResult
import com.neilb.nonocraft.domain.repository.AuthRepository

class GoogleLogin(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Resource<AuthResult> {
        return repository.googleLogin(idToken)
    }
}
