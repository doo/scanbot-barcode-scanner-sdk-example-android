package io.scanbot.example.sdk.barcode.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import io.scanbot.example.sdk.barcode.R
import io.scanbot.example.sdk.barcode.model.BarcodeTypeRepository
import io.scanbot.example.sdk.barcode.model.BarcodeResultBundle
import io.scanbot.example.sdk.barcode.model.BarcodeResultRepository
import io.scanbot.example.sdk.barcode.model.toV2Results
import io.scanbot.sdk.SdkLicenseError
import io.scanbot.sdk.barcode.BarcodeAutoSnappingController
import io.scanbot.sdk.barcode.BarcodeDetectorFrameHandler
import io.scanbot.sdk.barcode.entity.BarcodeFormat
import io.scanbot.sdk.barcode.entity.BarcodeItem
import io.scanbot.sdk.barcode.entity.BarcodeScanningResult
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.*
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerResult

class QRScanCameraViewActivity : AppCompatActivity(), BarcodeDetectorFrameHandler.ResultHandler {

    private lateinit var cameraView: ScanbotCameraView
    private lateinit var resultView: ImageView
    private lateinit var flash: View

    private var flashEnabled = false
    private var barcodeDetectorFrameHandler: BarcodeDetectorFrameHandler? = null

    private val detectedBarcodes = mutableSetOf<BarcodeItem>()

    override fun onCreate(savedInstanceState: Bundle?) {

        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_camera_view)

        cameraView = findViewById(R.id.camera)
        resultView = findViewById(R.id.result_image)
        flash = findViewById(R.id.flash)
        flash.setOnClickListener {
            flashEnabled = !flashEnabled
            cameraView.useFlash(flashEnabled)
        }
        cameraView.setCameraOpenCallback {
            cameraView.postDelayed({
                cameraView.useFlash(flashEnabled)
                cameraView.continuousFocus()
            }, 300)
        }

        val barcodeDetector = ScanbotBarcodeScannerSDK(this).createBarcodeDetector()

        barcodeDetectorFrameHandler = BarcodeDetectorFrameHandler.attach(
            cameraView,
            barcodeDetector
        )

        barcodeDetectorFrameHandler?.setDetectionInterval(1)
        barcodeDetectorFrameHandler?.addResultHandler(this)

        barcodeDetector.modifyConfig {
            setSaveCameraPreviewFrame(true)
            // TODO Define/limit the barcode types/symbologies that the detector should recognize:
            setBarcodeFormats(arrayListOf(BarcodeFormat.DATA_MATRIX, BarcodeFormat.QR_CODE))
        }

        val barcodeAutoSnappingController =
            BarcodeAutoSnappingController.attach(cameraView, barcodeDetectorFrameHandler!!)
        barcodeAutoSnappingController.setSensitivity(1f)
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

    private fun handleSuccess(result: FrameHandlerResult.Success<BarcodeScanningResult?>) {
        result.value?.let {
            it.barcodeItems.forEach {
                // TODO Collect 4 unique(!) barcodes of each corner by using the values and/or resultPoints.
                // The barcodes could have been detected all at once or in several frame results.
                detectedBarcodes.add(it)
                //it.text => value
                //it.barcodeFormat => symbology
                //it.resultPoints => coordinates
            }

            // TODO Once you have the 4 barcodes, stop the barcode detector and take the picture (trigger image snapping):
            if (detectedBarcodes.size >= 4) {
                barcodeDetectorFrameHandler?.isEnabled = false
                cameraView.takePicture(false)
            }
        }
    }

    fun processPictureTaken(image: ByteArray, imageOrientation: Int) {
        val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)

        val matrix = Matrix()
        matrix.setRotate(imageOrientation.toFloat(), bitmap.width / 2f, bitmap.height / 2f)
        val resultBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)

        // TODO Crop the resultBitmap by using the coordinates from 4 detectedBarcodes
        // detectedBarcodes....

        resultView.post {
            resultView.setImageBitmap(resultBitmap)
            cameraView.continuousFocus()
            cameraView.startPreview()
        }
    }

    override fun handle(result: FrameHandlerResult<BarcodeScanningResult?, SdkLicenseError>): Boolean {
        if (result is FrameHandlerResult.Success) {
            handleSuccess(result)
        } else {
            cameraView.post {
                Toast.makeText(
                    this,
                    "License has expired!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        return false
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 200
    }
}
