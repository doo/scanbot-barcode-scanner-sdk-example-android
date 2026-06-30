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
import io.scanbot.common.onSuccess
import io.scanbot.example.sdk.barcode.R
import io.scanbot.sdk.barcode.BarcodeFormats
import io.scanbot.sdk.barcode.BarcodeItem
import io.scanbot.sdk.barcode.setBarcodeFormats
import io.scanbot.sdk.barcode.ui.BarcodeScannerView
import io.scanbot.sdk.barcode.ui.IBarcodeScannerViewCallback
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.CameraModule
import io.scanbot.sdk.camera.CaptureInfo
import io.scanbot.sdk.geometry.AspectRatio
import io.scanbot.sdk.image.ImageRef

class BarcodeScannerCustomUiSnippetActivity : AppCompatActivity() {

    private lateinit var barcodeScannerView: BarcodeScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doc_snippet_activity_custom_ui)

        // @Tag("Barcode Custom UI view snippet")
        barcodeScannerView = findViewById<BarcodeScannerView>(R.id.barcode_scanner_view)!!
        val barcodeScanner = ScanbotBarcodeScannerSDK(this).createBarcodeScanner().getOrThrow()

        // modify config as needed
        barcodeScanner.setConfiguration(
            barcodeScanner.copyCurrentConfiguration().apply {
                // set the supported barcode formats here
                setBarcodeFormats(
                    barcodeFormats = BarcodeFormats.common
                )
            }
        )

        // both calls initCamera and initScanningBehavior are required
        barcodeScannerView.apply {
            initCamera()
            initScanningBehavior(barcodeScanner,
                { result, frame ->
                    result.onSuccess {
                        // process the scanned result here
                        // handleSuccess(result)
                    }
                    false
                },
                object : IBarcodeScannerViewCallback {
                    override fun onPictureTaken(
                        image: ImageRef,
                        captureInfo: CaptureInfo
                    ) {
                        // process the full size images taken by BarcodeAutoSnappingController here
                        // to enable auto snapping use the following command:
                        // barcodeScannerView.viewController.autoSnappingEnabled = true
                    }

                    override fun onCameraOpen() {
                        // barcodeScannerView.viewController.useFlash(flashEnabled)
                    }

                    override fun onSelectionOverlayBarcodeClicked(barcodeItem: BarcodeItem) {
                        // handle the barcode item selection here
                    }
                }
            )
        }
        // @EndTag("Barcode Custom UI view snippet")

        // @Tag("Barcode Custom UI configure finder snippet")
        // To disable the finder view
        barcodeScannerView.finderViewController.setFinderEnabled(false)
        // To set the required aspect ratio
        barcodeScannerView.finderViewController.setRequiredAspectRatios(
            listOf(
                AspectRatio(
                    4.0,
                    1.0
                )
            )
        )
        // @EndTag("Barcode Custom UI configure finder snippet")

        // @Tag("Barcode Custom UI configure camera behaviour snippet")
        // To switch to the front camera
        barcodeScannerView.cameraConfiguration.setCameraModule(CameraModule.FRONT)
        // To call the take picture function of the Camera
        barcodeScannerView.viewController.takePicture(acquireFocus = false)
        // @EndTag("Barcode Custom UI configure camera behaviour snippet")
    }
}
