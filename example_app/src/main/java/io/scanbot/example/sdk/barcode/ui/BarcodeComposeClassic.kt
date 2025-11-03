package io.scanbot.example.sdk.barcode.ui

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import io.scanbot.example.sdk.barcode.R
import io.scanbot.example.sdk.barcode.ui.util.applyEdgeToEdge
import io.scanbot.sdk.barcode.BarcodeItem
import io.scanbot.sdk.barcode.BarcodeScannerFrameHandler
import io.scanbot.sdk.barcode.BarcodeScannerResult
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.FrameHandler
import io.scanbot.sdk.camera.FrameHandlerResult
import io.scanbot.sdk.common.AspectRatio
import io.scanbot.sdk.ui_v2.barcode.components.ar_tracking.ScanbotBarcodesArOverlay
import io.scanbot.sdk.ui_v2.barcode.configuration.ArOverlayPolygonConfiguration
import io.scanbot.sdk.ui_v2.common.ActionBarConfiguration
import io.scanbot.sdk.ui_v2.common.CameraConfiguration
import io.scanbot.sdk.ui_v2.common.CameraPermissionScreen
import io.scanbot.sdk.ui_v2.common.Constants
import io.scanbot.sdk.ui_v2.common.ViewFinderConfiguration
import io.scanbot.sdk.ui_v2.common.activity.CanceledByUser
import io.scanbot.sdk.ui_v2.common.activity.CloseReason
import io.scanbot.sdk.ui_v2.common.camera.FinderConfiguration
import io.scanbot.sdk.ui_v2.common.camera.ScanbotComposeCamera
import io.scanbot.sdk.ui_v2.common.camera.ScanbotComposeCameraViewModel
import io.scanbot.sdk.ui_v2.common.components.ArComposeView
import io.scanbot.sdk.ui_v2.common.components.ScanbotCameraActionBar
import io.scanbot.sdk.ui_v2.common.components.ScanbotCameraPermissionView
import io.scanbot.sdk.ui_v2.common.components.ScanbotSystemBar
import io.scanbot.sdk.ui_v2.common.components.toFinder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking


