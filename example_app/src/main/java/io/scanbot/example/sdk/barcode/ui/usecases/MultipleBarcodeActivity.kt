package io.scanbot.example.sdk.barcode.ui.usecases

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.scanbot.example.sdk.barcode.R
import io.scanbot.example.sdk.barcode.ui.usecases.adapter.BarcodeItemAdapter
import io.scanbot.sdk.barcode.BarcodeItem
import io.scanbot.sdk.barcode.BarcodeScannerResult
import io.scanbot.sdk.barcode.ui.BarcodeScannerView
import io.scanbot.sdk.barcode.ui.IBarcodeScannerViewCallback
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.CaptureInfo
import io.scanbot.sdk.camera.FrameHandlerResult

class MultipleBarcodeActivity : AppCompatActivity() {
    private lateinit var barcodeScannerView: BarcodeScannerView

// IMPORTANT FOR THIS EXAMPLE:
    private val resultAdapter by lazy { BarcodeItemAdapter() }
    private lateinit var resultView: RecyclerView
// END OF IMPORTANT FOR THIS EXAMPLE:

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanner_recycler_view)

        barcodeScannerView = findViewById(R.id.barcode_scanner_view)

        val barcodeScanner = ScanbotBarcodeScannerSDK(this).createBarcodeScanner()
        barcodeScanner.setConfiguration(
            barcodeScanner.copyCurrentConfiguration().apply {
                // Specify the barcode format you want to scan
                // setBarcodeFormats(listOf(BarcodeFormat.QR_CODE))
            }
        )

        barcodeScannerView.apply {
            initCamera()
            initScanningBehavior(barcodeScanner, { result ->
                if (result is FrameHandlerResult.Success) {
                    handleSuccess(result)
                } else {
                    ExampleUtils.showLicenseExpiredToastAndExit(this@MultipleBarcodeActivity)
                }
                false
            }, object : IBarcodeScannerViewCallback {
                override fun onCameraOpen() {
                }

                override fun onPictureTaken(image: ByteArray, captureInfo: CaptureInfo) {
                    // we don't need full size pictures in this example
                }

                override fun onSelectionOverlayBarcodeClicked(barcodeItem: BarcodeItem) {
                    // handle the barcode item here
                }
            })
        }

        // @Tag("Scanning multiple barcodes")
        // Disable the finder view to hide the barcode scanner viewfinder
        // It allows to locate the barcodes on the full screen
        barcodeScannerView.finderViewController.setFinderEnabled(false)
        // Recommended for Multi-Scan approach
        barcodeScannerView.viewController.barcodeScanningInterval = 0

        resultView = findViewById(R.id.barcode_recycler_view)
        resultView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        resultView.adapter = resultAdapter
        // @EndTag("Scanning multiple barcodes")
    }

    // @Tag("Handle results")
    private fun handleSuccess(result: FrameHandlerResult.Success<BarcodeScannerResult?>) {
        result.value?.let {
            // We need to add the barcode items to the adapter on the main thread
            barcodeScannerView.post {
                resultAdapter.addBarcodeItems(it.barcodes)
                resultView.scrollToPosition(0)
            }
        }
    }
    // @EndTag("Handle results")

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
