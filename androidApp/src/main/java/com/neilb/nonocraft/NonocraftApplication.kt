package com.neilb.nonocraft

import android.app.Application
import com.neilb.nonocraft.di.androidServicesModule
import com.neilb.nonocraft.di.androidViewModelModule
import com.neilb.nonocraft.di.initKoin
import org.koin.android.ext.koin.androidContext

class NonocraftApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@NonocraftApplication)
            modules(androidServicesModule, androidViewModelModule)
        }
    }
}
