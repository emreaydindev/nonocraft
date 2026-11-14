package com.neilb.nonocraft.di

import com.neilb.nonocraft.data.local.TokenManager
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

actual val platformModule: Module = module {
    single<HttpClientEngine> { Darwin.create() }
    single<Settings> { NSUserDefaultsSettings(NSUserDefaults(suiteName = TokenManager.STORE_NAME)) }
}

/** Entry point for the iOS app: call once from `NonocraftApp.init`. */
fun doInitKoin() {
    initKoin()
}
