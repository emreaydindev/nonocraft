package com.neilb.nonocraft.di

import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidServicesModule = module {
    single<Vibrator> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                androidContext().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            androidContext().getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
    }
}
