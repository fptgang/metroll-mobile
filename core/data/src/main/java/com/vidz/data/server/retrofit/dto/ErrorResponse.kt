package com.vidz.data.server.retrofit.dto

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)

data class ErrorResponse(
    val error: String = ""
) 