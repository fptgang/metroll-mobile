package com.vidz.domain.model

data class Segment(
    val sequence: Int,
    val distance: Double,
    val travelTime: Int,
    val description: String,
    val lineId: String,
    val startStation: Station? = null,
    val endStation: Station? = null,
    val startStationCode: String,
    val startStationSequence: Int,
    val endStationCode: String,
    val endStationSequence: Int
) 