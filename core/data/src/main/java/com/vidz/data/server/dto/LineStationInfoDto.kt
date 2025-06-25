package com.vidz.data.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class LineStationInfoDto(
    val lineCode: String,
    val code: String,
    val sequence: Int
) 