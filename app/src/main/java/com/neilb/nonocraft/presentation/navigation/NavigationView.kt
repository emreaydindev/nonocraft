package com.neilb.nonocraft.presentation.navigation

import com.neilb.nonocraft.presentation.screens.auth.gateway.AuthGatewayScreen
import com.neilb.nonocraft.presentation.screens.auth.login.LoginScreen
import com.neilb.nonocraft.presentation.screens.auth.register.RegisterScreen
import com.neilb.nonocraft.presentation.screens.intro.SplashScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.neilb.nonocraft.presentation.screens.auth.forgot_password.ForgotPasswordScreen
import com.neilb.nonocraft.presentation.screens.auth.forgot_password.ResetPasswordScreen
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
            SplashScreen(
                onNavigateToOnboarding = {
                    rootNavController.navigate(Route.Onboarding) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    rootNavController.navigate(Route.DashboardRoot) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Onboarding> {
            OnboardingScreen(onFinishOnboarding = {
                rootNavController.navigate(Route.AuthRoot) {
                    popUpTo(Route.Onboarding) { inclusive = true }
                }
            })
        }

        navigation<Route.AuthRoot>(startDestination = Route.AuthGateway) {

            composable<Route.AuthGateway> {
                AuthGatewayScreen(
                    onNavigateToLogin = { rootNavController.navigate(Route.Login) },
                    onNavigateToRegister = { rootNavController.navigate(Route.Register) },
                    onContinueAsGuest = {
                        rootNavController.navigate(Route.Tutorial) {
                            popUpTo(Route.AuthRoot) { inclusive = true }
                        }
                    }
                )
            }

            composable<Route.Register> {
                RegisterScreen(
                    onNavigateBack = { rootNavController.popBackStack() },
                    onNavigateToLogin = {
                        rootNavController.navigate(Route.Login) {
                            popUpTo(Route.AuthGateway)
                        }
                    },
                    onRegisterSuccess = {
                        rootNavController.navigate(Route.Tutorial) {
                            popUpTo(Route.AuthRoot) { inclusive = true }
                        }
                    }
                )
            }

            composable<Route.Login> {
                LoginScreen(
                    onNavigateBack = { rootNavController.popBackStack() },
                    onNavigateToForgotPassword = { rootNavController.navigate(Route.ForgotPassword) },
                    onNavigateToRegister = {
                        rootNavController.navigate(Route.Register) {
                            popUpTo(Route.AuthGateway)
                        }
                    },
                    onLoginSuccess = {
                        rootNavController.navigate(Route.DashboardRoot) {
                            popUpTo(Route.AuthRoot) { inclusive = true }
                        }
                    }
                )
            }

            composable<Route.ForgotPassword> {
                ForgotPasswordScreen(
                    onNavigateBack = { rootNavController.popBackStack() },
                    onCodeSentSuccess = {
                        rootNavController.navigate(Route.ResetPassword)
                    }
                )
            }

            composable<Route.ResetPassword> {
                ResetPasswordScreen(
                    onNavigateBack = { rootNavController.popBackStack() },
                    onResetSuccess = {
                        rootNavController.navigate(Route.Login) {
                            popUpTo(Route.AuthGateway) { inclusive = false }
                        }
                    }
                )
            }
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