package com.neilb.nonocraft.presentation.screens.dashboard.playground

import com.neilb.nonocraft.domain.model.User

data class PlaygroundUiState(
    val userSection: SectionState<User> = SectionState.Loading,
)

sealed interface SectionState<out T> {
    data object Loading : SectionState<Nothing>
    data class Success<T>(val data: T) : SectionState<T>
    data class Error(val message: String) : SectionState<Nothing>
}