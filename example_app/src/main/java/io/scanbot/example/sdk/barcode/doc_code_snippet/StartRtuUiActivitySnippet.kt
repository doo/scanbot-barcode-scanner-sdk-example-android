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

// @Tag("Start RTU UI Barcode snippet")
import android.os.Bundle
import io.scanbot.example.sdk.barcode.R
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDKInitializer
import io.scanbot.sdk.ui_v2.barcode.BarcodeScannerActivity
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerScreenConfiguration
import io.scanbot.sdk.ui_v2.common.activity.registerForActivityResultOk

class StartRtuUiActivitySnippetActivity : AppCompatActivity() {

    private lateinit var barcodeScreenLauncher: ActivityResultLauncher<BarcodeScannerScreenConfiguration>

    // Adapt the 'onCreate' method in your Activity (for example, MainActivity.kt):
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doc_snippet_activity_rtu_barcode_scanner_start)

        // Initialize the SDK here:
        ScanbotBarcodeScannerSDKInitializer()
            // optional: uncomment the next line if you have a license key
            // .license(this.application, LICENSE_KEY)
            .initialize(this.application)

        barcodeScreenLauncher =
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

        val config = BarcodeScannerScreenConfiguration().apply {
            // TODO: configure as needed
        }

        findViewById<AppCompatButton>(R.id.start_barcode_rtu_button).setOnClickListener {
            // Launch the barcode scanner:
            barcodeScreenLauncher.launch(config)
        }
    }
}
// @EndTag("Start RTU UI Barcode snippet")
