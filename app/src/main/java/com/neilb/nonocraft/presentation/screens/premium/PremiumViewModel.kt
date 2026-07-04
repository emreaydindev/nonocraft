package com.neilb.nonocraft.presentation.screens.premium

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class PremiumViewModel : ViewModel() {

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _uiEvent = MutableSharedFlow<PremiumUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun purchasePremium(planId: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            delay(2000.milliseconds)
            _isProcessing.value = false
            _uiEvent.emit(PremiumUiEvent.PurchaseSuccess)
        }
    }
}

sealed interface PremiumUiEvent {
    object PurchaseSuccess : PremiumUiEvent
}