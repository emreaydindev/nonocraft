package com.neilb.nonocraft.presentation.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.neilb.nonocraft.data.local.TokenManager
import com.neilb.nonocraft.data.session.SessionManager
import com.neilb.nonocraft.domain.model.User
import com.neilb.nonocraft.presentation.screens.auth.gateway.AuthGatewayScreen
import com.neilb.nonocraft.presentation.screens.auth.login.LoginScreen
import com.neilb.nonocraft.presentation.screens.auth.register.RegisterScreen
import com.neilb.nonocraft.presentation.screens.auth.forgot_password.ForgotPasswordScreen
import com.neilb.nonocraft.presentation.screens.auth.forgot_password.ResetPasswordScreen
import com.neilb.nonocraft.presentation.screens.crafting.CraftingScreen
import com.neilb.nonocraft.presentation.screens.dashboard.DashboardScreen
import com.neilb.nonocraft.presentation.screens.dashboard.library.detail.LibraryDetailScreen
import com.neilb.nonocraft.presentation.screens.premium.PremiumScreen
import com.neilb.nonocraft.presentation.screens.gameplay.GameScreen
import com.neilb.nonocraft.presentation.screens.gameplay.GameViewModel
import com.neilb.nonocraft.presentation.screens.intro.OnboardingScreen
import com.neilb.nonocraft.presentation.screens.intro.SplashScreen
import com.neilb.nonocraft.presentation.screens.intro.TutorialScreen
import com.neilb.nonocraft.presentation.screens.profile.ProfileScreen
import com.neilb.nonocraft.presentation.screens.settings.SettingsScreen
import com.neilb.nonocraft.presentation.screens.settings.subscreens.account.ChangePasswordScreen
import com.neilb.nonocraft.presentation.screens.settings.subscreens.account.EditProfileScreen
import com.neilb.nonocraft.presentation.screens.settings.subscreens.icons.AppIconScreen
import com.neilb.nonocraft.presentation.screens.settings.subscreens.notifications.NotificationSettingsScreen
import com.neilb.nonocraft.presentation.screens.settings.subscreens.theme.ThemeScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val sessionManager: SessionManager,
) : ViewModel() {

    val userProfile: StateFlow<User?> = sessionManager.userProfile

    fun completeOnboarding() {
        tokenManager.hasCompletedOnboarding = true
    }

    fun enterGuestMode() {
        tokenManager.setGuestMode()
    }

    fun logout() {
        sessionManager.clearSession()
    }
}

