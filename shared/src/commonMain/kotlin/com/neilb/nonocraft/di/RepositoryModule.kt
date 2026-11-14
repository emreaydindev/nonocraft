package com.neilb.nonocraft.di

import com.neilb.nonocraft.data.local.TokenManager
import com.neilb.nonocraft.data.repository.AuthRepositoryImpl
import com.neilb.nonocraft.data.repository.DailyRepositoryImpl
import com.neilb.nonocraft.data.repository.FeedRepositoryImpl
import com.neilb.nonocraft.data.repository.PlaylistRepositoryImpl
import com.neilb.nonocraft.data.repository.PremiumRepositoryImpl
import com.neilb.nonocraft.data.repository.PuzzleRepositoryImpl
import com.neilb.nonocraft.data.repository.RankingRepositoryImpl
import com.neilb.nonocraft.data.repository.SocialRepositoryImpl
import com.neilb.nonocraft.data.repository.UserRepositoryImpl
import com.neilb.nonocraft.data.session.SessionManager
import com.neilb.nonocraft.domain.repository.AuthRepository
import com.neilb.nonocraft.domain.repository.DailyRepository
import com.neilb.nonocraft.domain.repository.FeedRepository
import com.neilb.nonocraft.domain.repository.PlaylistRepository
import com.neilb.nonocraft.domain.repository.PremiumRepository
import com.neilb.nonocraft.domain.repository.PuzzleRepository
import com.neilb.nonocraft.domain.repository.RankingRepository
import com.neilb.nonocraft.domain.repository.SocialRepository
import com.neilb.nonocraft.domain.repository.UserRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::TokenManager)
    singleOf(::SessionManager)

    singleOf(::AuthRepositoryImpl) { bind<AuthRepository>() }
    singleOf(::UserRepositoryImpl) { bind<UserRepository>() }
    singleOf(::PuzzleRepositoryImpl) { bind<PuzzleRepository>() }
    singleOf(::SocialRepositoryImpl) { bind<SocialRepository>() }
    singleOf(::PlaylistRepositoryImpl) { bind<PlaylistRepository>() }
    singleOf(::FeedRepositoryImpl) { bind<FeedRepository>() }
    singleOf(::DailyRepositoryImpl) { bind<DailyRepository>() }
    singleOf(::RankingRepositoryImpl) { bind<RankingRepository>() }
    singleOf(::PremiumRepositoryImpl) { bind<PremiumRepository>() }
}
