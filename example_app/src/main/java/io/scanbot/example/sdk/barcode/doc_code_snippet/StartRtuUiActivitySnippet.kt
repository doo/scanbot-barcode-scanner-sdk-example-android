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
import io.scanbot.common.onFailure
import io.scanbot.common.onSuccess
import io.scanbot.common.Result
import io.scanbot.common.onCancellation
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDKInitializer
import io.scanbot.sdk.ui_v2.barcode.BarcodeScannerActivity
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerScreenConfiguration

class StartRtuUiActivitySnippetActivity : AppCompatActivity() {

    // Adapt the 'onCreate' method in your Activity (for example, MainActivity.kt):
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doc_snippet_activity_rtu_barcode_scanner_start)

        // Initialize the SDK here:
        ScanbotBarcodeScannerSDKInitializer()
            // optional: uncomment the next line if you have a license key
            // .license(this.application, LICENSE_KEY)
            .initialize(this.application)

        // The call to BarcodeScannerActivity.ResultContract() must be done after the SDK initialization
        val barcodeScreenLauncher: ActivityResultLauncher<BarcodeScannerScreenConfiguration> =
            registerForActivityResult(BarcodeScannerActivity.ResultContract()) { resultEntity ->
                resultEntity.onSuccess { result ->
                    // Barcode Scanner result callback:
                    // Get the first scanned barcode from the result object...
                    val barcodeItem = result.items.first()
                    // ... and process the result as needed, for example, display as a Toast:
                    Toast.makeText(
                        this@StartRtuUiActivitySnippetActivity,
                        "Scanned: ${barcodeItem?.barcode?.text} (${barcodeItem?.barcode?.format})",
                        Toast.LENGTH_LONG
                    ).show()
                }.onCancellation {
                    // Indicates that the cancel button was tapped. Or screen is closed by other reason.
                }.onFailure {
                    // Optional activity closing cause handling to understand the reason scanner result is not provided
                    when (it) {
                        is Result.InvalidLicenseError -> {
                            // indicate that the Scanbot SDK license is invalid
                        }

                        else -> {
                            // Handle other errors
                        }
                    }
                }
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