class BarcodeComposeClassic : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doc_snippet_activity_artu_barcode_scanner)

        // If you use "traditional" Android XML-driven UI - integrate ComposeView into your layout
        // and use code below to render our BarcodeScannerView in it.
        val cameraContainerView: ComposeView = findViewById(R.id.compose_container)
        applyEdgeToEdge(cameraContainerView)
        cameraContainerView.apply {
            setContent {
                BarcodeScannerViewClassic(
                    modifier = Modifier.fillMaxSize(),
                    enableBackNavigation = true,
                    onScannerClosed = { closeReason ->

                    },
                    onBarcodesScanned = { barcodeResult ->
                        Log.d(
                            "BarcodeComposeClassic", "Scanned barcodes: ${barcodeResult.barcodes}"
                        )
                    },
                    viewModel = BarcodeScannerViewModel(
                        cameraConfiguration = CameraConfiguration(),
                        sdk = ScanbotBarcodeScannerSDK(context),
                        flashAvailable = true, // check flash availability on the device
                    )
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterialApi::class)
@androidx.camera.camera2.interop.ExperimentalCamera2Interop
fun BarcodeScannerViewClassic(
    modifier: Modifier = Modifier.fillMaxSize(),
    enableBackNavigation: Boolean = true,
    onScannerClosed: (CloseReason) -> Unit = {},
    onBarcodesScanned: (BarcodeScannerResult) -> Unit = {},
    viewModel: BarcodeScannerViewModel,
) {

    LaunchedEffect(key1 = Unit) {
        viewModel.scanningResults.collect { scannerResult ->
            scannerResult?.let { onBarcodesScanned(it) }
        }
    }
    val context = LocalContext.current
    val previewMode = LocalInspectionMode.current

    BoxWithConstraints(modifier = modifier) {
        this.maxWidth
        val density = LocalDensity.current

        val scope = rememberCoroutineScope()
        BackHandler(enableBackNavigation, onBack = {
            onScannerClosed(CanceledByUser)
        })

        val backgroundColor = Color.Black

        val permissionGranted = if (!previewMode) {
            val cameraPermissionState =
                rememberPermissionState(permission = Manifest.permission.CAMERA)

            // start scanning delay dialog counter if camera permissions are granted

            checkPermissionStatus(cameraPermissionState) {
                viewModel.permissionEnabled.value = cameraPermissionState.status.isGranted
            }
            cameraPermissionState.status.isGranted
        } else {
            true
        }
        if (permissionGranted) {

            val scaffoldState = rememberScaffoldState()

            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor),
                topBar = {
                    TopAppBar(
                        title = { Text(text = "Scan Barcode") }, backgroundColor = Color.Green
                    )
                },
                backgroundColor = backgroundColor,
                scaffoldState = scaffoldState,
                content = { paddingValues ->
                    BoxWithConstraints(modifier = modifier) {
                        this.maxWidth
                        val cornerRadius = with(LocalDensity.current) {
                            MaterialTheme.shapes.large.topEnd.toPx(
                                Size(
                                    maxWidth.toPx(), maxHeight.toPx()
                                ), LocalDensity.current
                            ).toDp()
                        }
                        ScanbotComposeCamera(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    bottom = paddingValues.calculateBottomPadding(),
                                    end = paddingValues.calculateEndPadding(
                                        LocalLayoutDirection.current
                                    )
                                ),
                            viewModel = viewModel,
                            cameraBackgroundColor = backgroundColor,
                            onViewCreated = { camera ->
                                if (!previewMode) {
                                    camera.removeFrameHandler(viewModel.frameHandler)
                                    camera.addFrameHandler(viewModel.frameHandler)
                                }
                            },
                            cameraArOverlayView = ArComposeView(context).apply {
                                setContent {
                                    DefaultArView(
                                        viewModel.scanningResults,
                                        viewModel.frameFlow,
                                        onBarcodeClick = { barcodeItem ->
                                            //handle click on barcode polygon
                                        },
                                        ArOverlayPolygonConfiguration(),
                                        density
                                    )
                                }
                            },
                            finderConfiguration = ViewFinderConfiguration().toFinder(
                                bottomSafeArea = 100.dp,
                                topSafeArea = 100.dp
                            ),
                        )

                    }
                },
                // action bar max scroll position from the bottom consists of sheet pick height, safe area(action bar with its padding itself) and the area from it to hint bottom
                floatingActionButton = {
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {

                        Box(Modifier.height(Constants.Ui.actionBarHeight)) {
                            ScanbotCameraActionBar(
                                config = ActionBarConfiguration(),
                                flashEnabled = viewModel.flashEnabled.collectAsState(),
                                flashButtonEnabled = viewModel.flashButtonEnabled.collectAsState(),
                                zoomState = viewModel.zoomFactorUi.collectAsState(),
                                cameraModule = viewModel.cameraModule.collectAsState(),
                                onAction = { viewModel.onAction(it) },
                            )
                        }
                    }
                },
            )

        } else {
            ScanbotSystemBar(
                systemBarColor = Color.Transparent,
                statusBarDarkIcons = true
            ) {
                ScanbotCameraPermissionView(
                    modifier = modifier,
                    bottomContentPadding = 0.dp,
                    permissionConfig = CameraPermissionScreen(),
                    onClose = { onScannerClosed(CanceledByUser) })
            }
        }
    }
}

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun checkPermissionStatus(
    cameraPermissionState: PermissionState,
    permissionGrantedBlock: CoroutineScope.() -> Unit,
) {
    LaunchedEffect(
        key1 = cameraPermissionState.status.isGranted, block = permissionGrantedBlock
    )
    if (cameraPermissionState.status == PermissionStatus.Denied(false)) {
        if (!LocalInspectionMode.current) {
            SideEffect {
                cameraPermissionState.launchPermissionRequest()
            }
        }
    }
}


