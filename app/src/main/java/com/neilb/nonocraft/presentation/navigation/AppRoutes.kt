package com.neilb.nonocraft.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable sealed interface Route {
    // Intro & Auth
    @Serializable data object Splash : Route
    @Serializable data object Onboarding : Route

    @Serializable data object AuthRoot : Route
    @Serializable data object Login : Route
    @Serializable data object Register : Route
    @Serializable data object ForgotPassword : Route
    @Serializable data object ResetPassword : Route

    // Dashboard (Main Nested Graph)
    @Serializable data object DashboardRoot : Route
    @Serializable data object Playground : Route
    @Serializable data object Feed : Route
    @Serializable data object Library : Route
    @Serializable data object Account : Route

    // Core & Extras
    @Serializable data object Tutorial : Route
    @Serializable data class Game(val gameId: String) : Route
    @Serializable data object Crafting : Route
    @Serializable data object Premium : Route
    @Serializable data object Settings : Route
}