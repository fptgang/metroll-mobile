package com.vidz.qrscanner.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.PointF
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.ViewGroup
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.vidz.base.components.PermissionManager
import com.vidz.base.components.PermissionState
import com.vidz.base.components.PermissionStatus
import kotlinx.coroutines.delay
import java.util.concurrent.Executors
import kotlin.math.max

//region Main Composable Root
@Composable
fun QrScannerScreenRoot(
    navController: NavController,
    modifier: Modifier = Modifier,
    onShowSnackbar: (String) -> Unit,
    qrScannerViewModel: QrScannerViewModel = hiltViewModel(),
) {
    val qrScannerUiState = qrScannerViewModel.uiState.collectAsStateWithLifecycle()
    QrScannerScreen(
        navController = navController,
        qrScannerUiState = qrScannerUiState,
        onShowSnackbar = onShowSnackbar,
        qrScannerViewModel = qrScannerViewModel,
        modifier = modifier
    )
}
//endregion

@SuppressLint("MissingPermission")
@Composable
fun QrScannerScreen(
    navController: NavController,
    qrScannerUiState: State<QrScannerViewModel.QrScannerViewState>,
    onShowSnackbar: (String) -> Unit,
    qrScannerViewModel: QrScannerViewModel,
    modifier: Modifier = Modifier,
) {
    //region Define Var
    val appContext = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    var lastScanned by remember { mutableStateOf<String?>(null) }
    var boundingPoints by remember { mutableStateOf<List<PointF>?>(null) }
    var noQrFrames by remember { mutableIntStateOf(0) }
    var permissionState by remember { mutableStateOf<PermissionState?>(null) }
    var requestPermission: (() -> Unit)? by remember { mutableStateOf(null) }
    // Used later for scaling bounding box
    var previewWidth by remember { mutableIntStateOf(1) }
    var previewHeight by remember { mutableIntStateOf(1) }
    
    // Camera control states
    var camera by remember { mutableStateOf<Camera?>(null) }
    var isFlashlightOn by remember { mutableStateOf(false) }
    //endregion

    //region Event Handler
    LaunchedEffect(permissionState) {
        if (permissionState?.status == PermissionStatus.NOT_REQUESTED) {
            requestPermission?.invoke()
        }
    }

    LaunchedEffect(lastScanned) {
        lastScanned?.let { onShowSnackbar(it) }
    }

    LaunchedEffect(qrScannerUiState.value.status) {
        if (qrScannerUiState.value.status is QrScannerViewModel.ScannerStatus.Success) {
            val vibrator = appContext.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
            vibrator?.let {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(200)
                }
            }
        }
    }

    // Handle flashlight toggle
    LaunchedEffect(isFlashlightOn, camera) {
        camera?.cameraControl?.enableTorch(isFlashlightOn)
    }

    // Aggressive autofocus on center
    LaunchedEffect(camera) {
        camera?.let { cam ->
            while (true) {
                try {
                    val factory = SurfaceOrientedMeteringPointFactory(1.0f, 1.0f)
                    val centerPoint = factory.createPoint(0.5f, 0.5f)
                    val action = FocusMeteringAction.Builder(centerPoint)
                        .setAutoCancelDuration(1, java.util.concurrent.TimeUnit.SECONDS)
                        .build()
                    cam.cameraControl.startFocusAndMetering(action)
                } catch (e: Exception) {
                    // Ignore focus errors
                }
                delay(1500) // Focus every 1.5 seconds
            }
        }
    }
    //endregion

    //region ui
    // Permission composable
    PermissionManager(
        permission = Manifest.permission.CAMERA,
        onPermissionResult = { permissionState = it },
        onRequestPermission = { req -> requestPermission = req }
    )

    if (permissionState?.status == PermissionStatus.GRANTED) {
        when (qrScannerUiState.value.currentScreen) {
            QrScannerViewModel.ScreenState.Scanner -> {
                ScannerScreenContent(
                    navController = navController,
                    qrScannerUiState = qrScannerUiState,
                    qrScannerViewModel = qrScannerViewModel,
                    boundingPoints = boundingPoints,
                    onBoundingPointsChanged = { boundingPoints = it },
                    noQrFrames = noQrFrames,
                    onNoQrFramesChanged = { noQrFrames = it },
                    previewWidth = previewWidth,
                    previewHeight = previewHeight,
                    onPreviewSizeChanged = { width, height ->
                        previewWidth = width
                        previewHeight = height
                    },
                    lifecycleOwner = lifecycleOwner,
                    camera = camera,
                    onCameraReady = { camera = it },
                    isFlashlightOn = isFlashlightOn,
                    onFlashlightToggle = { isFlashlightOn = !isFlashlightOn },
                    modifier = modifier
                )
            }
            QrScannerViewModel.ScreenState.SuccessResult -> {
                ResultScreen(
                    isSuccess = true,
                    message = "Ticket validated successfully!",
                    onScanMore = { qrScannerViewModel.onTriggerEvent(QrScannerViewModel.QrScannerViewEvent.ScanMore) },
                    onBackPressed = { navController.popBackStack() },
                    modifier = modifier
                )
            }
            QrScannerViewModel.ScreenState.FailureResult -> {
                val errorMessage = when (val status = qrScannerUiState.value.status) {
                    is QrScannerViewModel.ScannerStatus.Error -> status.message
                    else -> "Validation failed"
                }
                ResultScreen(
                    isSuccess = false,
                    message = errorMessage,
                    onScanMore = { qrScannerViewModel.onTriggerEvent(QrScannerViewModel.QrScannerViewEvent.ScanMore) },
                    onBackPressed = { navController.popBackStack() },
                    modifier = modifier
                )
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Camera permission is required to scan QR codes")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { requestPermission?.invoke() }) {
                Text(text = "Grant Permission")
            }
        }
    }
    //region Dialog and Sheet
    //endregion
    //endregion
}

