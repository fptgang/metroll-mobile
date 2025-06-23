package com.vidz.data.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class PageDto<T>(
    val content: List<T> = emptyList(),
    val pageNumber: Int = 0,
    val pageSize: Int = 0,
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val last: Boolean = false
) 