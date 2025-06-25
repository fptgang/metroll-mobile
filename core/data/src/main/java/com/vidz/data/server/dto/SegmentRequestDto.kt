package com.vidz.data.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class SegmentRequestDto(
    val sequence: Int,
    val distance: Double,
    val travelTime: Int,
    val description: String,
    val startStationCode: String,
    val endStationCode: String
) 