@Composable
fun DefaultArView(
    barcodesFlow: MutableSharedFlow<BarcodeScannerResult?>,
    framesFlow: MutableSharedFlow<FrameHandler.Frame>,
    onBarcodeClick: (BarcodeItem) -> Unit = {},
    polygon: ArOverlayPolygonConfiguration,
    density: Density,
) {
    ScanbotBarcodesArOverlay(
        barcodesFlow,
        getData = { barcodeItem -> },
        shouldHighlight = { barcodeItem -> false },
        view = { path, barcodeItem, data, shouldHighlight ->

        },
        getPolygonStyle = { defaultStyle, barcodeItem ->
            defaultStyle.copy(
                drawPolygon = polygon.visible,
                useFill = true,
                useFillHighlighted = true,
                cornerRadius = density.run { polygon.deselected.cornerRadius.dp.toPx() },
                cornerHighlightedRadius = density.run { polygon.selected.cornerRadius.dp.toPx() },
                strokeWidth = density.run { polygon.deselected.strokeWidth.dp.toPx() },
                strokeHighlightedWidth = density.run { polygon.selected.strokeWidth.dp.toPx() },
                strokeColor = Color.Green,
                strokeHighlightedColor = Color.Red,
                fillColor = Color.Green.copy(alpha = 0.3f),
                fillHighlightedColor = Color.Red.copy(alpha = 0.3f),
                shouldDrawShadows = false
            )
        },
        onClick = onBarcodeClick,
        frameFlow = framesFlow
    )
}

class BarcodeScannerViewModel(
    val cameraConfiguration: CameraConfiguration,
    val sdk: ScanbotBarcodeScannerSDK,
    val flashAvailable: Boolean = true,
) : ScanbotComposeCameraViewModel(
    cameraConfiguration.cameraModule,
    initialZoomSteps = cameraConfiguration.zoomSteps,
    defaultZoomFactor = cameraConfiguration.defaultZoomFactor,
    initialFlashEnabled = cameraConfiguration.flashEnabled,
    initialMinFocusDistanceLock = cameraConfiguration.minFocusDistanceLock,
    initialTouchToFocusEnabled = cameraConfiguration.touchToFocusEnabled,
    initialPinchToZoomEnabled = cameraConfiguration.pinchToZoomEnabled,
    initialPlayFlashOnSnap = false,
    initialOrientationLockMode = cameraConfiguration.orientationLockMode,
    initialCameraPreviewMode = cameraConfiguration.cameraPreviewMode,
    flashAvailable = flashAvailable,

    initialFpsLimit = 20
) {
    val frameHandler: BarcodeScannerFrameHandler =
        BarcodeScannerFrameHandler(sdk.createBarcodeScanner()).apply {
            setScanningInterval(50)
        }
    val scanningResults: MutableSharedFlow<BarcodeScannerResult?> = MutableSharedFlow()
    val scanningResultFlow: MutableSharedFlow<BarcodeScannerResult?> =
        MutableSharedFlow()
    val frameFlow: MutableSharedFlow<FrameHandler.Frame> =
        MutableSharedFlow()
    val handler = BarcodeScannerFrameHandler.ResultHandler { result ->
        Log.d("BarcodeScannerViewModel", result.toString())
        if (result is FrameHandlerResult.Success) {
            val res = result.value
            // handle result as needed
            // here is just example how to stream it via Flow to the Composable caller
            runBlocking {
                res?.let { scanningResultFlow.emit(res) }
                frameFlow.emit(result.frame)
                this@BarcodeScannerViewModel.scanningResults.emit(res)
            }
        }
        false
    }

    init {
        frameHandler.addResultHandler(handler)
    }
}