@Composable
private fun ScannerScreenContent(
    navController: NavController,
    qrScannerUiState: State<QrScannerViewModel.QrScannerViewState>,
    qrScannerViewModel: QrScannerViewModel,
    boundingPoints: List<PointF>?,
    onBoundingPointsChanged: (List<PointF>?) -> Unit,
    noQrFrames: Int,
    onNoQrFramesChanged: (Int) -> Unit,
    previewWidth: Int,
    previewHeight: Int,
    onPreviewSizeChanged: (Int, Int) -> Unit,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    camera: Camera?,
    onCameraReady: (Camera) -> Unit,
    isFlashlightOn: Boolean,
    onFlashlightToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(top= WindowInsets.statusBars.asPaddingValues().calculateTopPadding())) {
        // Top Bar with Back Button and Flashlight
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Scan QR Code",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Flashlight toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isFlashlightOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                    contentDescription = "Flashlight",
                    tint = if (isFlashlightOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = isFlashlightOn,
                    onCheckedChange = { onFlashlightToggle() }
                )
            }
        }

        // Station Information Card
        StationInfoCard(
            account = qrScannerUiState.value.account,
            stationDetails = qrScannerUiState.value.assignedStationDetails,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Validation Type Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ValidationTypeTab(
                text = "ENTRY",
                isSelected = qrScannerUiState.value.selectedValidationType == ValidationType.ENTRY,
                onClick = { qrScannerViewModel.onTriggerEvent(QrScannerViewModel.QrScannerViewEvent.ChangeValidationType(ValidationType.ENTRY)) },
                modifier = Modifier.weight(1f)
            )
            ValidationTypeTab(
                text = "EXIT",
                isSelected = qrScannerUiState.value.selectedValidationType == ValidationType.EXIT,
                onClick = { qrScannerViewModel.onTriggerEvent(QrScannerViewModel.QrScannerViewEvent.ChangeValidationType(ValidationType.EXIT)) },
                modifier = Modifier.weight(1f)
            )
        }

        // Camera Preview Box
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // Camera PreviewView hosted inside AndroidView
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val analyzerExecutor = Executors.newSingleThreadExecutor()
                        val analysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also { imageAnalysis ->
                                imageAnalysis.setAnalyzer(analyzerExecutor, QrCodeAnalyzer { text, points, imgWidth, imgHeight ->
                                    if (text.isNotBlank()) {
                                        qrScannerViewModel.onTriggerEvent(QrScannerViewModel.QrScannerViewEvent.QrDetected(text))
                                    }

                                    if (points != null) {
                                        onBoundingPointsChanged(points)
                                        onNoQrFramesChanged(0)
                                    } else {
                                        val newFrames = noQrFrames + 1
                                        onNoQrFramesChanged(newFrames)
                                        if (newFrames > 5) {
                                            onBoundingPointsChanged(null)
                                            qrScannerViewModel.onTriggerEvent(QrScannerViewModel.QrScannerViewEvent.ClearStatus)
                                        }
                                    }

                                    onPreviewSizeChanged(imgWidth, imgHeight)
                                })
                            }
                        try {
                            cameraProvider.unbindAll()
                            val cameraInstance = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                analysis
                            )
                            onCameraReady(cameraInstance)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            // Static overlay shading with center square guidance
            Canvas(modifier = Modifier.fillMaxSize()) {
                val overlaySide = minOf(size.width, size.height) * 0.65f
                val left = (size.width - overlaySide) / 2f
                val top = (size.height - overlaySide) / 2f

                // Draw shaded regions (black with alpha)
                val shadeColor = Color.Black.copy(alpha = 0.6f)
                // Top
                drawRect(shadeColor, size = androidx.compose.ui.geometry.Size(size.width, top))
                // Bottom
                drawRect(shadeColor, topLeft = androidx.compose.ui.geometry.Offset(0f, top + overlaySide), size = androidx.compose.ui.geometry.Size(size.width, size.height - (top + overlaySide)))
                // Left
                drawRect(shadeColor, topLeft = androidx.compose.ui.geometry.Offset(0f, top), size = androidx.compose.ui.geometry.Size(left, overlaySide))
                // Right
                drawRect(shadeColor, topLeft = androidx.compose.ui.geometry.Offset(left + overlaySide, top), size = androidx.compose.ui.geometry.Size(size.width - (left + overlaySide), overlaySide))

                // Draw center square border for guidance
                drawRect(
                    color = Color.White,
                    topLeft = androidx.compose.ui.geometry.Offset(left, top),
                    size = androidx.compose.ui.geometry.Size(overlaySide, overlaySide),
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Bounding box overlay
            boundingPoints?.let { pts ->
                if (pts.size >= 2) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val rotated = previewWidth > previewHeight && size.height > size.width

                        // Map points depending on rotation
                        val mappedPoints = pts.map { p ->
                            if (rotated) PointF(p.y, previewWidth - p.x) else p
                        }

                        val frameWidthMapped = if (rotated) previewHeight else previewWidth
                        val frameHeightMapped = if (rotated) previewWidth else previewHeight

                        val scale = max(size.width / frameWidthMapped.toFloat(), size.height / frameHeightMapped.toFloat())

                        val displayWidth = frameWidthMapped * scale
                        val displayHeight = frameHeightMapped * scale

                        val offsetX = (size.width - displayWidth) / 2f
                        val offsetY = (size.height - displayHeight) / 2f

                        val transformed = mappedPoints.map { point ->
                            androidx.compose.ui.geometry.Offset(offsetX + point.x * scale, offsetY + point.y * scale)
                        }

                        var minX = transformed.minOf { it.x }
                        var maxX = transformed.maxOf { it.x }
                        var minY = transformed.minOf { it.y }
                        var maxY = transformed.maxOf { it.y }

                        val width = maxX - minX
                        val height = maxY - minY
                        val expandX = width * 0.15f
                        val expandY = height * 0.15f

                        minX = (minX - expandX).coerceAtLeast(0f)
                        minY = (minY - expandY).coerceAtLeast(0f)
                        maxX = (maxX + expandX).coerceAtMost(size.width)
                        maxY = (maxY + expandY).coerceAtMost(size.height)

                        drawRect(
                            color = Color.Green,
                            topLeft = androidx.compose.ui.geometry.Offset(minX, minY),
                            size = androidx.compose.ui.geometry.Size(maxX - minX, maxY - minY),
                            style = Stroke(width = 4.dp.toPx())
                        )
                    }
                }
            }

            // Status display at bottom center
            val statusText = when (val st = qrScannerUiState.value.status) {
                is QrScannerViewModel.ScannerStatus.Waiting -> "Waiting for QR..."
                is QrScannerViewModel.ScannerStatus.Validating -> "Validating ticket..."
                is QrScannerViewModel.ScannerStatus.Success -> "Success"
                is QrScannerViewModel.ScannerStatus.Error -> "Error: ${st.message}"
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f), shape = RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(text = statusText, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
private fun StationInfoCard(
    account: com.vidz.domain.model.Account?,
    stationDetails: com.vidz.domain.model.Station?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Staff",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = account?.fullName ?: "Staff Member",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Station Scanner",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }

            // Station Information Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Station",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Assigned Station",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                    
                    if (stationDetails != null) {
                        Text(
                            text = stationDetails.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        if (stationDetails.code.isNotBlank()) {
                            Text(
                                text = "Code: ${stationDetails.code}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    } else if (account?.assignedStation?.isNotBlank() == true) {
                        Text(
                            text = account.assignedStation,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Loading details...",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    } else {
                        Text(
                            text = "No station assigned",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ValidationTypeTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = modifier
    ) {
        Text(text = text)
    }
}

@Composable
private fun ResultScreen(
    isSuccess: Boolean,
    message: String,
    onScanMore: () -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Result Icon
        Icon(
            imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
            contentDescription = if (isSuccess) "Success" else "Error",
            tint = if (isSuccess) Color.Green else MaterialTheme.colorScheme.error,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Result Title
        Text(
            text = if (isSuccess) "Success!" else "Failed!",
            style = MaterialTheme.typography.headlineMedium,
            color = if (isSuccess) Color.Green else MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Result Message
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Action Buttons
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onScanMore,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(text = "Scan More")
            }

            OutlinedButton(
                onClick = onBackPressed,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(text = "Back to Home")
            }
        }
    }
}

//region Analyzer
private class QrCodeAnalyzer(
    private val onResult: (String, List<PointF>?, Int, Int) -> Unit
) : ImageAnalysis.Analyzer {

    private val reader = MultiFormatReader().apply {
        setHints(mapOf(DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE)))
    }

    override fun analyze(image: ImageProxy) {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        val width = image.width
        val height = image.height

        val source = PlanarYUVLuminanceSource(
            bytes,
            width,
            height,
            0,
            0,
            width,
            height,
            false
        )
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        try {
            val result = reader.decodeWithState(bitmap)
            val pts = result.resultPoints?.map { PointF(it.x, it.y) }
            onResult(result.text, pts, width, height)
        } catch (_: Exception) {
            // No QR detected in this frame
            onResult("", null, width, height)
        } finally {
            image.close()
        }
    }
}
//endregion 
