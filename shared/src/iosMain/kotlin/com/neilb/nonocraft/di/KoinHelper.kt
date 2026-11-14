package com.neilb.nonocraft.di

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import com.neilb.nonocraft.presentation.navigation.AppViewModel
import com.neilb.nonocraft.presentation.screens.auth.AuthViewModel
import com.neilb.nonocraft.presentation.screens.auth.forgot_password.ForgotPasswordViewModel
import com.neilb.nonocraft.presentation.screens.auth.forgot_password.ResetPasswordViewModel
import com.neilb.nonocraft.presentation.screens.auth.login.LoginViewModel
import com.neilb.nonocraft.presentation.screens.auth.register.RegisterViewModel
import com.neilb.nonocraft.presentation.screens.dashboard.account.AccountViewModel
import com.neilb.nonocraft.presentation.screens.dashboard.feed.FeedViewModel
import com.neilb.nonocraft.presentation.screens.dashboard.library.LibraryViewModel
import com.neilb.nonocraft.presentation.screens.dashboard.library.detail.LibraryDetailViewModel
import com.neilb.nonocraft.presentation.screens.dashboard.playground.PlaygroundViewModel
import com.neilb.nonocraft.presentation.screens.settings.SettingsViewModel
import com.neilb.nonocraft.presentation.screens.settings.subscreens.account.ChangePasswordViewModel
import com.neilb.nonocraft.presentation.screens.settings.subscreens.account.EditProfileViewModel
import com.neilb.nonocraft.presentation.screens.gameplay.GameViewModel
import com.neilb.nonocraft.presentation.screens.intro.SplashViewModel
import com.neilb.nonocraft.presentation.screens.premium.PremiumViewModel
import com.neilb.nonocraft.presentation.screens.profile.ProfileViewModel
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform
import kotlin.reflect.KClass

/** Starts Koin for the iOS app; call once from `NonocraftApp.init`. */
fun doInitKoin() {
    initKoin()
}

/**
 * Owns the ViewModels of one SwiftUI screen. Swift keeps it alive for as long as the screen exists
 * and calls [clear] when the screen goes away, which cancels each ViewModel's viewModelScope.
 */
class ViewModelOwner {
    private val store = ViewModelStore()

    private val provider = ViewModelProvider.create(
        store = store,
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
                KoinPlatform.getKoin().get(modelClass)
        },
    )

    fun appViewModel(): AppViewModel = provider[AppViewModel::class]
    fun splashViewModel(): SplashViewModel = provider[SplashViewModel::class]
    fun authViewModel(): AuthViewModel = provider[AuthViewModel::class]
    fun loginViewModel(): LoginViewModel = provider[LoginViewModel::class]
    fun registerViewModel(): RegisterViewModel = provider[RegisterViewModel::class]
    fun forgotPasswordViewModel(): ForgotPasswordViewModel = provider[ForgotPasswordViewModel::class]
    fun resetPasswordViewModel(): ResetPasswordViewModel = provider[ResetPasswordViewModel::class]
    fun premiumViewModel(): PremiumViewModel = provider[PremiumViewModel::class]
    fun playgroundViewModel(): PlaygroundViewModel = provider[PlaygroundViewModel::class]
    fun libraryViewModel(): LibraryViewModel = provider[LibraryViewModel::class]
    fun accountViewModel(): AccountViewModel = provider[AccountViewModel::class]
    fun settingsViewModel(): SettingsViewModel = provider[SettingsViewModel::class]
    fun editProfileViewModel(): EditProfileViewModel = provider[EditProfileViewModel::class]
    fun changePasswordViewModel(): ChangePasswordViewModel = provider[ChangePasswordViewModel::class]

    fun feedViewModel(): FeedViewModel = provider[FeedViewModel::class]
    fun profileViewModel(): ProfileViewModel = provider[ProfileViewModel::class]

    // These read their argument from a SavedStateHandle, like the Android navigation routes.
    fun gameViewModel(gameId: String): GameViewModel =
        providerWithArgument("gameId", gameId)[GameViewModel::class]

    fun libraryDetailViewModel(categoryId: String): LibraryDetailViewModel =
        providerWithArgument("categoryId", categoryId)[LibraryDetailViewModel::class]

    private fun providerWithArgument(key: String, value: String) = ViewModelProvider.create(
        store = store,
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T =
                KoinPlatform.getKoin().get(modelClass) {
                    parametersOf(SavedStateHandle(mapOf(key to value)))
                }
        },
    )

    fun clear() = store.clear()
}
