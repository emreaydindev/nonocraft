package com.neilb.nonocraft.di

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

/** Platform bindings: HTTP engine and the key-value store behind TokenManager. */
expect val platformModule: Module

val sharedModules: List<Module>
    get() = listOf(platformModule, networkModule, repositoryModule, useCaseModule)

fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication = startKoin {
    appDeclaration()
    modules(sharedModules)
}
