package io.scanbot.example.sdk.barcode.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import io.scanbot.example.sdk.barcode.R
import io.scanbot.example.sdk.barcode.model.BarcodeTypeRepository
import io.scanbot.sdk.barcode.entity.BarcodeItem
import io.scanbot.sdk.barcode.entity.BarcodeScanningResult
import io.scanbot.sdk.barcode.ui.BarcodePolygonsView
import io.scanbot.sdk.barcode.ui.BarcodeScannerView
import io.scanbot.sdk.barcode.ui.IBarcodeScannerViewCallback
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.CaptureInfo
import io.scanbot.sdk.camera.FrameHandlerResult
import io.scanbot.sdk.ui.camera.CameraUiSettings

class BarcodeScannerViewActivity : AppCompatActivity() {
    private lateinit var barcodeScannerView: BarcodeScannerView
    private lateinit var resultView: ImageView

    private val resultsMap = hashMapOf<String, Long>()

    private var flashEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_scanner_view)

        barcodeScannerView = findViewById(R.id.barcode_scanner_view)
        resultView = findViewById(R.id.result)

        val barcodeDetector = ScanbotBarcodeScannerSDK(this).createBarcodeDetector()
        barcodeDetector.modifyConfig {
            setBarcodeFormats(BarcodeTypeRepository.selectedTypes.toList())
            setSaveCameraPreviewFrame(false)
        }

        barcodeScannerView.apply {
            initCamera(CameraUiSettings(true))
            initDetectionBehavior(barcodeDetector,
                { result ->
                    if (result is FrameHandlerResult.Success) {
                        handleSuccess(result)
                    } else {
                        barcodeScannerView.post {
                            Toast.makeText(
                                this@BarcodeScannerViewActivity,
                                "License has expired!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                    false
                },
                object : IBarcodeScannerViewCallback {
                    override fun onCameraOpen() {
                        barcodeScannerView.viewController.useFlash(flashEnabled)
                    }

                    override fun onPictureTaken(image: ByteArray, captureInfo: CaptureInfo) {
                        // we don't need full size pictures in this example
                    }
                }
            )
        }

        // Setting the Selection Overlay (AR)
        barcodeScannerView.selectionOverlayController.setEnabled(true)
        barcodeScannerView.viewController.apply {
            // This is important for Selection Overlay to work properly
            barcodeDetectionInterval = 0
            autoSnappingEnabled = false
        }
        barcodeScannerView.selectionOverlayController.setBarcodeAppearanceDelegate(object :
            BarcodePolygonsView.BarcodeAppearanceDelegate {
            override fun getPolygonStyle(
                defaultStyle: BarcodePolygonsView.BarcodePolygonStyle,
                barcodeItem: BarcodeItem
            ): BarcodePolygonsView.BarcodePolygonStyle {
                return defaultStyle.copy(strokeColor = Color.CYAN)
            }

            override fun getTextViewStyle(
                defaultStyle: BarcodePolygonsView.BarcodeTextViewStyle,
                barcodeItem: BarcodeItem
            ): BarcodePolygonsView.BarcodeTextViewStyle {
                return defaultStyle.copy(textColor = Color.BLACK)
            }

        })
        barcodeScannerView.selectionOverlayController.setBarcodeItemViewFactory(object :
            BarcodePolygonsView.BarcodeItemViewFactory {
            override fun createView(): View {
                val inflater = LayoutInflater.from(this@BarcodeScannerViewActivity)
                return inflater.inflate(R.layout.custom_view_for_ar, barcodeScannerView, false)
            }
        })
        barcodeScannerView.selectionOverlayController.setBarcodeItemViewBinder(object :
            BarcodePolygonsView.BarcodeItemViewBinder {
            override fun bindView(view: View, barcodeItem: BarcodeItem, shouldHighlight: Boolean) {
                val textWithExtension = barcodeItem.textWithExtension
                val progressView = view.findViewById<ProgressBar>(R.id.custom_ar_view_progress)

                if (!resultsMap.containsKey(textWithExtension)) {
                    // TODO: here we emulate loading info from the database/internet
                    resultsMap[textWithExtension] = System.currentTimeMillis() + 2500
                }
                val valueTextView = view.findViewById<TextView>(R.id.custom_ar_view_value)
                val resultIsReady = resultsMap[textWithExtension]!! < System.currentTimeMillis()
                progressView.isVisible = !resultIsReady
                valueTextView.isVisible = resultIsReady
                valueTextView.text = textWithExtension
            }
        })
    }

    override fun onResume() {
        super.onResume()
        barcodeScannerView.viewController.onResume()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Use onActivityResult to handle permission rejection
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION_CODE
            )
        }
    }

    override fun onPause() {
        super.onPause()
        barcodeScannerView.viewController.onPause()
    }

    private fun handleSuccess(result: FrameHandlerResult.Success<BarcodeScanningResult?>) {
        result.value?.let {
            // TODO: uncomment if you wish to proceed to the result screen automatically
            // BarcodeResultRepository.barcodeResultBundle = BarcodeResultBundle(it)
            // val intent = Intent(this, BarcodeResultActivity::class.java)
            // startActivity(intent)
            // finish()
        }
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 200
    }
}
