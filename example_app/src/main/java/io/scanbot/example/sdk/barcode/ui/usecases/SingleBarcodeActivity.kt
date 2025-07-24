package io.scanbot.example.sdk.barcode.ui.usecases

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.scanbot.example.sdk.barcode.R
import io.scanbot.example.sdk.barcode.ui.util.applyEdgeToEdge
import io.scanbot.sdk.barcode.BarcodeItem
import io.scanbot.sdk.barcode.BarcodeScannerResult
import io.scanbot.sdk.barcode.ui.BarcodeScannerView
import io.scanbot.sdk.barcode.ui.IBarcodeScannerViewCallback
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.CaptureInfo
import io.scanbot.sdk.camera.FrameHandlerResult

class SingleBarcodeActivity : AppCompatActivity() {

    private var flashEnabled = false
    private lateinit var barcodeScannerView: BarcodeScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanner_view)
        applyEdgeToEdge(this.findViewById(R.id.root_view))

        barcodeScannerView = findViewById(R.id.barcode_scanner_view)

        val barcodeScanner = ScanbotBarcodeScannerSDK(this).createBarcodeScanner()
barcodeScanner.setConfiguration(
            barcodeScanner.copyCurrentConfiguration().apply {
                // Specify the barcode format you want to scan
                // setBarcodeFormats(listOf(BarcodeFormat.QR_CODE))
            }
        )

        // @Tag("Scanning single barcode")
        barcodeScannerView.apply {
            initCamera()
            initScanningBehavior(barcodeScanner, { result ->
                if (result is FrameHandlerResult.Success) {
                    handleSuccess(result)
                } else {
                    ExampleUtils.showLicenseExpiredToastAndExit(this@SingleBarcodeActivity)
                }
                false
            }, object : IBarcodeScannerViewCallback {
                override fun onCameraOpen() {
                    barcodeScannerView.viewController.useFlash(flashEnabled)
                }

                override fun onPictureTaken(image: ByteArray, captureInfo: CaptureInfo) {
                    // we don't need full size pictures in this example
                }

                override fun onSelectionOverlayBarcodeClicked(barcodeItem: BarcodeItem) {
                    // handle the barcode item here
                }
            })
        }
        // @EndTag("Scanning single barcode")

        val flash: Button = findViewById(R.id.flash)
        flash.setOnClickListener {
            flashEnabled = !flashEnabled
            barcodeScannerView.viewController.useFlash(flashEnabled)
        }
    }

    private fun handleSuccess(result: FrameHandlerResult.Success<BarcodeScannerResult?>) {
        result.value?.let {
            barcodeScannerView.viewController.isFrameProcessingEnabled = false
            runOnUiThread {
                ExampleUtils.showBarcodeResult(
                    this@SingleBarcodeActivity, it
                ) { barcodeScannerView.viewController.isFrameProcessingEnabled = true }
            }
            // You may also finish the scanning and proceed to the separate result screen
            // val barcodeItems = it.barcodeItems
            // val intent = Intent()
            // intent.putExtra("BARCODES_ARG", barcodeItems.toTypedArray())
            // finish()
        }
    }

    override fun onResume() {
        super.onResume()
        barcodeScannerView.viewController.onResume()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Use onActivityResult to handle permission rejection
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CODE)
        }
    }

    override fun onPause() {
        super.onPause()
        barcodeScannerView.viewController.onPause()
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 100
    }
}
