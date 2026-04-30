package io.scanbot.example.sdk.barcode.ui.usecases

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.scanbot.common.onFailure
import io.scanbot.common.onSuccess
import io.scanbot.example.sdk.barcode.R
import io.scanbot.example.sdk.barcode.ui.usecases.adapter.BarcodeItemAdapter
import io.scanbot.example.sdk.barcode.ui.util.applyEdgeToEdge
import io.scanbot.sdk.barcode.BarcodeItem
import io.scanbot.sdk.barcode.BarcodeScannerResult
import io.scanbot.sdk.barcode.ui.BarcodeScannerView
import io.scanbot.sdk.barcode.ui.IBarcodeScannerViewCallback
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.CaptureInfo
import io.scanbot.sdk.image.ImageRef

class MultipleBarcodeActivity : AppCompatActivity() {
    private lateinit var barcodeScannerView: BarcodeScannerView

    // IMPORTANT FOR THIS EXAMPLE:
    private val resultAdapter by lazy { BarcodeItemAdapter() }
    private lateinit var resultView: RecyclerView
// END OF IMPORTANT FOR THIS EXAMPLE:

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanner_recycler_view)
        applyEdgeToEdge(this.findViewById(R.id.root_view))

        barcodeScannerView = findViewById(R.id.barcode_scanner_view)

        val barcodeScanner = ScanbotBarcodeScannerSDK(this).createBarcodeScanner().getOrThrow()
        barcodeScanner.setConfiguration(
            barcodeScanner.copyCurrentConfiguration().apply {
                // Specify the barcode format you want to scan
                // setBarcodeFormats(listOf(BarcodeFormat.QR_CODE))
            }
        )

        barcodeScannerView.apply {
            initCamera()
            initScanningBehavior(barcodeScanner, { result, frame ->
                result.onSuccess {
                    handleSuccess(it)
                }.onFailure {
                    when (it) {
                        is io.scanbot.common.Result.InvalidLicenseError -> {
                            io.scanbot.example.sdk.barcode.ui.usecases.ExampleUtils.showLicenseExpiredToastAndExit(
                                this@MultipleBarcodeActivity
                            )
                        }

                        else -> {
                            // handle other errors
                        }
                    }
                }
                false
            }, object : IBarcodeScannerViewCallback {
                override fun onCameraOpen() {
                }

                override fun onPictureTaken(image: ImageRef, captureInfo: CaptureInfo) {
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
    private fun handleSuccess(result: BarcodeScannerResult) {
        // We need to add the barcode items to the adapter on the main thread
        barcodeScannerView.post {
            resultAdapter.addBarcodeItems(result.barcodes)
            resultView.scrollToPosition(0)
        }
    }
    // @EndTag("Handle results")

    override fun onResume() {
        super.onResume()

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

    companion object {
        private const val REQUEST_PERMISSION_CODE = 100
    }
}
