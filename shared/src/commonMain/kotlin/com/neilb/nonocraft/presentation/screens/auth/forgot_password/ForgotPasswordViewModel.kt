package com.neilb.nonocraft.presentation.screens.auth.forgot_password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.domain.use_case.auth.ForgotPassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val isLoading: Boolean = false,
    val isSent: Boolean = false,
    val error: String? = null,
)

class ForgotPasswordViewModel(
    private val forgotPasswordUseCase: ForgotPassword,
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordUiState())
    val state: StateFlow<ForgotPasswordUiState> = _state.asStateFlow()

    fun sendResetCode(email: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = forgotPasswordUseCase(email)) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, isSent = true) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> { /* already handled */ }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null, isSent = false) }
    }
}