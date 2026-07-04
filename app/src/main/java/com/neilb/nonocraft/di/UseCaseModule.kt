package com.neilb.nonocraft.di

import com.neilb.nonocraft.domain.repository.AuthRepository
import com.neilb.nonocraft.domain.repository.PuzzleRepository
import com.neilb.nonocraft.domain.repository.UserRepository
import com.neilb.nonocraft.domain.use_case.auth.*
import com.neilb.nonocraft.domain.use_case.puzzle.*
import com.neilb.nonocraft.domain.use_case.user.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides @Singleton
    fun provideGetPuzzle(repository: PuzzleRepository): GetPuzzle = GetPuzzle(repository)

    @Provides @Singleton
    fun provideGetPuzzles(repository: PuzzleRepository): GetPuzzles = GetPuzzles(repository)

    @Provides @Singleton
    fun providePlayPuzzle(repository: PuzzleRepository): PlayPuzzle = PlayPuzzle(repository)

    @Provides @Singleton
    fun provideCompletePuzzle(repository: PuzzleRepository): CompletePuzzle = CompletePuzzle(repository)

    @Provides @Singleton
    fun provideLogin(repository: AuthRepository): Login = Login(repository)

    @Provides @Singleton
    fun provideRegister(repository: AuthRepository): Register = Register(repository)

    @Provides @Singleton
    fun provideGoogleLogin(repository: AuthRepository): GoogleLogin = GoogleLogin(repository)

    @Provides @Singleton
    fun provideGetMyProfile(repository: UserRepository): GetMyProfile = GetMyProfile(repository)

    @Provides @Singleton
    fun provideUpdateProfile(repository: UserRepository): UpdateProfile = UpdateProfile(repository)

    @Provides @Singleton
    fun provideChangePassword(repository: UserRepository): ChangePassword = ChangePassword(repository)

    @Provides @Singleton
    fun provideDeleteAccount(repository: UserRepository): DeleteAccount = DeleteAccount(repository)
}
