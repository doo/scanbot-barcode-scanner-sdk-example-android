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
import io.scanbot.sdk.barcode.textWithExtension
import io.scanbot.sdk.barcode.ui.BarcodeOverlayTextFormat
import io.scanbot.sdk.barcode.ui.BarcodePolygonsView
import io.scanbot.sdk.barcode.ui.BarcodeScannerView
import io.scanbot.sdk.barcode.ui.IBarcodeScannerViewCallback
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.CaptureInfo
import io.scanbot.sdk.camera.FrameHandlerResult

class AR_SelectScanActivity : AppCompatActivity() {
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

        // @Tag("AR-SelectScan")
        barcodeScannerView.apply {
            initCamera()
            initScanningBehavior(barcodeScanner, { result ->
                if (result is FrameHandlerResult.Success) {
// IMPORTANT FOR THIS EXAMPLE:
                    // We keep this part empty as we process barcodes only when the barcode was tapped on AR overlay layer
// END OF IMPORTANT FOR THIS EXAMPLE:
                } else {
                    ExampleUtils.showLicenseExpiredToastAndExit(this@AR_SelectScanActivity)
                }
                false
            }, object : IBarcodeScannerViewCallback {
                override fun onCameraOpen() {
                }

                override fun onPictureTaken(image: ByteArray, captureInfo: CaptureInfo) {
                    // we don't need full size pictures in this example
                }

                // IMPORTANT FOR THIS EXAMPLE:
                override fun onSelectionOverlayBarcodeClicked(barcodeItem: BarcodeItem) {
                    // We need to add the barcode items to the adapter on the main thread
                    barcodeScannerView.post {
                        resultAdapter.addBarcodeItems(listOf(barcodeItem))
                        resultView.scrollToPosition(0)
                    }
// END OF IMPORTANT FOR THIS EXAMPLE:
                }
            })
        }

// IMPORTANT FOR THIS EXAMPLE:
        // Disable the finder view to hide the barcode scanner viewfinder
        // It allows to locate the barcodes on the full screen
        barcodeScannerView.finderViewController.setFinderEnabled(false)

        // Enable the selection overlay (AR Overlay) to show the contours of detected barcodes
        barcodeScannerView.selectionOverlayController.setEnabled(true)

        // Set the colors for the AR overlay
        val notHighlightedColor = ContextCompat.getColor(this, R.color.ar_overlay_not_highlighted)

        // Set the colors for AR overlay for highlighted barcodes
        val highlightedColor = ContextCompat.getColor(this, R.color.ar_overlay_highlighted)

        barcodeScannerView.selectionOverlayController.setBarcodeAppearanceDelegate(object :
            BarcodePolygonsView.BarcodeAppearanceDelegate {
            override fun getPolygonStyle(
                defaultStyle: BarcodePolygonsView.BarcodePolygonStyle,
                barcodeItem: BarcodeItem
            ): BarcodePolygonsView.BarcodePolygonStyle {
                return defaultStyle.copy(
                    strokeHighlightedColor = highlightedColor,
                    strokeColor = notHighlightedColor,
                )
            }

            override fun getTextViewStyle(
                defaultStyle: BarcodePolygonsView.BarcodeTextViewStyle,
                barcodeItem: BarcodeItem
            ): BarcodePolygonsView.BarcodeTextViewStyle {
                return defaultStyle.copy(
                    // Show text with barcode type
                    textFormat = BarcodeOverlayTextFormat.CODE,
                    textContainerHighlightedColor = highlightedColor,
                    textContainerColor = notHighlightedColor,
                )
            }
        })

        // Required for the AR overlay to work faster
        barcodeScannerView.viewController.barcodeScanningInterval = 0

        // Set the delegate to highlight the barcodes that were already added to the list
        barcodeScannerView.selectionOverlayController.setBarcodeHighlightedDelegate(
            object : BarcodePolygonsView.BarcodeHighlightDelegate {
                override fun shouldHighlight(barcodeItem: BarcodeItem): Boolean {
                    // We highlight only the barcodes that were already added to the list
                    return resultAdapter.getItems()
                        .any {
                            it.textWithExtension == barcodeItem.textWithExtension
                                    && it.format == barcodeItem.format
                        }
                }
            })
        // @EndTag("AR-SelectScan")

        resultView = findViewById(R.id.barcode_recycler_view)
        resultView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        resultView.adapter = resultAdapter
// END OF IMPORTANT FOR THIS EXAMPLE:
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
