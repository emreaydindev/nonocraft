package com.neilb.nonocraft.presentation.screens.dashboard.playground

import com.neilb.nonocraft.domain.model.User

data class PlaygroundUiState(
    val userSection: SectionState<User> = SectionState.Loading,
    // TODO: demo values until the economy, daily and level features are connected to the API.
    val crystals: Int = 0,
    val crystalRefillMinutes: Int = 23,
    val isDailySolved: Boolean = false,
    val currentLevelNumber: Int = 3,
    val hasDraft: Boolean = true,
    val draftSize: String = "10×10",
    val draftProgress: Int = 60,
) {
    val username: String get() = (userSection as? SectionState.Success)?.data?.username.orEmpty()
    val stars: Int get() = (userSection as? SectionState.Success)?.data?.stars ?: 0
}

sealed interface SectionState<out T> {
    data object Loading : SectionState<Nothing>
    data class Success<T>(val data: T) : SectionState<T>
    data class Error(val message: String) : SectionState<Nothing>
}
