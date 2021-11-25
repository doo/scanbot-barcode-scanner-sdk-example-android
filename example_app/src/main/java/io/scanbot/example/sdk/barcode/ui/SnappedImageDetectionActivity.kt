package io.scanbot.example.sdk.barcode.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import io.scanbot.example.sdk.barcode.BarcodeResultActivity
import io.scanbot.example.sdk.barcode.R
import io.scanbot.example.sdk.barcode.model.BarcodeResultBundle
import io.scanbot.example.sdk.barcode.model.BarcodeResultRepository
import io.scanbot.example.sdk.barcode.model.BarcodeTypeRepository
import io.scanbot.sdk.SdkLicenseError
import io.scanbot.sdk.barcode.BarcodeAutoSnappingController
import io.scanbot.sdk.barcode.BarcodeDetectorFrameHandler
import io.scanbot.sdk.barcode.ScanbotBarcodeDetector
import io.scanbot.sdk.barcode.entity.BarcodeScanningResult
import io.scanbot.sdk.barcode.entity.EngineMode
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.*

class SnappedImageDetectionActivity : AppCompatActivity(), BarcodeDetectorFrameHandler.ResultHandler {
    private lateinit var barcodeDetectorFrameHandler: BarcodeDetectorFrameHandler
    private lateinit var barcodeDetector: ScanbotBarcodeDetector
    private lateinit var cameraView: ScanbotCameraView

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snapped_image)

        cameraView = findViewById(R.id.camera)

        cameraView.setCameraOpenCallback {
            cameraView.postDelayed({
                cameraView.continuousFocus()
            }, 300)
        }

        findViewById<Button>(R.id.take_picture_button).setOnClickListener {
            cameraView.takePicture(false)
        }

        barcodeDetector = ScanbotBarcodeScannerSDK(this).createBarcodeDetector()
        barcodeDetectorFrameHandler = BarcodeDetectorFrameHandler.attach(
            cameraView,
            barcodeDetector
        )

        // Set to false if you want to disable live-detection
        barcodeDetectorFrameHandler.isEnabled = false

        barcodeDetectorFrameHandler.setDetectionInterval(1000)
        barcodeDetectorFrameHandler.addResultHandler(this)

        barcodeDetector.modifyConfig {
            setSaveCameraPreviewFrame(true)
            setBarcodeFormats(BarcodeTypeRepository.selectedTypes.toList())
        }

        cameraView.addPictureCallback(object : PictureCallback() {
            override fun onPictureTaken(image: ByteArray, captureInfo: CaptureInfo) {
                processPictureTaken(image, captureInfo.imageOrientation)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        cameraView.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Use onActivityResult to handle permission rejection
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CODE)
        }
    }

    override fun onPause() {
        super.onPause()
        cameraView.onPause()
    }

    private fun handleSuccess(result: BarcodeScanningResult) {
        BarcodeResultRepository.barcodeResultBundle = BarcodeResultBundle(result)
        val intent = Intent(this, BarcodeResultActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun processPictureTaken(image: ByteArray, imageOrientation: Int) {
        val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)

        val start = System.currentTimeMillis()
        val result = barcodeDetector.detectFromBitmap(bitmap, imageOrientation)

        Log.e("Scanbot_example", "Timer ${System.currentTimeMillis() - start}")

        val barcodeItems = result?.barcodeItems
        if (barcodeItems?.isNotEmpty() == true) {
            handleSuccess(result)
        } else {
            cameraView.continuousFocus()
            cameraView.startPreview()
            Toast.makeText(this, "No barcodes found", Toast.LENGTH_SHORT).show()
        }
    }


    override fun handle(result: FrameHandlerResult<BarcodeScanningResult?, SdkLicenseError>): Boolean {
        if (result is FrameHandlerResult.Success) {
            result.value?.let { handleSuccess(it) }
        } else {
            cameraView.post {
                Toast.makeText(this, "License has expired!", Toast.LENGTH_LONG).show()
            }
        }
        return false
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 200
    }
}
