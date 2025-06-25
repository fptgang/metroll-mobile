package com.vidz.domain.model

data class MetroLine(
    val id: String,
    val code: String,
    val name: String,
    val color: String,
    val operatingHours: String,
    val status: String,
    val description: String,
    val segments: List<Segment> = emptyList()
) 