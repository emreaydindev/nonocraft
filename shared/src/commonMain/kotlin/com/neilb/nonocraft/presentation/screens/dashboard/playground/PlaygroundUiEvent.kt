package com.neilb.nonocraft.presentation.screens.dashboard.playground

sealed interface PlaygroundUiEvent {
    data object Refresh : PlaygroundUiEvent
}
