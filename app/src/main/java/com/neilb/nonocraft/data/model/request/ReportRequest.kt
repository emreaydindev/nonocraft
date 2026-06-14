package com.neilb.nonocraft.data.model.request

import com.google.gson.annotations.SerializedName

data class ReportRequest(

    @field:SerializedName("puzzleId")
    val puzzleId: String? = null

)
