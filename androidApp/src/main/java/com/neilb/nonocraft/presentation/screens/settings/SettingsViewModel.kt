package com.neilb.nonocraft.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.session.SessionManager
import com.neilb.nonocraft.domain.use_case.user.DeleteAccount
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val sessionManager: SessionManager,
    private val deleteAccountUseCase: DeleteAccount
) : ViewModel() {
    private val _uiEvent = MutableSharedFlow<SettingsUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _uiEvent.emit(SettingsUiEvent.NavigateToAuth)
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            val result = deleteAccountUseCase()
            if (result is Resource.Success) {
                sessionManager.clearSession()
                _uiEvent.emit(SettingsUiEvent.NavigateToAuth)
            } else if (result is Resource.Error) {
                _uiEvent.emit(SettingsUiEvent.ShowError(result.message ?: "Hesap silinemedi"))
            }
        }
    }
}

sealed interface SettingsUiEvent {
    object NavigateToAuth : SettingsUiEvent
    data class ShowError(val message: String) : SettingsUiEvent
}