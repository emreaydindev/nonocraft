package com.neilb.nonocraft.presentation.navigation

import com.neilb.nonocraft.presentation.screens.intro.SplashScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.neilb.nonocraft.presentation.screens.auth.ForgotPasswordScreen
import com.neilb.nonocraft.presentation.screens.auth.LoginScreen
import com.neilb.nonocraft.presentation.screens.auth.RegisterScreen
import com.neilb.nonocraft.presentation.screens.auth.ResetPasswordScreen
import com.neilb.nonocraft.presentation.screens.crafting.CraftingScreen
import com.neilb.nonocraft.presentation.screens.dashboard.DashboardScreen
import com.neilb.nonocraft.presentation.screens.extras.PremiumScreen
import com.neilb.nonocraft.presentation.screens.extras.SettingsScreen
import com.neilb.nonocraft.presentation.screens.gameplay.GameScreen
import com.neilb.nonocraft.presentation.screens.intro.OnboardingScreen
import com.neilb.nonocraft.presentation.screens.intro.TutorialScreen

@Composable
fun NavigationView() {
    val rootNavController = rememberNavController()

    NavHost(rootNavController, startDestination = Route.Splash) {
        composable<Route.Splash> {
            SplashScreen(onNavigateToOnboarding = { rootNavController.navigate(Route.Onboarding) })
        }

        composable<Route.Onboarding> {
            OnboardingScreen(onFinishOnboarding = {
                rootNavController.navigate(Route.AuthRoot) {
                    popUpTo(Route.Onboarding) { inclusive = true }
                }
            })
        }

        navigation<Route.AuthRoot>(startDestination = Route.Register) {

            composable<Route.Register> {
                RegisterScreen(
                    onNavigateToLogin = { rootNavController.navigate(Route.Login) },
                    onContinueAsGuest = {
                        rootNavController.navigate(Route.Tutorial) { popUpTo(Route.AuthRoot) { inclusive = true } }
                    },
                    onRegisterSuccess = {
                        rootNavController.navigate(Route.Tutorial) { popUpTo(Route.AuthRoot) { inclusive = true } }
                    }
                )
            }

            composable<Route.Login> {
                LoginScreen(
                    onNavigateToForgotPassword = { rootNavController.navigate(Route.ForgotPassword) },
                    onLoginSuccess = {
                        rootNavController.navigate(Route.DashboardRoot) { popUpTo(Route.AuthRoot) { inclusive = true } }
                    }
                )
            }

            composable<Route.ForgotPassword> { ForgotPasswordScreen() }
            composable<Route.ResetPassword> { ResetPasswordScreen() }
        }

        composable<Route.DashboardRoot> {
            DashboardScreen(
                onNavigateToGame = { gameId -> rootNavController.navigate(Route.Game(gameId)) },
                onNavigateToPremium = { rootNavController.navigate(Route.Premium) },
                onNavigateToSettings = { rootNavController.navigate(Route.Settings) }
            )
        }

        composable<Route.Game> { backStackEntry ->
            val args = backStackEntry.toRoute<Route.Game>()
            GameScreen(gameId = args.gameId)
        }

        composable<Route.Tutorial> { TutorialScreen() }
        composable<Route.Crafting> { CraftingScreen() }
        composable<Route.Premium> { PremiumScreen() }
        composable<Route.Settings> { SettingsScreen() }
    }
}