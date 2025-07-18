package io.scanbot.example.sdk.barcode.ui.usecases

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import io.scanbot.example.sdk.barcode.R
import io.scanbot.sdk.barcode.BarcodeItem
import io.scanbot.sdk.barcode.ui.BarcodeScanAndCountView
import io.scanbot.sdk.barcode.ui.IBarcodeScanCountViewCallback
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK

class AR_ScanAndCountActivity : AppCompatActivity() {
    // @Tag("AR-ScanAndCount")
    private lateinit var barcodeScanAndCountView: BarcodeScanAndCountView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scan_and_count)

        barcodeScanAndCountView = findViewById(R.id.barcode_scanner_view)

        val barcodeScanner = ScanbotBarcodeScannerSDK(this).createBarcodeScanner()
        barcodeScanner.setConfiguration(
            barcodeScanner.copyCurrentConfiguration().apply {
                // Specify the barcode format you want to scan
                // setBarcodeFormats(listOf(BarcodeFormat.QR_CODE))
            }
        )

        val scanAndCountButton = findViewById<Button>(R.id.button_scan_and_count)
        val continueScanningButton = findViewById<Button>(R.id.button_continue_scanning)

        barcodeScanAndCountView.apply {
            initCamera()
            initScanningBehavior(barcodeScanner, object : IBarcodeScanCountViewCallback {
                override fun onLicenseError() {
                    ExampleUtils.showLicenseExpiredToastAndExit(this@AR_ScanAndCountActivity)
                }

                override fun onScanAndCountFinished(barcodes: List<BarcodeItem>) {
// IMPORTANT FOR THIS EXAMPLE:
                    // Counted barcodes from all scans (Barcode -> Count)
                    val barcodesFromAllScans = barcodeScanAndCountView.countedBarcodes
                    Toast.makeText(context, "Found barcodes now: ${barcodes.size}\n" +
                            "Total different barcodes: ${barcodesFromAllScans.size}", Toast.LENGTH_LONG).show()
                    // You may process the calculated barcodes here
                    // For example, you may show them in a list
// END OF IMPORTANT FOR THIS EXAMPLE
                }

                override fun onScanAndCountStarted() {
                    continueScanningButton.isVisible = true
                    scanAndCountButton.isVisible = false
                }

                override fun onCameraOpen() {
                }
            })
        }
        scanAndCountButton.setOnClickListener {
// IMPORTANT FOR THIS EXAMPLE:
            barcodeScanAndCountView.viewController.scanAndCount()
// END OF IMPORTANT FOR THIS EXAMPLE
        }

        continueScanningButton.setOnClickListener {
// IMPORTANT FOR THIS EXAMPLE:
            barcodeScanAndCountView.viewController.continueScanning()
// END OF IMPORTANT FOR THIS EXAMPLE
            continueScanningButton.isVisible = false
            scanAndCountButton.isVisible = true
        }
        // @EndTag("AR-ScanAndCount")
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Use onActivityResult to handle permission rejection
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CODE)
        }
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 100
    }
}
