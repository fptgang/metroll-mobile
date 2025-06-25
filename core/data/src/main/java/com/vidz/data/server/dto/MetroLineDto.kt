package com.vidz.data.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class MetroLineDto(
    val id: String,
    val code: String,
    val name: String,
    val color: String,
    val operatingHours: String,
    val status: String,
    val description: String,
    val segments: List<SegmentDto> = emptyList()
) 