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

// @Tag("RTU UI v2 Sub Component Barcode scanner snippet")
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowCompat
import io.scanbot.common.onCancellation
import io.scanbot.common.onFailure
import io.scanbot.example.sdk.barcode.R
import io.scanbot.sdk.ui_v2.barcode.BarcodeScannerView
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerScreenConfiguration
import io.scanbot.sdk.ui_v2.common.StatusBarMode

class AlmostRtuUiBarcodeScannerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doc_snippet_activity_artu_barcode_scanner)

        // If you use "traditional" Android XML-driven UI - integrate ComposeView into your layout
        // and use code below to render our BarcodeScannerView in it.
        val cameraContainerView: ComposeView = findViewById(R.id.compose_container)
        cameraContainerView.apply {
            setContent {

                //In case if you already migrated to Compose UI - just use
                // the code below in your Composable function.
                val configuration = remember {
                    BarcodeScannerScreenConfiguration().apply {
                        // TODO: configure as needed
                    }
                }

                // This `LaunchedEffect` will allow view to react on
                // BarcodeScannerConfiguration's `statusBarMode` correctly.
                val statusBarHidden = configuration.topBar.statusBarMode == StatusBarMode.HIDDEN
                LaunchedEffect(key1 = true, block = {
                    if (statusBarHidden) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            window.attributes.layoutInDisplayCutoutMode =
                                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                        }

                        WindowCompat.setDecorFitsSystemWindows(window, false)
                    }
                })

                BarcodeScannerView(
                    configuration = configuration,
                    onBarcodeScanned = {
                        // TODO: present barcode result as needed.
                    },
                    onBarcodeScannerClosed = {
                        it.onCancellation {
                            // normal cancellation handling (e.g., user pressed back button)
                        }.onFailure {
                            //some internal code error happened and screen is closed
                        }
                        finish()
                    }
                )
            }
        }
    }
}
// @EndTag("RTU UI v2 Sub Component Barcode scanner snippet")
