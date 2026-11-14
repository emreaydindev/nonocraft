package com.neilb.nonocraft.domain.use_case.user

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.domain.model.User
import com.neilb.nonocraft.domain.repository.UserRepository

class GetMyProfile(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Resource<User> {
        return repository.getMyProfile()
    }
}
