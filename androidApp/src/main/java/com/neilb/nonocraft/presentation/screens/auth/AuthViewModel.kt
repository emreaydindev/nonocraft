package com.neilb.nonocraft.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.session.SessionManager
import com.neilb.nonocraft.domain.use_case.auth.GoogleLogin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class AuthViewModel(
    private val googleLoginUseCase: GoogleLogin,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    fun googleLogin(idToken: String) {
        viewModelScope.launch {
            changeLoadingState(true)
            when (val result = googleLoginUseCase(idToken)) {
                is Resource.Success -> {
                    sessionManager.refreshSession()
                    _state.update { it.copy(isLoading = false, isSuccess = true) }
                }
                is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                is Resource.Loading -> {}
            }
        }
    }

    fun changeLoadingState(loading: Boolean, error: String? = null) {
        _state.update { it.copy(isLoading = loading, error = error) }
    }

    fun showError(message: String?) {
        _state.update { it.copy(error = message ?: "Bilinmeyen bir hata oluştu") }
    }
}