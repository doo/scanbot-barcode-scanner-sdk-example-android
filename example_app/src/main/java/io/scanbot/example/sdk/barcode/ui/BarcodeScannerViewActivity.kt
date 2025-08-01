package io.scanbot.example.sdk.barcode.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import io.scanbot.example.sdk.barcode.R
import io.scanbot.example.sdk.barcode.model.BarcodeTypeRepository
import io.scanbot.example.sdk.barcode.ui.util.applyEdgeToEdge
import io.scanbot.sdk.barcode.BarcodeItem
import io.scanbot.sdk.barcode.BarcodeScannerResult
import io.scanbot.sdk.barcode.setBarcodeFormats
import io.scanbot.sdk.barcode.textWithExtension
import io.scanbot.sdk.barcode.ui.BarcodePolygonsView
import io.scanbot.sdk.barcode.ui.BarcodeScannerView
import io.scanbot.sdk.barcode.ui.IBarcodeScannerViewCallback
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.CaptureInfo
import io.scanbot.sdk.camera.FrameHandlerResult

class BarcodeScannerViewActivity : AppCompatActivity() {
    private lateinit var barcodeScannerView: BarcodeScannerView
    private lateinit var resultView: ImageView
    private lateinit var flash: View

    private var flashEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanner_view)

        applyEdgeToEdge(this.findViewById(R.id.root_view))

        barcodeScannerView = findViewById(R.id.barcode_scanner_view)
        resultView = findViewById(R.id.result)

        val barcodeScanner = ScanbotBarcodeScannerSDK(this).createBarcodeScanner()
        barcodeScanner.setConfiguration(barcodeScanner.copyCurrentConfiguration().apply {
            setBarcodeFormats(BarcodeTypeRepository.selectedTypes.toList())
        })
        barcodeScannerView.apply {
            initCamera()
            initScanningBehavior(
                barcodeScanner,
                { result ->
                    if (result is FrameHandlerResult.Success) {
                        handleSuccess(result)
                    } else {
                        barcodeScannerView.post {
                            Toast.makeText(
                                this@BarcodeScannerViewActivity,
                                "License has expired!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    false
                },
                object : IBarcodeScannerViewCallback {
                    override fun onCameraOpen() {
                        barcodeScannerView.viewController.useFlash(flashEnabled)
                    }

                    override fun onPictureTaken(image: ByteArray, captureInfo: CaptureInfo) {
                        // we don't need full size pictures in this example
                    }

                    override fun onSelectionOverlayBarcodeClicked(barcodeItem: BarcodeItem) {
                        // here you can handle the click on the barcode item
                    }
                }
            )
        }

        // Setting the Selection Overlay (AR)
        barcodeScannerView.selectionOverlayController.setEnabled(true)
        barcodeScannerView.viewController.apply {
            // This is important for Selection Overlay to work properly
            barcodeScanningInterval = 0
            autoSnappingEnabled = false
        }
        barcodeScannerView.selectionOverlayController.setBarcodeAppearanceDelegate(object :
            BarcodePolygonsView.BarcodeAppearanceDelegate {
            override fun getPolygonStyle(
                defaultStyle: BarcodePolygonsView.BarcodePolygonStyle,
                barcodeItem: BarcodeItem
            ): BarcodePolygonsView.BarcodePolygonStyle {
                return defaultStyle.copy(strokeColor = Color.CYAN)
            }

            override fun getTextViewStyle(
                defaultStyle: BarcodePolygonsView.BarcodeTextViewStyle,
                barcodeItem: BarcodeItem
            ): BarcodePolygonsView.BarcodeTextViewStyle {
                return defaultStyle.copy(textColor = Color.BLACK)
            }

        })

        flash = findViewById(R.id.flash)
        flash.setOnClickListener {
            flashEnabled = !flashEnabled
            barcodeScannerView.viewController.useFlash(flashEnabled)
        }
    }

    override fun onResume() {
        super.onResume()
        barcodeScannerView.viewController.onResume()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Use onActivityResult to handle permission rejection
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION_CODE
            )
        }
    }

    override fun onPause() {
        super.onPause()
        barcodeScannerView.viewController.onPause()
    }

    private fun handleSuccess(result: FrameHandlerResult.Success<BarcodeScannerResult?>) {
        result.value?.let {
            // TODO: uncomment if you wish to proceed to the result screen automatically
            // BarcodeResultRepository.barcodeResultBundle = BarcodeResultBundle(it)
            // val intent = Intent(this, BarcodeResultActivity::class.java)
            // startActivity(intent)
            // finish()
        }
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 200
    }
}
