package com.neilb.nonocraft.di

import com.neilb.nonocraft.data.remote.createHttpClient
import com.neilb.nonocraft.data.remote.createJson
import com.neilb.nonocraft.data.remote.service.AuthApiService
import com.neilb.nonocraft.data.remote.service.DailyApiService
import com.neilb.nonocraft.data.remote.service.FeedApiService
import com.neilb.nonocraft.data.remote.service.PlaylistApiService
import com.neilb.nonocraft.data.remote.service.PremiumApiService
import com.neilb.nonocraft.data.remote.service.PuzzleApiService
import com.neilb.nonocraft.data.remote.service.RankingApiService
import com.neilb.nonocraft.data.remote.service.SocialApiService
import com.neilb.nonocraft.data.remote.service.UserApiService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val networkModule = module {
    single { createJson() }
    single { createHttpClient(engine = get(), json = get(), tokenManager = get()) }

    singleOf(::AuthApiService)
    singleOf(::UserApiService)
    singleOf(::PuzzleApiService)
    singleOf(::SocialApiService)
    singleOf(::PlaylistApiService)
    singleOf(::FeedApiService)
    singleOf(::DailyApiService)
    singleOf(::RankingApiService)
    singleOf(::PremiumApiService)
}
