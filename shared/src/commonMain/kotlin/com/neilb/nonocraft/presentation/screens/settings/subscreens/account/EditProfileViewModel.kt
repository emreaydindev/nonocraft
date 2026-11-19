package com.neilb.nonocraft.presentation.screens.settings.subscreens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neilb.nonocraft.common.ErrorCode
import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.session.SessionManager
import com.neilb.nonocraft.domain.use_case.user.UpdateProfile
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val sessionManager: SessionManager,
    private val updateProfileUseCase: UpdateProfile
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<EditProfileUiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    val currentUser = sessionManager.userProfile

    fun updateProfile(username: String, bio: String) {
        viewModelScope.launch {
            _uiEvent.emit(EditProfileUiEvent.Loading)
            val result = updateProfileUseCase(username, bio)
            when (result) {
                is Resource.Success -> {
                    // Update local session if needed, or assume backend sync
                    sessionManager.refreshProfile()
                    _uiEvent.emit(EditProfileUiEvent.Success)
                }
                is Resource.Error -> {
                    _uiEvent.emit(EditProfileUiEvent.Error(result.error.code))
                }
                else -> {}
            }
        }
    }
}

sealed interface EditProfileUiEvent {
    object Loading : EditProfileUiEvent
    object Success : EditProfileUiEvent
    data class Error(val code: ErrorCode) : EditProfileUiEvent
}
