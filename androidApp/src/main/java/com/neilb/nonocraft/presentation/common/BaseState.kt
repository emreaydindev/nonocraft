package com.neilb.nonocraft.presentation.common

data class BaseState(
    val isLoading: Boolean = false,
    val taskSucceeded: Boolean = false,
    val error: String? = null
)