package com.vidz.data.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class SegmentDto(
    val sequence: Int,
    val distance: Double,
    val travelTime: Int,
    val description: String,
    val lineId: String,
    val startStation: StationDto? = null,
    val endStation: StationDto? = null,
    val startStationCode: String,
    val startStationSequence: Int,
    val endStationCode: String,
    val endStationSequence: Int
) 