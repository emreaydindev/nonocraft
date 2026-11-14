package com.neilb.nonocraft.di

import com.neilb.nonocraft.domain.use_case.auth.ForgotPassword
import com.neilb.nonocraft.domain.use_case.auth.GoogleLogin
import com.neilb.nonocraft.domain.use_case.auth.Login
import com.neilb.nonocraft.domain.use_case.auth.Register
import com.neilb.nonocraft.domain.use_case.auth.ResetPassword
import com.neilb.nonocraft.domain.use_case.auth.VerifyEmail
import com.neilb.nonocraft.domain.use_case.puzzle.CompletePuzzle
import com.neilb.nonocraft.domain.use_case.puzzle.GetPuzzle
import com.neilb.nonocraft.domain.use_case.puzzle.GetPuzzles
import com.neilb.nonocraft.domain.use_case.puzzle.PlayPuzzle
import com.neilb.nonocraft.domain.use_case.user.ChangePassword
import com.neilb.nonocraft.domain.use_case.user.DeleteAccount
import com.neilb.nonocraft.domain.use_case.user.GetMyProfile
import com.neilb.nonocraft.domain.use_case.user.UpdateProfile
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val useCaseModule = module {
    factoryOf(::Login)
    factoryOf(::Register)
    factoryOf(::GoogleLogin)
    factoryOf(::ForgotPassword)
    factoryOf(::ResetPassword)
    factoryOf(::VerifyEmail)

    factoryOf(::GetPuzzle)
    factoryOf(::GetPuzzles)
    factoryOf(::PlayPuzzle)
    factoryOf(::CompletePuzzle)

    factoryOf(::GetMyProfile)
    factoryOf(::UpdateProfile)
    factoryOf(::ChangePassword)
    factoryOf(::DeleteAccount)
}
