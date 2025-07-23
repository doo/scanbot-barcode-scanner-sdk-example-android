package io.scanbot.example.sdk.barcode.ui.usecases

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.scanbot.example.sdk.barcode.R
import io.scanbot.example.sdk.barcode.ui.usecases.adapter.BarcodeItemAdapter
import io.scanbot.example.sdk.barcode.ui.util.applyEdgeToEdge
import io.scanbot.sdk.barcode.BarcodeScanner
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// This activity demonstrates how to scan barcodes on an image
class DetectionOnTheImageActivity : AppCompatActivity() {
    private lateinit var scanbotBarcodeScannerSDK: ScanbotBarcodeScannerSDK

    private val resultAdapter by lazy { BarcodeItemAdapter() }
    private lateinit var resultView: RecyclerView

    private lateinit var galleryImageLauncher: ActivityResultLauncher<Unit>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detection_on_the_image)
        applyEdgeToEdge(this.findViewById(R.id.root_view))

        // @Tag("Detecting barcodes on still images")
        scanbotBarcodeScannerSDK = ScanbotBarcodeScannerSDK(this)

        // Create a barcode scanner instance
        val barcodeScanner = scanbotBarcodeScannerSDK.createBarcodeScanner()
        barcodeScanner.setConfiguration(
            barcodeScanner.copyCurrentConfiguration().apply {
                // Specify the barcode format you want to scan
                // setBarcodeFormats(listOf(BarcodeFormat.QR_CODE))
            }
        )

        findViewById<Button>(R.id.import_image_button).setOnClickListener {
            galleryImageLauncher.launch(Unit)
        }

        // Register a callback to receive the result of the image selection
        galleryImageLauncher = registerForActivityResult(ImportImageContract(this)) { resultEntity ->
            // Clear the previous results
            resultAdapter.setBarcodeItems(listOf())

            // Process the selected image on a background thread
            lifecycleScope.launch(Dispatchers.Default) {
                resultEntity?.let { bitmap ->
                    processImage(barcodeScanner, bitmap)
                }
            }
        }
        // @EndTag("Detecting barcodes on still images")

        resultView = findViewById(R.id.barcode_recycler_view)
        resultView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        resultView.adapter = resultAdapter
    }

    // @Tag("Import and process image")
    private fun processImage(
        barcodeScanner: BarcodeScanner,
        bitmap: Bitmap
    ) {
        if (!scanbotBarcodeScannerSDK.licenseInfo.isValid) {
            ExampleUtils.showLicenseExpiredToastAndExit(this@DetectionOnTheImageActivity)
            return
        }
        barcodeScanner.scanFromBitmap(bitmap, 0)?.let {
            runOnUiThread {
                resultAdapter.setBarcodeItems(it.barcodes)
            }
        } ?: runOnUiThread {
            Toast.makeText(this, "No barcodes detected", Toast.LENGTH_SHORT).show()
        }
    }

    // This class is used to select an image from the photo library
    class ImportImageContract(private val context: Context) : ActivityResultContract<Unit, Bitmap?>() {

        override fun createIntent(context: Context, input: Unit): Intent {
            // An image is selected from the photo library and document detection is run on it:
            val imageIntent = Intent()
            imageIntent.type = "image/*"
            imageIntent.action = Intent.ACTION_GET_CONTENT
            imageIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, false)
            imageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)

            return Intent.createChooser(imageIntent, "Select a picture")
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Bitmap? {
            return if (resultCode == Activity.RESULT_OK && intent != null) {
                return processGalleryResult(intent)
            } else {
                null
            }
        }

        // This method is called when the image selection is complete
        private fun processGalleryResult(data: Intent): Bitmap? {
            val imageUri = data.data
            return MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        }
    }
    // @EndTag("Import and process image")
}