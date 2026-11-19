package com.neilb.nonocraft.presentation.text

import android.content.Context
import android.text.format.DateUtils
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
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
fun ErrorCode.toMessage(context: Context, where: ErrorContext = ErrorContext.GENERAL): String = when {
    this == ErrorCode.NETWORK -> "Bağlantı hatası. Lütfen internetinizi kontrol edin."
    where == ErrorContext.LOGIN && (this == ErrorCode.UNAUTHORIZED || this == ErrorCode.INVALID_CREDENTIALS) ->
        "E-posta veya şifre hatalı"
    where == ErrorContext.REGISTER && this == ErrorCode.CONFLICT -> "Bu e-posta veya kullanıcı adı zaten kullanılıyor"
    where == ErrorContext.CHANGE_PASSWORD && (this == ErrorCode.UNAUTHORIZED || this == ErrorCode.WRONG_PASSWORD) ->
        "Mevcut şifre hatalı"
    where == ErrorContext.RESET_PASSWORD && (this == ErrorCode.NOT_FOUND || this == ErrorCode.VALIDATION) ->
        "Geçersiz veya süresi dolmuş kod"
    where == ErrorContext.EDIT_PROFILE && this == ErrorCode.CONFLICT -> "Bu kullanıcı adı zaten alınmış"
    where == ErrorContext.GAME && this == ErrorCode.NOT_FOUND -> "Bulmaca bulunamadı"
    this == ErrorCode.INVALID_CREDENTIALS -> "E-posta veya şifre hatalı"
    this == ErrorCode.EMAIL_TAKEN -> "Bu e-posta zaten kullanılıyor"
    this == ErrorCode.USERNAME_TAKEN -> "Bu kullanıcı adı zaten alınmış"
    this == ErrorCode.WRONG_PASSWORD -> "Mevcut şifre hatalı"
    this == ErrorCode.INVALID_CODE -> "Geçersiz veya süresi dolmuş kod"
    this == ErrorCode.SESSION_EXPIRED || this == ErrorCode.UNAUTHORIZED -> "Oturum süresi doldu, lütfen tekrar giriş yapın"
    this == ErrorCode.EMAIL_NOT_VERIFIED -> "Önce e-posta adresinizi doğrulayın"
    this == ErrorCode.INSUFFICIENT_CRYSTALS -> "Yetersiz kristal"
    this == ErrorCode.DAILY_LIMIT_REACHED -> "Günlük limitine ulaştın"
    this == ErrorCode.PUZZLE_NOT_FOUND -> "Bulmaca bulunamadı"
    this == ErrorCode.GOOGLE_SIGN_IN_FAILED -> "Google ile giriş yapılamadı"
    this == ErrorCode.RATE_LIMITED -> "Çok fazla deneme. Biraz sonra tekrar dene."
    this == ErrorCode.SERVER -> "Sunucu hatası. Lütfen daha sonra tekrar deneyin."
    else -> when (where) {
        ErrorContext.LOGIN -> "Giriş yapılamadı"
        ErrorContext.REGISTER -> "Kayıt olunamadı"
        ErrorContext.CHANGE_PASSWORD -> "Şifre güncellenemedi"
        ErrorContext.EDIT_PROFILE -> "Profil güncellenemedi"
        ErrorContext.DELETE_ACCOUNT -> "Hesap silinemedi"
        else -> "Beklenmedik bir hata oluştu"
    }
}

@Composable
fun ErrorCode.message(where: ErrorContext = ErrorContext.GENERAL): String = toMessage(LocalContext.current, where)

@Composable
fun FeedTab.label(): String = when (this) {
    FeedTab.FEATURED -> "Öne Çıkanlar"
    FeedTab.LATEST -> "En Yeniler"
    FeedTab.FOLLOWING -> "Takip Edilenler"
    FeedTab.MOST_LIKED -> "En Beğenilenler"
}

@Composable
fun TimeRange.label(): String = when (this) {
    TimeRange.TODAY -> "Son 24 Saat"
    TimeRange.WEEK -> "Bu Hafta"
    TimeRange.MONTH -> "Bu Ay"
    TimeRange.ALL_TIME -> "Tüm Zamanlar"
}

@Composable
fun LibraryCategory.title(playlistName: String? = null): String = when (this) {
    LibraryCategory.LIKED -> "Beğenilenler"
    LibraryCategory.DOWNLOADED -> "İndirilenler"
    LibraryCategory.SHARED -> "Paylaşılanlar"
    LibraryCategory.DRAFTS -> "Taslaklar"
    LibraryCategory.COMPLETED -> "Çözülenler"
    LibraryCategory.DAILIES -> "Günlük Serüven"
    LibraryCategory.PLAYLIST -> playlistName ?: "Oynatma Listesi"
}

@Composable
fun Difficulty.label(): String = when (this) {
    Difficulty.EASY -> "Kolay"
    Difficulty.MEDIUM -> "Orta"
    Difficulty.HARD -> "Zor"
}

/** "5×5"; the multiplication sign reads the same in both languages. */
fun boardSizeText(size: Int): String = "$size×$size"

@Composable
fun authorName(author: String?): String = author ?: "Anonim"

/** "5 hours ago" in the app's current locale; empty when unknown. */
fun relativeTime(epochMillis: Long?, now: Long = System.currentTimeMillis()): String =
    epochMillis?.let {
        DateUtils.getRelativeTimeSpanString(it, now, DateUtils.MINUTE_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString()
    }.orEmpty()
