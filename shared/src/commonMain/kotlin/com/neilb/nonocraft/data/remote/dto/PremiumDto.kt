package com.neilb.nonocraft.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PremiumStatus(
    val isPremium: Boolean,
    @SerialName("expiresAt") val expirationDate: String? = null,
    val planType: String? = null,
    val lifetime: Boolean = false
)
