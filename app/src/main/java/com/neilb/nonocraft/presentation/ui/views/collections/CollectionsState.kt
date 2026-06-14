package com.neilb.nonocraft.presentation.ui.views.collections

import com.neilb.nonocraft.data.model.request.GameItem

data class CollectionsState(
    val isLoading: Boolean = false,
    val data: List<GameItem>? = null,
    val error: String? = null
)