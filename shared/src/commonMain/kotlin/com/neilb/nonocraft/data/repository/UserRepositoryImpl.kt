package com.neilb.nonocraft.data.repository

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.mapper.toUser
import com.neilb.nonocraft.data.remote.safeApiCall
import com.neilb.nonocraft.data.remote.service.UserApiService
import com.neilb.nonocraft.domain.model.User
import com.neilb.nonocraft.domain.repository.UserRepository

class UserRepositoryImpl(
    private val api: UserApiService
) : UserRepository {

    override suspend fun getMyProfile(): Resource<User> =
        safeApiCall { api.getMyProfile() }.let { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data!!.toUser())
                is Resource.Error -> Resource.Error(resource.message ?: "Unknown error")
                is Resource.Loading -> Resource.Loading()
            }
        }

    override suspend fun getUserProfile(userId: String): Resource<User> =
        safeApiCall { api.getUserProfile(userId) }.let { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data!!.toUser())
                is Resource.Error -> Resource.Error(resource.message ?: "Unknown error")
                is Resource.Loading -> Resource.Loading()
            }
        }

    override suspend fun updateProfile(username: String?, bio: String?): Resource<Unit> =
        safeApiCall { api.updateProfile(mapOf("username" to username, "bio" to bio)) }

    override suspend fun changePassword(currentPassword: String, newPassword: String) =
        safeApiCall { api.changePassword(mapOf("oldPassword" to currentPassword, "newPassword" to newPassword)) }

    override suspend fun changeEmail(newEmail: String) =
        safeApiCall { api.changeEmail(mapOf("email" to newEmail)) }

    override suspend fun followUser(userId: String) = safeApiCall { api.followUser(userId) }

    override suspend fun unfollowUser(userId: String) = safeApiCall { api.unfollowUser(userId) }

    override suspend fun getFollowers(userId: String, limit: Int?, offset: Int?): Resource<List<User>> =
        safeApiCall { api.getFollowers(userId, limit, offset) }.let { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data?.map { it.toUser() } ?: emptyList())
                is Resource.Error -> Resource.Error(resource.message ?: "Unknown error")
                is Resource.Loading -> Resource.Loading()
            }
        }

    override suspend fun getFollowing(userId: String, limit: Int?, offset: Int?): Resource<List<User>> =
        safeApiCall { api.getFollowing(userId, limit, offset) }.let { resource ->
            when (resource) {
                is Resource.Success -> Resource.Success(resource.data?.map { it.toUser() } ?: emptyList())
                is Resource.Error -> Resource.Error(resource.message ?: "Unknown error")
                is Resource.Loading -> Resource.Loading()
            }
        }

    override suspend fun deleteAccount() = safeApiCall { api.deleteAccount() }
}
