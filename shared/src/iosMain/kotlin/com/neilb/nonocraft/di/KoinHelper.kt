package com.neilb.nonocraft.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import com.neilb.nonocraft.presentation.navigation.AppViewModel
import com.neilb.nonocraft.presentation.screens.auth.AuthViewModel
import com.neilb.nonocraft.presentation.screens.auth.login.LoginViewModel
import com.neilb.nonocraft.presentation.screens.auth.register.RegisterViewModel
import com.neilb.nonocraft.presentation.screens.dashboard.playground.PlaygroundViewModel
import com.neilb.nonocraft.presentation.screens.intro.SplashViewModel
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
    fun playgroundViewModel(): PlaygroundViewModel = provider[PlaygroundViewModel::class]

    fun clear() = store.clear()
}
