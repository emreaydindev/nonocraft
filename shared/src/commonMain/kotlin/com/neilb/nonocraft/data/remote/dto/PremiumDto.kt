package com.neilb.nonocraft.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PremiumStatus(
    val isPremium: Boolean,
    val expirationDate: String?,
    val planType: String?
)
