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
import io.scanbot.example.sdk.barcode.R
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
// @Tag("Add imports for RTU UI v2 activity")
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDKInitializer
import io.scanbot.sdk.common.AspectRatio
import io.scanbot.sdk.ui_v2.barcode.BarcodeScannerActivity
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerScreenConfiguration
import io.scanbot.sdk.ui_v2.common.FinderStyle
import io.scanbot.sdk.ui_v2.common.ScanbotColor
import io.scanbot.sdk.ui_v2.common.activity.registerForActivityResultOk

// @EndTag("Add imports for RTU UI v2 activity")

class ViewFinderSnippet : AppCompatActivity() {

    // Adapt the 'onCreate' method in your Activity (for example, MainActivity.kt):
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doc_snippet_activity_rtu_barcode_scanner_start)
        // @Tag("Init Scanbot Barcode Scanner SDK")
        // Initialize the SDK here:
        ScanbotBarcodeScannerSDKInitializer()
            // optional: uncomment the next line if you have a license key
            // .license(this.application, LICENSE_KEY)
            .initialize(this.application)
        // @EndTag("Init Scanbot Barcode Scanner SDK")

        // @Tag("Register RTU UI v2 activity result launcher")
        // The call to BarcodeScannerActivity.ResultContract() must be done after the SDK initialization
        val barcodeScreenLauncher: ActivityResultLauncher<BarcodeScannerScreenConfiguration> =
            registerForActivityResultOk(BarcodeScannerActivity.ResultContract()) { resultEntity ->
                // Barcode Scanner result callback:
                // Get the first scanned barcode from the result object...
                val barcodeItem = resultEntity.result?.items?.first()
                // ... and process the result as needed, for example, display as a Toast:
                Toast.makeText(
                    this,
                    "Scanned: ${barcodeItem?.barcode?.text} (${barcodeItem?.barcode?.format})",
                    Toast.LENGTH_LONG
                ).show()
            }
        // @EndTag("Register RTU UI v2 activity result launcher")

        findViewById<AppCompatButton>(R.id.start_barcode_rtu_button).setOnClickListener {
            // @Tag("Launch RTU UI v2 activity")
            // Launch the barcode scanner:
            val config = BarcodeScannerScreenConfiguration().apply {
                viewFinder.visible = true // Show the view finder
                viewFinder.aspectRatio = AspectRatio(16.0, 9.0) // Set the aspect ratio of the view finder
                viewFinder.style = FinderStyle.finderCorneredStyle().apply {
                    strokeColor = ScanbotColor("#00FF00") // Set the color of the view finder corners
                    strokeWidth = 10.0 // Set the width of the view finder corners in dp
                }
            }
            barcodeScreenLauncher.launch(config)
            // @EndTag("Launch RTU UI v2 activity")
        }
    }
}
