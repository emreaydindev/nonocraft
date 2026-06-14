package com.neilb.nonocraft.data.repository

import android.util.Log
import com.neilb.nonocraft.data.model.RequestException
import com.neilb.nonocraft.data.model.request.GameRequest
import com.neilb.nonocraft.data.model.request.ReportRequest
import com.neilb.nonocraft.data.model.response.GameResponse
import com.neilb.nonocraft.data.model.response.GetPuzzlesResponse
import com.neilb.nonocraft.data.model.response.PuzzleSizeResponse
import com.neilb.nonocraft.data.service.ApiService
import com.neilb.nonocraft.domain.repository.ApiRepository
import javax.inject.Inject

private const val TAG = "ApiRepository"

class ApiRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ApiRepository {

    override suspend fun getPublicPuzzles(limit: Int?, skip: Int?): Result<GetPuzzlesResponse> {
        val response = apiService.getPublicPuzzles(limit, skip)

        return if (response.isSuccessful &&
            response.body() != null &&
            response.body()!!.status in arrayOf(200,201)
        ) {
            Result.success(response.body()!!)
        } else if (response.body() == null) {
            Result.failure(RequestException(response.code(), "An error occurred!"))
        } else {
            Log.e(TAG, "addPublicPuzzle: ${response.body()!!.error.toString()}")
            Result.failure(
                RequestException(
                    response.body()!!.status ?: 500,
                    response.body()!!.error.toString()
                )
            )
        }
    }

    override suspend fun addPublicPuzzle(request: GameRequest): Result<GameResponse> {
        val response = apiService.addPublicPuzzle(request)

        return if (response.isSuccessful &&
            response.body() != null &&
            response.body()!!.status in arrayOf(200,201)
        ) {
            Result.success(response.body()!!)
        } else if (response.body() == null) {
            Result.failure(RequestException(response.code(), "An error occurred!"))
        } else {
            Log.e(TAG, "addPublicPuzzle: ${response.body()!!.error.toString()}")
            Result.failure(
                RequestException(
                    response.body()!!.status ?: 500,
                    response.body()!!.error.toString()
                )
            )
        }
    }

    override suspend fun getPuzzleSize(): Result<PuzzleSizeResponse> {
        try {
            val response = apiService.getPuzzleSize()

            return if (response.isSuccessful &&
                response.body() != null &&
                response.body()!!.status in arrayOf(200,201)
            ) {
                Result.success(response.body()!!)
            } else if (response.body() == null) {
                Result.failure(RequestException(response.code(), "An error occurred!"))
            } else {
                Log.e(TAG, "addPublicPuzzle: ${response.body()!!.error.toString()}")
                Result.failure(
                    RequestException(
                        response.body()!!.status ?: 500,
                        response.body()!!.error.toString()
                    )
                )
            }
        } catch (e: Exception) {
            return Result.failure(RequestException(500, "An error occurred!"))
        }
    }

    override suspend fun reportPuzzle(request: ReportRequest): Result<GameResponse> {
        val response = apiService.reportPuzzle(request)

        return if (response.isSuccessful &&
            response.body() != null &&
            response.body()!!.status in arrayOf(200,201)
        ) {
            Result.success(response.body()!!)
        } else if (response.body() == null) {
            Result.failure(RequestException(response.code(), "An error occurred!"))
        } else {
            Log.e(TAG, "reportPuzzle: ${response.body()!!.error.toString()}")
            Result.failure(
                RequestException(
                    response.body()!!.status ?: 500,
                    response.body()!!.error.toString()
                )
            )
        }
    }

}