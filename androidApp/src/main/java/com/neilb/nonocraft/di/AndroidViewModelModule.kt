package com.neilb.nonocraft.di

import com.neilb.nonocraft.presentation.screens.dashboard.DashboardViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** ViewModels that still depend on Android or Compose types; the rest live in shared. */
val androidViewModelModule = module {
    viewModelOf(::DashboardViewModel)
}
