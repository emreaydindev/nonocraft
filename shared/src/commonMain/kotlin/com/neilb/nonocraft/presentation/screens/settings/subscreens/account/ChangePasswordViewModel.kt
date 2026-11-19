package com.neilb.nonocraft.presentation.screens.settings.subscreens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neilb.nonocraft.common.ErrorCode
import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.domain.use_case.user.ChangePassword
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val changePasswordUseCase: ChangePassword
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<ChangePasswordUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    fun changePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            _uiEvent.emit(ChangePasswordUiEvent.Loading)
            val result = changePasswordUseCase(oldPassword, newPassword)
            when (result) {
                is Resource.Success -> {
                    _uiEvent.emit(ChangePasswordUiEvent.Success)
                }
                is Resource.Error -> {
                    _uiEvent.emit(ChangePasswordUiEvent.Error(result.error.code))
                }
                else -> {}
            }
        }
    }
}

sealed interface ChangePasswordUiEvent {
    object Loading : ChangePasswordUiEvent
    object Success : ChangePasswordUiEvent
    data class Error(val code: ErrorCode) : ChangePasswordUiEvent
}
