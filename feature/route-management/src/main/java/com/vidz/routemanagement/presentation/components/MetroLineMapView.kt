package com.vidz.routemanagement.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.generated.circleLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor
import com.mapbox.maps.extension.style.layers.properties.generated.TextJustify
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateBearing
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.vidz.domain.model.MetroLine
import com.vidz.domain.model.Station
import com.mapbox.maps.extension.compose.MapboxMapScope
import androidx.compose.runtime.DisposableEffect
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.rememberMapState
import com.mapbox.maps.extension.style.layers.addLayerAbove
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures
import com.vidz.domain.model.P2PJourney

@Composable
fun MetroLineMapView(
    selectedMetroLine: MetroLine?,
    stations: List<Station>,
    p2pJourney: P2PJourney? = null,
    onStationClick: (Station) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Create a stable key for the map to prevent unnecessary recomposition
    val mapKey = remember(selectedMetroLine?.id, p2pJourney?.id) {
        "${selectedMetroLine?.id ?: "none"}_${p2pJourney?.id ?: "none"}"
    }
    
    // Map height is now controlled by parent container weight animation
    
    // Calculate default camera position based on selected metro line stations
    val defaultCameraOptions = remember(selectedMetroLine, stations) {
        if (selectedMetroLine != null && stations.isNotEmpty()) {
            // Get stations on the selected metro line
            val lineStations = stations.filter { station ->
                station.lineStationInfos.any { it.lineCode == selectedMetroLine.code }
            }
            
            if (lineStations.isNotEmpty()) {
                // Calculate bounds to fit all stations on the line
                val minLat = lineStations.minOf { it.latitude }
                val maxLat = lineStations.maxOf { it.latitude }
                val minLng = lineStations.minOf { it.longitude }
                val maxLng = lineStations.maxOf { it.longitude }
                
                // Add padding to the bounds
                val padding = 0.02
                val paddedMinLat = minLat - padding
                val paddedMaxLat = maxLat + padding
                val paddedMinLng = minLng - padding
                val paddedMaxLng = maxLng + padding
                
                // Calculate center
                val centerLat = (paddedMinLat + paddedMaxLat) / 2
                val centerLng = (paddedMinLng + paddedMaxLng) / 2
                
                // Calculate zoom level based on distance
                val latDiff = paddedMaxLat - paddedMinLat
                val lngDiff = paddedMaxLng - paddedMinLng
                val maxDiff = maxOf(latDiff, lngDiff)
                
                val zoom = when {
                    maxDiff > 0.2 -> 9.0
                    maxDiff > 0.1 -> 10.0
                    maxDiff > 0.05 -> 11.0
                    maxDiff > 0.02 -> 12.0
                    maxDiff > 0.01 -> 13.0
                    else -> 14.0
                }
                
                CameraOptions.Builder()
                    .center(Point.fromLngLat(centerLng, centerLat))
                    .zoom(zoom)
                    .pitch(0.0)
                    .bearing(0.0)
                    .build()
            } else {
                // Fallback to Ho Chi Minh City coordinates
                CameraOptions.Builder()
                    .center(Point.fromLngLat(106.6297, 10.8231))
                    .zoom(12.0)
                    .pitch(0.0)
                    .bearing(0.0)
                    .build()
            }
        } else {
            // Fallback to Ho Chi Minh City coordinates
            CameraOptions.Builder()
                .center(Point.fromLngLat(106.6297, 10.8231))
                .zoom(12.0)
                .pitch(0.0)
                .bearing(0.0)
                .build()
        }
    }
    
    // Create a stable viewport state with default camera options
    val mapViewportState = remember(mapKey) {
        MapViewportState().apply {
            setCameraOptions(defaultCameraOptions)
        }
    }
    
    // Effect to focus map on journey when selected or return to default when cleared
    LaunchedEffect(p2pJourney, stations, selectedMetroLine) {
        if (p2pJourney != null && stations.isNotEmpty()) {
            val startStation = stations.find { it.code == p2pJourney.startStationId }
            val endStation = stations.find { it.code == p2pJourney.endStationId }
            
            if (startStation != null && endStation != null) {
                // Calculate bounds to fit both stations
                val minLat = minOf(startStation.latitude, endStation.latitude)
                val maxLat = maxOf(startStation.latitude, endStation.latitude)
                val minLng = minOf(startStation.longitude, endStation.longitude)
                val maxLng = maxOf(startStation.longitude, endStation.longitude)
                
                // Add padding to the bounds
                val padding = 0.015
                val paddedMinLat = minLat - padding
                val paddedMaxLat = maxLat + padding
                val paddedMinLng = minLng - padding
                val paddedMaxLng = maxLng + padding
                
                // Calculate center and zoom level
                val centerLat = (paddedMinLat + paddedMaxLat) / 2
                val centerLng = (paddedMinLng + paddedMaxLng) / 2
                
                // Calculate zoom level based on distance
                val latDiff = paddedMaxLat - paddedMinLat
                val lngDiff = paddedMaxLng - paddedMinLng
                val maxDiff = maxOf(latDiff, lngDiff)
                
                val zoom = when {
                    maxDiff > 0.1 -> 10.0
                    maxDiff > 0.05 -> 11.0
                    maxDiff > 0.03 -> 12.0
                    maxDiff > 0.015 -> 13.0
                    else -> 14.0
                }
                
                mapViewportState.setCameraOptions {
                    center(Point.fromLngLat(centerLng, centerLat))
                    zoom(zoom)
                    pitch(0.0)
                    bearing(0.0)
                }
            }
        } else {
            // When journey is cleared, return to default view showing all stations on the line
            mapViewportState.setCameraOptions(defaultCameraOptions)
        }
    }
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (selectedMetroLine != null && selectedMetroLine.segments.isNotEmpty()) {
                
                MapboxMap(
                    Modifier.fillMaxSize(),
                    mapViewportState = mapViewportState,
                    mapState = rememberMapState()
                ) {
                    MapEffect(selectedMetroLine, stations, p2pJourney) { mapView ->
                        val mapboxMap = mapView.mapboxMap
                        
                        // Set up click listener for stations
                        mapView.gestures.addOnMapClickListener(OnMapClickListener { point ->
                            // Find the closest station to the clicked point
                            val clickedStation = findClosestStation(point, stations)
                            clickedStation?.let { station ->
                                onStationClick(station)
                            }
                            true
                        })
                        
                        mapboxMap.loadStyle(Style.STANDARD) { style ->
                            // Remove existing layers and sources if they exist
                            try {
                                style.removeStyleLayer("metro-line-layer")
                                style.removeStyleSource("metro-line-source")
                                style.removeStyleLayer("metro-stations-layer")
                                style.removeStyleLayer("metro-stations-labels-layer")
                                style.removeStyleSource("metro-stations-source")
                                style.removeStyleLayer("p2p-journey-layer")
                                style.removeStyleLayer("p2p-selected-stations-layer")
                                style.removeStyleSource("p2p-journey-source")
                                style.removeStyleSource("p2p-selected-stations-source")
                            } catch (e: Exception) {
                                // Layer/source doesn't exist, which is fine
                            }
                            
                            // Create line features from segments
                            val lineFeatures = mutableListOf<Feature>()
                            val uniqueStations = mutableSetOf<com.vidz.domain.model.Station>()
                            
                            selectedMetroLine.segments.forEach { segment ->
                                val startStation = segment.startStation
                                val endStation = segment.endStation
                                if (startStation != null && endStation != null) {
                                    // Add stations to unique set
                                    uniqueStations.add(startStation)
                                    uniqueStations.add(endStation)
                                    
                                    // Create line segment
                                    val lineString = LineString.fromLngLats(
                                        listOf(
                                            Point.fromLngLat(startStation.longitude, startStation.latitude),
                                            Point.fromLngLat(endStation.longitude, endStation.latitude)
                                        )
                                    )
                                    val feature = Feature.fromGeometry(lineString)
                                    feature.addStringProperty("line-color", selectedMetroLine.color)
                                    lineFeatures.add(feature)
                                }
                            }
                            
                            // Create station features
                            val stationFeatures = uniqueStations.map { station ->
                                val point = Point.fromLngLat(station.longitude, station.latitude)
                                val feature = Feature.fromGeometry(point)
                                feature.addStringProperty("station-name", station.name)
                                feature.addStringProperty("station-code", station.code)
                                feature.addStringProperty("station-id", station.id.toString())
                                feature
                            }
                            
                            // Add line source and layer
                            if (lineFeatures.isNotEmpty()) {
                                val lineFeatureCollection = FeatureCollection.fromFeatures(lineFeatures)
                                style.addSource(
                                    geoJsonSource("metro-line-source") {
                                        data(lineFeatureCollection.toJson())
                                    }
                                )
                                
                                val lineColor = parseMetroLineColor(selectedMetroLine.color)
                                style.addLayer(
                                    lineLayer("metro-line-layer", "metro-line-source") {
                                        lineColor(lineColor.toArgb())
                                        lineWidth(6.0)
                                        lineCap(LineCap.ROUND)
                                        lineJoin(LineJoin.ROUND)
                                    }
                                )
                            }
                            
                            // Add station source and layer
                            if (stationFeatures.isNotEmpty()) {
                                val stationFeatureCollection = FeatureCollection.fromFeatures(stationFeatures)
                                style.addSource(
                                    geoJsonSource("metro-stations-source") {
                                        data(stationFeatureCollection.toJson())
                                    }
                                )
                                
                                // Add station circle markers
                                style.addLayer(
                                    circleLayer("metro-stations-layer", "metro-stations-source") {
                                        circleRadius(8.0)
                                        circleColor(Color.White.toArgb())
                                        circleStrokeWidth(3.0)
                                        circleStrokeColor(parseMetroLineColor(selectedMetroLine.color).toArgb())
                                    }
                                )
                                
                                // Add station name labels
                                style.addLayer(
                                    symbolLayer("metro-stations-labels-layer", "metro-stations-source") {
                                        textField("{station-name}")
                                        textSize(12.0)
                                        textColor(Color.Black.toArgb())
                                        textHaloColor(Color.White.toArgb())
                                        textHaloWidth(1.0)
                                        textAnchor(TextAnchor.TOP)
                                        textJustify(TextJustify.CENTER)
                                        textOffset(listOf(0.0, 1.0))
                                    }
                                )
                            }
                            
                            // Add P2P journey route if available
                            p2pJourney?.let { journey ->
                                println("DEBUG: MetroLineMapView - P2P Journey found: ${journey.startStationId} -> ${journey.endStationId}")
                                renderP2PJourney(style, journey, stations, selectedMetroLine)
                            } ?: run {
                                println("DEBUG: MetroLineMapView - No P2P Journey to render")
                            }
                        }
                    }
                }
            } else {
                // Show placeholder when no line is selected
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (selectedMetroLine == null) {
                                "Select a metro line to view on map"
                            } else {
                                "No route data available for this line"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun parseMetroLineColor(color: String): Color {
    return try {
        when {
            color.startsWith("#") -> Color(android.graphics.Color.parseColor(color))
            color.uppercase() == "RED" -> Color.Red
            color.uppercase() == "BLUE" -> Color.Blue
            color.uppercase() == "GREEN" -> Color.Green
            color.uppercase() == "YELLOW" -> Color.Yellow
            color.uppercase() == "PURPLE" -> Color.Magenta
            color.uppercase() == "ORANGE" -> Color(0xFFFF8C00)
            color.uppercase() == "BROWN" -> Color(0xFF8B4513)
            color.uppercase() == "PINK" -> Color(0xFFFFC0CB)
            color.uppercase() == "CYAN" -> Color.Cyan
            color.uppercase() == "GRAY" || color.uppercase() == "GREY" -> Color.Gray
            else -> Color.Gray
        }
    } catch (e: Exception) {
        Color.Gray
    }
} 

// Helper function to create journey path following metro line route
private fun createMetroLineJourneyPath(
    startStation: Station,
    endStation: Station,
    metroLine: MetroLine?,
    allStations: List<Station>
): List<Point> {
    if (metroLine == null) {
        // Fallback to direct line if no metro line data
        return listOf(
            Point.fromLngLat(startStation.longitude, startStation.latitude),
            Point.fromLngLat(endStation.longitude, endStation.latitude)
        )
    }
    
    // Get the line station info for both stations to determine their sequence
    val startLineInfo = startStation.lineStationInfos.find { it.lineCode == metroLine.code }
    val endLineInfo = endStation.lineStationInfos.find { it.lineCode == metroLine.code }
    
    if (startLineInfo == null || endLineInfo == null) {
        // Fallback to direct line if sequence info not available
        return listOf(
            Point.fromLngLat(startStation.longitude, startStation.latitude),
            Point.fromLngLat(endStation.longitude, endStation.latitude)
        )
    }
    
    // Determine the direction of travel
    val startSequence = startLineInfo.sequence
    val endSequence = endLineInfo.sequence
    val isForward = startSequence < endSequence
    
    // Get all stations on this line in sequence order
    val lineStations = allStations
        .filter { station -> 
            station.lineStationInfos.any { it.lineCode == metroLine.code }
        }
        .sortedBy { station ->
            station.lineStationInfos.find { it.lineCode == metroLine.code }?.sequence ?: Int.MAX_VALUE
        }
    
    // Find the range of stations for the journey
    val journeyStations = if (isForward) {
        lineStations.filter { station ->
            val sequence = station.lineStationInfos.find { it.lineCode == metroLine.code }?.sequence ?: Int.MAX_VALUE
            sequence in startSequence..endSequence
        }
    } else {
        lineStations.filter { station ->
            val sequence = station.lineStationInfos.find { it.lineCode == metroLine.code }?.sequence ?: Int.MAX_VALUE
            sequence in endSequence..startSequence
        }.reversed()
    }
    
    // Create points for the journey path
    val journeyPoints = journeyStations.map { station ->
        Point.fromLngLat(station.longitude, station.latitude)
    }.ifEmpty {
        // Fallback to direct line if no journey stations found
        listOf(
            Point.fromLngLat(startStation.longitude, startStation.latitude),
            Point.fromLngLat(endStation.longitude, endStation.latitude)
        )
    }
    
    // Debug logging
    println("DEBUG: Journey path created with ${journeyPoints.size} points")
    journeyStations.forEachIndexed { index, station ->
        println("DEBUG: Station $index: ${station.name} (${station.code})")
    }
    
    return journeyPoints
}

// Helper function to find the closest station to a clicked point
private fun findClosestStation(clickedPoint: Point, stations: List<Station>): Station? {
    if (stations.isEmpty()) return null
    
    val clickLat = clickedPoint.latitude()
    val clickLng = clickedPoint.longitude()
    
    return stations.minByOrNull { station ->
        val deltaLat = station.latitude - clickLat
        val deltaLng = station.longitude - clickLng
        // Simple distance calculation (not geodesic, but sufficient for close points)
        deltaLat * deltaLat + deltaLng * deltaLng
    }
}

// Helper function to render P2P journey route
private fun renderP2PJourney(style: Style, journey: P2PJourney, stations: List<Station>, selectedMetroLine: MetroLine? = null) {
    try {
        // Remove existing P2P journey layers if they exist
        style.removeStyleLayer("p2p-journey-background-layer")
        style.removeStyleLayer("p2p-journey-layer")
        style.removeStyleLayer("p2p-selected-stations-layer")
        style.removeStyleSource("p2p-journey-source")
        style.removeStyleSource("p2p-selected-stations-source")
    } catch (e: Exception) {
        // Layer/source doesn't exist, which is fine
    }
    
    // Find stations by their codes (P2PJourney uses station codes, not IDs)
    val startStation = stations.find { it.code == journey.startStationId }
    val endStation = stations.find { it.code == journey.endStationId }
    
    // Debug logging
    println("DEBUG: P2P Journey - Start: ${journey.startStationId}, End: ${journey.endStationId}")
    println("DEBUG: Found Start Station: ${startStation?.name}, Found End Station: ${endStation?.name}")
    
    if (startStation != null && endStation != null) {
        // Create journey route line following the metro line path
        val journeyPoints = createMetroLineJourneyPath(
            startStation = startStation,
            endStation = endStation,
            metroLine = selectedMetroLine,
            allStations = stations
        )
        
        val journeyLine = LineString.fromLngLats(journeyPoints)
        
        val journeyFeature = Feature.fromGeometry(journeyLine)
        val journeyFeatureCollection = FeatureCollection.fromFeatures(listOf(journeyFeature))
        
        // Add journey source
        style.addSource(
            geoJsonSource("p2p-journey-source") {
                data(journeyFeatureCollection.toJson())
            }
        )
        
        // Add green line with same thickness as metro line
        style.addLayer(
            lineLayer("p2p-journey-layer", "p2p-journey-source") {
                lineColor(Color(0xFF00FF00).toArgb()) // Bright lime green color
                lineWidth(8.0) // Slightly thicker than metro line for visibility
                lineCap(LineCap.ROUND)
                lineJoin(LineJoin.ROUND)
                lineOpacity(0.9) // Slightly transparent for better visual layering
            }
        )
        
        // Highlight selected stations with special markers
        val selectedStationFeatures = listOf(
            Feature.fromGeometry(Point.fromLngLat(startStation.longitude, startStation.latitude)).apply {
                addStringProperty("station-type", "start")
                addStringProperty("station-name", startStation.name)
            },
            Feature.fromGeometry(Point.fromLngLat(endStation.longitude, endStation.latitude)).apply {
                addStringProperty("station-type", "end")
                addStringProperty("station-name", endStation.name)
            }
        )
        
        val selectedStationsFeatureCollection = FeatureCollection.fromFeatures(selectedStationFeatures)
        
        // Add selected stations source
        style.addSource(
            geoJsonSource("p2p-selected-stations-source") {
                data(selectedStationsFeatureCollection.toJson())
            }
        )
        
        // Add selected stations layer with enhanced visibility
        style.addLayer(
            circleLayer("p2p-selected-stations-layer", "p2p-selected-stations-source") {
                circleRadius(12.0) // Larger than regular stations
                circleColor(Color(0xFF00FF00).toArgb()) // Bright lime green color matching the line
                circleStrokeWidth(4.0) // Thicker stroke for better visibility
                circleStrokeColor(Color.White.toArgb()) // White stroke for contrast
                circleOpacity(0.9) // Slightly transparent for better visual layering
            }
        )
    }
} 