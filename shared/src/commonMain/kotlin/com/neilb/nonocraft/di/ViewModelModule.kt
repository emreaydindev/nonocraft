package com.neilb.nonocraft.di

import com.neilb.nonocraft.presentation.navigation.AppViewModel
import com.neilb.nonocraft.presentation.screens.auth.AuthViewModel
import com.neilb.nonocraft.presentation.screens.auth.forgot_password.ForgotPasswordViewModel
import com.neilb.nonocraft.presentation.screens.auth.forgot_password.ResetPasswordViewModel
import com.neilb.nonocraft.presentation.screens.auth.login.LoginViewModel
import com.neilb.nonocraft.presentation.screens.auth.register.RegisterViewModel
import com.neilb.nonocraft.presentation.screens.dashboard.account.AccountViewModel
import com.neilb.nonocraft.presentation.screens.dashboard.library.LibraryViewModel
import com.neilb.nonocraft.presentation.screens.dashboard.playground.PlaygroundViewModel
import com.neilb.nonocraft.presentation.screens.gameplay.GameViewModel
import com.neilb.nonocraft.presentation.screens.intro.SplashViewModel
import com.neilb.nonocraft.presentation.screens.premium.PremiumViewModel
import com.neilb.nonocraft.presentation.screens.settings.SettingsViewModel
import com.neilb.nonocraft.presentation.screens.settings.subscreens.account.ChangePasswordViewModel
import com.neilb.nonocraft.presentation.screens.settings.subscreens.account.EditProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::AccountViewModel)
    viewModelOf(::AppViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::ChangePasswordViewModel)
    viewModelOf(::EditProfileViewModel)
    viewModelOf(::ForgotPasswordViewModel)
    viewModelOf(::GameViewModel)
    viewModelOf(::LibraryViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::PlaygroundViewModel)
    viewModelOf(::PremiumViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::ResetPasswordViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::SplashViewModel)
}
