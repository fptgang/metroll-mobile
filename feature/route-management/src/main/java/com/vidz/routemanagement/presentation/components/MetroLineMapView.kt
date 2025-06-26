package com.vidz.routemanagement.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
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
import com.mapbox.maps.extension.compose.MapboxMapScope
import androidx.compose.runtime.DisposableEffect
import com.mapbox.maps.extension.compose.MapEffect

@Composable
fun MetroLineMapView(
    selectedMetroLine: MetroLine?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (selectedMetroLine != null && selectedMetroLine.segments.isNotEmpty()) {
                val mapViewportState = remember {
                    MapViewportState().apply {
                        // Calculate center point from segments
                        val allPoints = selectedMetroLine.segments.mapNotNull { segment ->
                            listOfNotNull(
                                segment.startStation?.let { Point.fromLngLat(it.longitude, it.latitude) },
                                segment.endStation?.let { Point.fromLngLat(it.longitude, it.latitude) }
                            )
                        }.flatten()
                        
                        if (allPoints.isNotEmpty()) {
                            val avgLat = allPoints.map { it.latitude() }.average()
                            val avgLng = allPoints.map { it.longitude() }.average()
                            setCameraOptions {
                                center(Point.fromLngLat(avgLng, avgLat))
                                zoom(12.0)
                            }
                        }
                    }
                }
                
                MapboxMap(
                    Modifier.fillMaxSize(),
                    mapViewportState = mapViewportState
                ) {
                    MapEffect(selectedMetroLine) { mapView ->
                        if (selectedMetroLine != null) {
                            val mapboxMap = mapView.mapboxMap
                            mapboxMap.loadStyle(Style.STANDARD) { style ->
                                // Remove existing layers and sources if they exist
                                try {
                                    style.removeStyleLayer("metro-line-layer")
                                    style.removeStyleSource("metro-line-source")
                                    style.removeStyleLayer("metro-stations-layer")
                                    style.removeStyleLayer("metro-stations-labels-layer")
                                    style.removeStyleSource("metro-stations-source")
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
                                            textHaloWidth(2.0)
                                            textOffset(listOf(0.0, 2.0))
                                            textAnchor(TextAnchor.TOP)
                                            textJustify(TextJustify.CENTER)
                                            textAllowOverlap(false)
                                            textIgnorePlacement(false)
                                        }
                                    )
                                }
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