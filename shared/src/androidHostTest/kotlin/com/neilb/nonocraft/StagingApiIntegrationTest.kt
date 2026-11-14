package com.neilb.nonocraft

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.local.TokenManager
import com.neilb.nonocraft.data.remote.createHttpClient
import com.neilb.nonocraft.data.remote.createJson
import com.neilb.nonocraft.data.remote.service.AuthApiService
import com.neilb.nonocraft.data.remote.service.DailyApiService
import com.neilb.nonocraft.data.remote.service.FeedApiService
import com.neilb.nonocraft.data.remote.service.PuzzleApiService
import com.neilb.nonocraft.data.remote.service.RankingApiService
import com.neilb.nonocraft.data.remote.service.UserApiService
import com.neilb.nonocraft.data.repository.AuthRepositoryImpl
import com.neilb.nonocraft.data.repository.DailyRepositoryImpl
import com.neilb.nonocraft.data.repository.FeedRepositoryImpl
import com.neilb.nonocraft.data.repository.PuzzleRepositoryImpl
import com.neilb.nonocraft.data.repository.RankingRepositoryImpl
import com.neilb.nonocraft.data.repository.UserRepositoryImpl
import com.russhwolf.settings.MapSettings
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * Exercises the real repositories against the API in Constants.API_URL.
 * Skipped unless NONOCRAFT_API_TESTS=1, because it creates a throwaway account.
 */
class StagingApiIntegrationTest {

    private val enabled = System.getenv("NONOCRAFT_API_TESTS") == "1"

    private val tokenManager = TokenManager(MapSettings())
    private val client = createHttpClient(OkHttp.create(), createJson(), tokenManager)
    private val auth = AuthRepositoryImpl(AuthApiService(client), tokenManager)
    private val users = UserRepositoryImpl(UserApiService(client))
    private val puzzles = PuzzleRepositoryImpl(PuzzleApiService(client))
    private val feed = FeedRepositoryImpl(FeedApiService(client))
    private val daily = DailyRepositoryImpl(DailyApiService(client))
    private val ranking = RankingRepositoryImpl(RankingApiService(client))

    private fun <T> Resource<T>.describe(): String =
        if (this is Resource.Success) "Success" else "Error(${message})"

    @Test
    fun authenticatedFlow() = runBlocking {
        if (!enabled) return@runBlocking
        val name = "kmpit${Random.nextInt(100_000, 999_999)}"
        val results = linkedMapOf<String, Resource<*>>()

        val registered = auth.register("$name@example.com", name, "password123")
        assertIs<Resource.Success<*>>(registered, registered.describe())
        assertTrue(tokenManager.isLoggedIn)

        val wrongPassword = auth.login("$name@example.com", "wrong-password")
        assertIs<Resource.Error<*>>(wrongPassword)
        assertNotNull(wrongPassword.message)
        println("bad login message: ${wrongPassword.message}")

        results["login"] = auth.login("$name@example.com", "password123")
        val me = users.getMyProfile()
        results["users/me"] = me
        assertEquals(name, me.data?.username, me.describe())

        results["refresh"] = auth.refreshToken()
        results["updateProfile"] = users.updateProfile(username = null, bio = "integration test")
        results["puzzles"] = puzzles.getPuzzles(limit = 5, offset = 0, type = null, difficulty = null)
        results["puzzles(level)"] = puzzles.getPuzzles(limit = 5, offset = 0, type = "level", difficulty = null)
        val level = puzzles.getPuzzleById("level-001")
        results["puzzle level-001"] = level
        assertTrue(level.data?.grid?.isNotEmpty() == true && level.data?.paletteHex?.isNotEmpty() == true, "level grid/palette")
        results["play"] = puzzles.playPuzzle("level-001")
        results["complete"] = puzzles.completePuzzle("level-001")
        results["like"] = puzzles.likePuzzle("level-001")
        results["comments"] = puzzles.getComments("level-001")
        results["feed/latest"] = feed.getLatest(5, 0)
        results["feed/most-liked"] = feed.getMostLiked(5, 0)
        val today = daily.getTodayPuzzle()
        results["daily/today"] = today
        results["daily/complete"] = daily.completeDaily(today.data?.puzzle?.dailyDate.orEmpty())
        results["daily/streak"] = daily.getStreak()
        results["daily/calendar"] = daily.getCalendar()
        results["ranking"] = ranking.getRanking(5, 0)
        results["ranking/my-rank"] = ranking.getMyRank()
        results["deleteAccount"] = users.deleteAccount()

        results.forEach { (call, result) -> println("${call.padEnd(18)} ${result.describe()}") }
        val failed = results.filterValues { it !is Resource.Success }.keys
        assertTrue(failed.isEmpty(), "Failed calls: $failed")
    }
}