@Composable
fun NavigationView() {
    val rootNavController = rememberNavController()
    val context = LocalContext.current

    NavHost(rootNavController, startDestination = Route.Splash) {
        composable<Route.Splash> {
            SplashScreen(
                onNavigateToOnboarding = {
                    rootNavController.navigate(Route.Onboarding) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                },
                onNavigateToAuthGateway = {
                    rootNavController.navigate(Route.AuthRoot) {
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
            val appViewModel: AppViewModel = hiltViewModel()
            OnboardingScreen(onFinishOnboarding = {
                appViewModel.completeOnboarding()
                rootNavController.navigate(Route.AuthRoot) {
                    popUpTo(Route.Onboarding) { inclusive = true }
                }
            })
        }

        navigation<Route.AuthRoot>(startDestination = Route.AuthGateway) {
            composable<Route.AuthGateway> {
                val appViewModel: AppViewModel = hiltViewModel()
                AuthGatewayScreen(
                    onNavigateToLogin = { rootNavController.navigate(Route.Login) },
                    onNavigateToRegister = { rootNavController.navigate(Route.Register) },
                    onContinueAsGuest = {
                        appViewModel.enterGuestMode()
                        rootNavController.navigate(Route.Tutorial) {
                            popUpTo(Route.AuthRoot) { inclusive = true }
                        }
                    },
                    onGoogleLoginSuccess = {
                        rootNavController.navigate(Route.DashboardRoot) {
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
                        rootNavController.navigate(Route.DashboardRoot) {
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
                onNavigateToProfile = { userId -> rootNavController.navigate(Route.Profile(userId)) },
                onNavigateToPremium = { rootNavController.navigate(Route.Premium) },
                onNavigateToSettings = { rootNavController.navigate(Route.SettingsRoot) },
                onNavigateToCrafting = { rootNavController.navigate(Route.Crafting) },
                onNavigateToCategory = { categoryId -> rootNavController.navigate(Route.LibraryDetail(categoryId)) },
                onNavigateToPlaylist = { playlistId -> rootNavController.navigate(Route.LibraryDetail(playlistId)) },
                onNavigateToTutorial = {
                    rootNavController.navigate(Route.Tutorial) {
                        popUpTo(Route.DashboardRoot) { inclusive = true }
                    }
                },
                onExitApp = {
                    (context as? Activity)?.finish()
                },
                onNavigateToAuthGateway = {
                    rootNavController.navigate(Route.AuthRoot) {
                        popUpTo(Route.DashboardRoot) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Game> {
            val viewModel: GameViewModel = hiltViewModel()

            GameScreen(
                viewModel = viewModel,
                onNavigateBack = { rootNavController.popBackStack() }
            )
        }

        composable<Route.Tutorial> {
            TutorialScreen(
                onNavigateToDashboard = {
                    rootNavController.navigate(Route.DashboardRoot) {
                        popUpTo(Route.Tutorial) { inclusive = true }
                    }
                }
            )
        }
        composable<Route.Crafting> { CraftingScreen() }
        composable<Route.Premium> {
            PremiumScreen(
                onNavigateBack = { rootNavController.popBackStack() }
            )
        }

        navigation<Route.SettingsRoot>(startDestination = Route.SettingsMain) {
            composable<Route.SettingsMain> {
                SettingsScreen(
                    onNavigateBack = { rootNavController.popBackStack() },
                    onNavigateToTheme = { rootNavController.navigate(Route.SettingsTheme) },
                    onNavigateToAppIcon = { rootNavController.navigate(Route.SettingsAppIcon) },
                    onNavigateToEditProfile = { rootNavController.navigate(Route.SettingsEditProfile) },
                    onNavigateToChangePassword = { rootNavController.navigate(Route.SettingsChangePassword) },
                    onNavigateToNotifications = { rootNavController.navigate(Route.SettingsNotifications) },
                    onLogoutSuccess = {
                        rootNavController.navigate(Route.AuthRoot) {
                            popUpTo(Route.DashboardRoot) { inclusive = true }
                        }
                    }
                )
            }

            composable<Route.SettingsTheme> {
                ThemeScreen(
                    onNavigateBack = { rootNavController.popBackStack() }
                )
            }

            composable<Route.SettingsAppIcon> {
                AppIconScreen(
                    onNavigateBack = { rootNavController.popBackStack() }
                )
            }

            composable<Route.SettingsEditProfile> {
                EditProfileScreen(
                    onNavigateBack = { rootNavController.popBackStack() }
                )
            }

            composable<Route.SettingsChangePassword> {
                ChangePasswordScreen(
                    onNavigateBack = { rootNavController.popBackStack() }
                )
            }

            composable<Route.SettingsNotifications> {
                NotificationSettingsScreen(
                    onNavigateBack = { rootNavController.popBackStack() }
                )
            }
        }

        composable<Route.Profile> { backStackEntry ->
            val args = backStackEntry.toRoute<Route.Profile>()
            ProfileScreen(
                userId = args.userId,
                onNavigateBack = { rootNavController.popBackStack() },
                onNavigateToGame = { gameId -> rootNavController.navigate(Route.Game(gameId)) }
            )
        }

        composable<Route.LibraryDetail> {
            LibraryDetailScreen(
                onNavigateBack = { rootNavController.popBackStack() },
                onNavigateToGame = { gameId -> rootNavController.navigate(Route.Game(gameId)) },
                onNavigateToProfile = { userId -> rootNavController.navigate(Route.Profile(userId)) }
            )
        }
    }
}