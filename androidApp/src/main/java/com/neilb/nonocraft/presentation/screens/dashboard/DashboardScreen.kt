package com.neilb.nonocraft.presentation.screens.dashboard

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.koin.androidx.compose.koinViewModel
import com.neilb.nonocraft.presentation.screens.dashboard.account.AccountScreen
import com.neilb.nonocraft.presentation.screens.dashboard.components.DashboardBottomNavBar
import com.neilb.nonocraft.presentation.screens.dashboard.components.DashboardTab
import com.neilb.nonocraft.presentation.screens.dashboard.feed.FeedScreen
import com.neilb.nonocraft.presentation.screens.dashboard.library.LibraryScreen
import com.neilb.nonocraft.presentation.screens.dashboard.playground.PlaygroundScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    onNavigateToGame: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToTutorial: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCrafting: () -> Unit,
    onNavigateToCategory: (String) -> Unit,
    onNavigateToPlaylist: (String) -> Unit,
    onNavigateToAuthGateway: () -> Unit,
    onExitApp: () -> Unit,
    viewModel: DashboardViewModel = koinViewModel(),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var currentTab by rememberSaveable { mutableStateOf(DashboardTab.PLAYGROUND) }

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                DashboardUiEvent.ExitApp -> onExitApp()
                DashboardUiEvent.ShowExitSnackbar -> {
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(
                            message = "Çıkmak için tekrar geri basın.",
                            withDismissAction = true,
                        )
                    }
                }
            }
        }
    }

    BackHandler(enabled = true) {
        if (currentTab != DashboardTab.PLAYGROUND) {
            currentTab = DashboardTab.PLAYGROUND
        } else {
            viewModel.handleBackPress()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            DashboardBottomNavBar(
                currentTab = currentTab,
                onTabSelected = { currentTab = it },
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            when (currentTab) {
                DashboardTab.PLAYGROUND -> PlaygroundScreen(
                    onNavigateToGame = onNavigateToGame,
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToTutorial = onNavigateToTutorial
                )

                DashboardTab.FEED -> FeedScreen(
                    onNavigateToGame = onNavigateToGame,
                    onNavigateToProfile = onNavigateToProfile,
                    onNavigateToCrafting = onNavigateToCrafting
                )

                DashboardTab.LIBRARY -> LibraryScreen(
                    onNavigateToCrafting = onNavigateToCrafting,
                    onNavigateToPremium = onNavigateToPremium,
                    onNavigateToCategory = onNavigateToCategory,
                    onNavigateToPlaylist = onNavigateToPlaylist
                )

                DashboardTab.ACCOUNT -> AccountScreen(
                    onNavigateToPremium = onNavigateToPremium,
                    onNavigateToSettings = onNavigateToSettings,
                    onNavigateToCrafting = onNavigateToCrafting,
                    onNavigateToAuthGateway = onNavigateToAuthGateway
                )
            }
        }
    }
}