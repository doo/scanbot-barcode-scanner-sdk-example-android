package io.scanbot.example.sdk.barcode.doc_code_snippet

/*
    NOTE: this snippet of code is to be used only as a part of the website documentation.
    This code is not intended for any use outside of the support of documentation by Scanbot SDK GmbH employees.
*/

// NOTE for maintainers: whenever changing this code,
// ensure that links using it are still pointing to valid lines!
// Pay attention to imports adding/removal/sorting!
// Page URLs using this code:
// TODO: add URLs here

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.scanbot.example.sdk.barcode.R
import io.scanbot.sdk.barcode.BarcodeFormats
import io.scanbot.sdk.barcode.BarcodeItem
import io.scanbot.sdk.barcode.ui.BarcodeScannerView
import io.scanbot.sdk.barcode.ui.IBarcodeScannerViewCallback
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.CaptureInfo
import io.scanbot.sdk.camera.FrameHandlerResult
import io.scanbot.sdk.ui.camera.CameraUiSettings

class BarcodeScannerClassicUiSnippetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doc_snippet_activity_classic_ui)

        val barcodeScannerView: BarcodeScannerView = findViewById(R.id.barcode_scanner_view)
        val barcodeScanner = ScanbotBarcodeScannerSDK(this).createBarcodeScanner()

        // modify config as needed
        barcodeScanner.setConfigurations(
            // set the supported barcode formats here
            barcodeFormats = BarcodeFormats.common
        )

        // both calls initCamera and initScanningBehavior are required
        barcodeScannerView.apply {
            initCamera(CameraUiSettings(false))
            initScanningBehavior(barcodeScanner,
                { result ->
                    if (result is FrameHandlerResult.Success) {
                        // process the scanned result here
                        // handleSuccess(result)
                    }
                    false
                },
                object : IBarcodeScannerViewCallback {
                    override fun onCameraOpen() {
                        // barcodeScannerView.viewController.useFlash(flashEnabled)
                    }
                    override fun onPictureTaken(image: ByteArray, captureInfo: CaptureInfo) {
                        // process the full size images taken by BarcodeAutoSnappingController here
                        // to enable auto snapping use the following command:
                        // barcodeScannerView.viewController.autoSnappingEnabled = true
                    }

                    override fun onSelectionOverlayBarcodeClicked(barcodeItem: BarcodeItem) {
                        // handle the barcode item selection here
                    }
                }
            )
        }
    }
}
