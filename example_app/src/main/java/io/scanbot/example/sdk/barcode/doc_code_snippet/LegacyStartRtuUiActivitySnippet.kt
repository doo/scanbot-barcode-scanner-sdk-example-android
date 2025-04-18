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

// @Tag("Start RTU UI Barcode snippet (deprecated Android API)")
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import io.scanbot.example.sdk.barcode.R
import io.scanbot.sdk.ui_v2.barcode.BarcodeScannerActivity
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerScreenConfiguration

/** Deprecated approach, use ActivityResultLauncher instead - see [AlmostRtuUiBarcodeScannerActivity] */
class LegacyStartRtuUiActivitySnippet : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doc_snippet_activity_rtu_barcode_scanner_start)

        val config = BarcodeScannerScreenConfiguration().apply {
            // TODO: configure as needed
        }

        findViewById<AppCompatButton>(R.id.start_barcode_rtu_button).setOnClickListener {
            val intent = BarcodeScannerActivity.newIntent(this, config)
            startActivityForResult(intent, BARCODE_SCANNER_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != BARCODE_SCANNER_REQUEST_CODE) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }

        when (resultCode) {
            RESULT_OK -> {
                val resultEntity = BarcodeScannerActivity.extractResult(resultCode, data)

                // TODO: present barcode result as needed
            }
        }
    }

    private companion object {
        const val BARCODE_SCANNER_REQUEST_CODE = 42
    }
}
// @EndTag("Start RTU UI Barcode snippet (deprecated Android API)")
