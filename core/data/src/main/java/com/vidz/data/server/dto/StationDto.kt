package com.vidz.data.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class StationDto(
    val id: String,
    val code: String,
    val name: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val status: String,
    val description: String? ="",
    val lineStationInfos: List<LineStationInfoDto> = emptyList()
) 