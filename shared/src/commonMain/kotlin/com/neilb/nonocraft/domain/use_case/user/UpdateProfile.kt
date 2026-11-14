package com.neilb.nonocraft.domain.use_case.user

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.domain.repository.UserRepository

class UpdateProfile(
    private val repository: UserRepository
) {
    suspend operator fun invoke(username: String?, bio: String?): Resource<Unit> {
        return repository.updateProfile(username, bio)
    }
}
