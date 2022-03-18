package io.scanbot.example.sdk.barcode

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import io.scanbot.example.sdk.barcode.model.BarcodeResultBundle
import io.scanbot.example.sdk.barcode.model.BarcodeResultRepository
import io.scanbot.example.sdk.barcode.model.BarcodeTypeRepository
import io.scanbot.sdk.SdkLicenseError
import io.scanbot.sdk.barcode.BarcodeAutoSnappingController
import io.scanbot.sdk.barcode.BarcodeDetectorFrameHandler
import io.scanbot.sdk.barcode.entity.BarcodeScanningResult
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.*
import io.scanbot.sdk.ui.camera.ScanbotCameraXView

class QRScanCameraViewActivity : AppCompatActivity(), BarcodeDetectorFrameHandler.ResultHandler {

    private lateinit var cameraView: ScanbotCameraXView
    private lateinit var resultView: ImageView
    private lateinit var flash: View

    private var flashEnabled = false
    private var cameraModule: CameraModule = CameraModule.BACK
    private var barcodeDetectorFrameHandler: BarcodeDetectorFrameHandler? = null


    override fun onCreate(savedInstanceState: Bundle?) {

        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_camera_view)

        cameraView = findViewById(R.id.camera)
        cameraView.setCameraModule(cameraModule)

        resultView = findViewById(R.id.result)

        flash = findViewById(R.id.flash)
        flash.setOnClickListener {
            flashEnabled = !flashEnabled
            cameraView.useFlash(flashEnabled)
        }

        findViewById<Button>(R.id.cam_switch).setOnClickListener {
            cameraModule = if (cameraModule == CameraModule.BACK)
                CameraModule.FRONT_MIRRORED
            else
                CameraModule.BACK

            cameraView.setCameraModule(cameraModule)
            cameraView.restartPreview()
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

        barcodeDetectorFrameHandler?.setDetectionInterval(1000)
        barcodeDetectorFrameHandler?.addResultHandler(this)

        barcodeDetector.modifyConfig {
            //setSaveCameraPreviewFrame(true)
            setBarcodeFormats(BarcodeTypeRepository.selectedTypes.toList())
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
        cameraView.startPreview()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Use onActivityResult to handle permission rejection
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CODE)
        }
    }

    override fun onPause() {
        super.onPause()
        cameraView.stopPreview()
    }


    private fun handleSuccess(result: FrameHandlerResult.Success<BarcodeScanningResult?>) {
        result.value?.let {
            BarcodeResultRepository.barcodeResultBundle = BarcodeResultBundle(it)
            val intent = Intent(this, BarcodeResultActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    fun processPictureTaken(image: ByteArray, imageOrientation: Int) {
        val bitmap = BitmapFactory.decodeByteArray(image, 0, image.size)

        val matrix = Matrix()
        matrix.setRotate(imageOrientation.toFloat(), bitmap.width / 2f, bitmap.height / 2f)
        val resultBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false)

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
