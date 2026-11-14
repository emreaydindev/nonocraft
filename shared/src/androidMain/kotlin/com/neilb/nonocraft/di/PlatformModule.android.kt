package com.neilb.nonocraft.di

import android.content.Context
import com.neilb.nonocraft.data.local.TokenManager
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<HttpClientEngine> { OkHttp.create() }
    single<Settings> {
        SharedPreferencesSettings(
            androidContext().getSharedPreferences(TokenManager.STORE_NAME, Context.MODE_PRIVATE)
        )
    }
}
