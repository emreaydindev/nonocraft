package com.neilb.nonocraft.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.neilb.nonocraft.common.NavDestinations
import com.neilb.nonocraft.presentation.ui.views.collections.CollectionScreen
import com.neilb.nonocraft.presentation.ui.views.create_nonocraft.CreateNonogramScreen
import com.neilb.nonocraft.presentation.ui.views.main.MainScreen
import com.neilb.nonocraft.presentation.ui.views.main.MainViewModel
import com.neilb.nonocraft.presentation.ui.views.menu.MenuScreen
import com.neilb.nonocraft.presentation.ui.views.test.TestScreen

@Composable
fun NavigationView() {
    val navController = rememberNavController()
    val mainViewModel: MainViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = NavDestinations.MENU_SCREEN) {
        composable(NavDestinations.MENU_SCREEN) {
            MenuScreen(navController, mainViewModel)
        }

        composable(NavDestinations.MAIN_SCREEN) {
            MainScreen(navController, mainViewModel)
        }

        composable(NavDestinations.CREATE_NONOGRAM_SCREEN) {
            CreateNonogramScreen(navController, mainViewModel)
        }

        composable("${NavDestinations.COLLECTIONS_SCREEN}/{public}") {
            val isPublic = it.arguments?.getString("public") == "public"
            CollectionScreen(navController, mainViewModel, isPublic)
        }

        composable(NavDestinations.TEST_SCREEN) {
            TestScreen(navController)
        }
    }
}