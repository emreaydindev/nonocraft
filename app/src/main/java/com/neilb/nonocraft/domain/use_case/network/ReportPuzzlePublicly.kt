package com.neilb.nonocraft.domain.use_case.network

import com.neilb.nonocraft.common.Resource
import com.neilb.nonocraft.data.model.request.ReportRequest
import com.neilb.nonocraft.data.model.response.GameResponse
import com.neilb.nonocraft.domain.repository.ApiRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReportPuzzlePublicly  @Inject constructor(
    private val apiRepository: ApiRepository
) {

    operator fun invoke(body: ReportRequest) : Flow<Resource<GameResponse>> = flow {
        emit(Resource.Loading())
        val response = apiRepository.reportPuzzle(body)

        val resBody = response.getOrNull()

        if (response.isFailure) {
            emit(Resource.Error(response.exceptionOrNull()!!.message!!))
        } else if (resBody == null) {
            emit(Resource.Error("An error occurred!"))
        } else if (response.isSuccess) {
            emit(Resource.Success(resBody))
        }
    }

}