package com.neilb.nonocraft.di

import com.neilb.nonocraft.data.repository.ApiRepositoryImpl
import com.neilb.nonocraft.data.repository.PuzzleRepositoryImpl
import com.neilb.nonocraft.domain.repository.ApiRepository
import com.neilb.nonocraft.domain.repository.PuzzleRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPuzzleRepository(puzzleRepositoryImpl: PuzzleRepositoryImpl): PuzzleRepository

    @Binds
    @Singleton
    abstract fun bindApiRepository(apiRepositoryImpl: ApiRepositoryImpl): ApiRepository

}