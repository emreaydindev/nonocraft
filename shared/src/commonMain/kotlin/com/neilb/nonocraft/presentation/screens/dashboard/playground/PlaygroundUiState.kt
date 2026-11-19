package com.neilb.nonocraft.presentation.screens.dashboard.playground

import com.neilb.nonocraft.common.ErrorCode
import com.neilb.nonocraft.domain.model.User

data class PlaygroundUiState(
    val userSection: SectionState<User> = SectionState.Loading,
    /** Guests have an empty username; the UI shows its own localized "Guest". */
    val isGuest: Boolean = false,
    // TODO: demo values until the economy, daily and level features are connected to the API.
    val crystals: Int = 0,
    val crystalRefillMinutes: Int = 23,
    val isDailySolved: Boolean = false,
    val currentLevelNumber: Int = 3,
    val hasDraft: Boolean = true,
    /** Board side length; the UI formats it (e.g. "10×10"). */
    val draftSize: Int = 10,
    val draftProgress: Int = 60,
) {
    val username: String get() = (userSection as? SectionState.Success)?.data?.username.orEmpty()
    val stars: Int get() = (userSection as? SectionState.Success)?.data?.stars ?: 0

    /** Campaign levels are stored as "level-001", "level-002", ... */
    val currentLevelId: String get() = "level-" + currentLevelNumber.toString().padStart(3, '0')
}

sealed interface SectionState<out T> {
    data object Loading : SectionState<Nothing>
    data class Success<T>(val data: T) : SectionState<T>
    data class Error(val code: ErrorCode) : SectionState<Nothing>
}
