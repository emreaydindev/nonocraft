package com.neilb.nonocraft.di

import com.neilb.nonocraft.presentation.screens.dashboard.DashboardViewModel
import com.neilb.nonocraft.presentation.screens.dashboard.feed.FeedViewModel
import com.neilb.nonocraft.presentation.screens.dashboard.library.LibraryViewModel
import com.neilb.nonocraft.presentation.screens.dashboard.library.detail.LibraryDetailViewModel
import com.neilb.nonocraft.presentation.screens.profile.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** ViewModels that still depend on Android or Compose types; the rest live in shared. */
val androidViewModelModule = module {
    viewModelOf(::DashboardViewModel)
    viewModelOf(::FeedViewModel)
    viewModelOf(::LibraryDetailViewModel)
    viewModelOf(::LibraryViewModel)
    viewModelOf(::ProfileViewModel)
}
