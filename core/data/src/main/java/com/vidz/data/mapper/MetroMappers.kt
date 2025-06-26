package com.vidz.data.mapper

import com.vidz.data.server.dto.LineStationInfoDto
import com.vidz.data.server.dto.MetroLineDto
import com.vidz.data.server.dto.MetroLineRequestDto
import com.vidz.data.server.dto.SegmentDto
import com.vidz.data.server.dto.SegmentRequestDto
import com.vidz.data.server.dto.StationDto
import com.vidz.domain.model.LineStationInfo
import com.vidz.domain.model.MetroLine
import com.vidz.domain.model.Segment
import com.vidz.domain.model.Station

// MetroLine Mappers
fun MetroLineDto.toDomain(): MetroLine {
    return MetroLine(
        id = id,
        code = code,
        name = name,
        color = color,
        operatingHours = operatingHours,
        status = status,
        description = description,
        segments = segments.map { it.toDomain() }
    )
}

fun MetroLine.toRequestDto(): MetroLineRequestDto {
    return MetroLineRequestDto(
        id = id,
        code = code,
        name = name,
        color = color,
        operatingHours = operatingHours,
        status = status,
        description = description,
        segments = segments.map { it.toRequestDto() }
    )
}

// Station Mappers
fun StationDto.toDomain(): Station {
    return Station(
        id = id,
        code = code,
        name = name,
        address = address,
        latitude = lat,
        longitude = lng,
        status = status,
        description = description,
        lineStationInfos = lineStationInfos.map { it.toDomain() }
    )
}

fun Station.toDto(): StationDto {
    return StationDto(
        id = id,
        code = code,
        name = name,
        address = address,
        lat = latitude,
        lng = longitude,
        status = status,
        description = description,
        lineStationInfos = lineStationInfos.map { it.toDto() }
    )
}

// Segment Mappers
fun SegmentDto.toDomain(): Segment {
    return Segment(
        sequence = sequence,
        distance = distance,
        travelTime = travelTime,
        description = description,
        lineId = lineId,
        startStation = startStation?.toDomain(),
        endStation = endStation?.toDomain(),
        startStationCode = startStationCode,
        startStationSequence = startStationSequence,
        endStationCode = endStationCode,
        endStationSequence = endStationSequence
    )
}

fun Segment.toRequestDto(): SegmentRequestDto {
    return SegmentRequestDto(
        sequence = sequence,
        distance = distance,
        travelTime = travelTime,
        description = description,
        startStationCode = startStationCode,
        endStationCode = endStationCode
    )
}

// LineStationInfo Mappers
fun LineStationInfoDto.toDomain(): LineStationInfo {
    val lineCode = lineCode?.ifEmpty { "" }
    return LineStationInfo(
        lineCode = lineCode,
        code = code,
        sequence = sequence
    )
}

fun LineStationInfo.toDto(): LineStationInfoDto {
    val lineCode = lineCode?.ifEmpty { "" }
    return LineStationInfoDto(
        lineCode = lineCode,
        code = code,
        sequence = sequence
    )
} 