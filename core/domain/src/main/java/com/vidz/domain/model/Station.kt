package com.vidz.domain.model

data class Station(
    val id: String,
    val code: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val status: String,
    val description: String? = "",
    val lineStationInfos: List<LineStationInfo> = emptyList()
) 