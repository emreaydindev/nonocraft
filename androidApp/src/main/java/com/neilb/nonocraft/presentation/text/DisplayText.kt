package com.neilb.nonocraft.presentation.text

import android.content.Context
import android.text.format.DateUtils
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.annotation.StringRes
import com.neilb.nonocraft.R
import com.neilb.nonocraft.common.ErrorCode
import com.neilb.nonocraft.presentation.model.Difficulty
import com.neilb.nonocraft.presentation.screens.dashboard.feed.FeedTab
import com.neilb.nonocraft.presentation.screens.dashboard.feed.TimeRange
import com.neilb.nonocraft.presentation.screens.dashboard.library.detail.LibraryCategory

/** Where an error is shown, so the same code can read differently per screen. */
enum class ErrorContext {
    GENERAL, LOGIN, REGISTER, FORGOT_PASSWORD, RESET_PASSWORD, CHANGE_PASSWORD, EDIT_PROFILE, DELETE_ACCOUNT, FEED, GAME
}

/** Turns a shared [ErrorCode] into text for the screen it appears on. */
fun ErrorCode.toMessage(context: Context, where: ErrorContext = ErrorContext.GENERAL): String =
    context.getString(messageRes(where))

@StringRes
private fun ErrorCode.messageRes(where: ErrorContext): Int = when {
    this == ErrorCode.NETWORK -> R.string.error_network
    where == ErrorContext.LOGIN && (this == ErrorCode.UNAUTHORIZED || this == ErrorCode.INVALID_CREDENTIALS) ->
        R.string.error_invalid_credentials
    where == ErrorContext.REGISTER && this == ErrorCode.CONFLICT -> R.string.error_email_or_username_taken
    where == ErrorContext.CHANGE_PASSWORD && (this == ErrorCode.UNAUTHORIZED || this == ErrorCode.WRONG_PASSWORD) ->
        R.string.error_wrong_password
    where == ErrorContext.RESET_PASSWORD && (this == ErrorCode.NOT_FOUND || this == ErrorCode.VALIDATION) ->
        R.string.error_invalid_code
    where == ErrorContext.EDIT_PROFILE && this == ErrorCode.CONFLICT -> R.string.error_username_taken
    where == ErrorContext.GAME && this == ErrorCode.NOT_FOUND -> R.string.error_puzzle_not_found
    this == ErrorCode.INVALID_CREDENTIALS -> R.string.error_invalid_credentials
    this == ErrorCode.EMAIL_TAKEN -> R.string.error_email_taken
    this == ErrorCode.USERNAME_TAKEN -> R.string.error_username_taken
    this == ErrorCode.WRONG_PASSWORD -> R.string.error_wrong_password
    this == ErrorCode.INVALID_CODE -> R.string.error_invalid_code
    this == ErrorCode.SESSION_EXPIRED || this == ErrorCode.UNAUTHORIZED -> R.string.error_session_expired
    this == ErrorCode.EMAIL_NOT_VERIFIED -> R.string.error_email_not_verified
    this == ErrorCode.INSUFFICIENT_CRYSTALS -> R.string.error_insufficient_crystals
    this == ErrorCode.DAILY_LIMIT_REACHED -> R.string.error_daily_limit
    this == ErrorCode.PUZZLE_NOT_FOUND -> R.string.error_puzzle_not_found
    this == ErrorCode.GOOGLE_SIGN_IN_FAILED -> R.string.error_google_sign_in
    this == ErrorCode.RATE_LIMITED -> R.string.error_rate_limited
    this == ErrorCode.SERVER -> R.string.error_server
    else -> when (where) {
        ErrorContext.LOGIN -> R.string.error_login_failed_generic
        ErrorContext.REGISTER -> R.string.error_register_failed_generic
        ErrorContext.CHANGE_PASSWORD -> R.string.error_change_password_generic
        ErrorContext.EDIT_PROFILE -> R.string.error_edit_profile_generic
        ErrorContext.DELETE_ACCOUNT -> R.string.error_delete_account_generic
        else -> R.string.error_unexpected
    }
}

@Composable
fun ErrorCode.message(where: ErrorContext = ErrorContext.GENERAL): String = toMessage(LocalContext.current, where)

@Composable
fun FeedTab.label(): String = when (this) {
    FeedTab.FEATURED -> stringResource(R.string.feed_tab_featured)
    FeedTab.LATEST -> stringResource(R.string.feed_tab_latest)
    FeedTab.FOLLOWING -> stringResource(R.string.feed_tab_following)
    FeedTab.MOST_LIKED -> stringResource(R.string.feed_tab_most_liked)
}

@Composable
fun TimeRange.label(): String = when (this) {
    TimeRange.TODAY -> stringResource(R.string.time_range_today)
    TimeRange.WEEK -> stringResource(R.string.time_range_week)
    TimeRange.MONTH -> stringResource(R.string.time_range_month)
    TimeRange.ALL_TIME -> stringResource(R.string.time_range_all)
}

@Composable
fun LibraryCategory.title(playlistName: String? = null): String = when (this) {
    LibraryCategory.LIKED -> stringResource(R.string.library_liked)
    LibraryCategory.DOWNLOADED -> stringResource(R.string.library_downloaded)
    LibraryCategory.SHARED -> stringResource(R.string.library_shared)
    LibraryCategory.DRAFTS -> stringResource(R.string.library_drafts)
    LibraryCategory.COMPLETED -> stringResource(R.string.library_completed)
    LibraryCategory.DAILIES -> stringResource(R.string.library_dailies)
    LibraryCategory.PLAYLIST -> playlistName ?: stringResource(R.string.library_playlist)
}

@Composable
fun Difficulty.label(): String = when (this) {
    Difficulty.EASY -> stringResource(R.string.difficulty_easy)
    Difficulty.MEDIUM -> stringResource(R.string.difficulty_medium)
    Difficulty.HARD -> stringResource(R.string.difficulty_hard)
}

/** "5×5"; the multiplication sign reads the same in both languages. */
fun boardSizeText(size: Int): String = "$size×$size"

@Composable
fun authorName(author: String?): String = author ?: stringResource(R.string.common_anonymous)

/** "5 hr. ago" in the app's current locale (DateUtils follows the per-app language); empty when unknown. */
fun relativeTime(epochMillis: Long?, now: Long = System.currentTimeMillis()): String =
    epochMillis?.let {
        DateUtils.getRelativeTimeSpanString(it, now, DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString()
    }.orEmpty()
