package com.neilb.nonocraft.domain.use_case.auth

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.domain.model.AuthResult
import com.neilb.nonocraft.domain.repository.AuthRepository
import javax.inject.Inject

class Login @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Resource<AuthResult> {
        return repository.login(email, password)
    }
}
