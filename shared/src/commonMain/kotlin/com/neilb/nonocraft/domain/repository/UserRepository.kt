package com.neilb.nonocraft.domain.repository

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.domain.model.User

interface UserRepository {
    suspend fun getMyProfile(): Resource<User>
    suspend fun getUserProfile(userId: String): Resource<User>
    suspend fun updateProfile(username: String?, bio: String?): Resource<Unit>
    suspend fun changePassword(currentPassword: String, newPassword: String): Resource<Unit>
    suspend fun changeEmail(newEmail: String): Resource<Unit>
    suspend fun followUser(userId: String): Resource<Unit>
    suspend fun unfollowUser(userId: String): Resource<Unit>
    suspend fun getFollowers(userId: String, limit: Int?, offset: Int?): Resource<List<User>>
    suspend fun getFollowing(userId: String, limit: Int?, offset: Int?): Resource<List<User>>
    suspend fun deleteAccount(): Resource<Unit>
}
