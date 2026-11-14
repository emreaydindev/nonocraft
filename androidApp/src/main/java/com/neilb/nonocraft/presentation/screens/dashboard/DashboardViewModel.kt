package com.neilb.nonocraft.presentation.screens.dashboard

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class DashboardViewModel(
    private val vibrator: Vibrator
) : ViewModel() {

    private val _backPressedOnce = MutableStateFlow(false)
    val backPressedOnce: StateFlow<Boolean> = _backPressedOnce.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<DashboardUiEvent>()
    val navigationEvent: SharedFlow<DashboardUiEvent> = _navigationEvent.asSharedFlow()

    private var timerJob: Job? = null

    fun handleBackPress() {
        if (_backPressedOnce.value) {
            viewModelScope.launch {
                _navigationEvent.emit(DashboardUiEvent.ExitApp)
            }
        } else {
            _backPressedOnce.value = true
            triggerVibration()

            viewModelScope.launch {
                _navigationEvent.emit(DashboardUiEvent.ShowExitSnackbar)
            }

            timerJob?.cancel()
            timerJob = viewModelScope.launch {
                delay(4000L.milliseconds)
                _backPressedOnce.value = false
            }
        }
    }

    private fun triggerVibration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(200L, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(200L)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

sealed interface DashboardUiEvent {
    object ShowExitSnackbar : DashboardUiEvent
    object ExitApp : DashboardUiEvent
}