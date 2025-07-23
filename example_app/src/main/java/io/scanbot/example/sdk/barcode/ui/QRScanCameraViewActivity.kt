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
import io.scanbot.example.sdk.barcode.model.BarcodeResultBundle
import io.scanbot.example.sdk.barcode.model.BarcodeResultRepository
import io.scanbot.example.sdk.barcode.model.BarcodeTypeRepository
import io.scanbot.example.sdk.barcode.ui.util.applyEdgeToEdge
import io.scanbot.sdk.SdkLicenseError
import io.scanbot.sdk.barcode.BarcodeAutoSnappingController
import io.scanbot.sdk.barcode.BarcodeScannerFrameHandler
import io.scanbot.sdk.barcode.BarcodeScannerResult
import io.scanbot.sdk.barcode.setBarcodeFormats
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.CaptureInfo
import io.scanbot.sdk.camera.FrameHandlerResult
import io.scanbot.sdk.camera.PictureCallback
import io.scanbot.sdk.camera.ScanbotCameraView
import io.scanbot.sdk.ui.camera.ScanbotCameraXView
import io.scanbot.sdk.ui_v2.barcode.common.mappers.toV2
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerUiResult

class QRScanCameraViewActivity : AppCompatActivity(), BarcodeScannerFrameHandler.ResultHandler {

    private lateinit var cameraView: ScanbotCameraXView
    private lateinit var resultView: ImageView
    private lateinit var flash: View

    private var flashEnabled = false
    private var barcodeScannerFrameHandler: BarcodeScannerFrameHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_camera_view)

        applyEdgeToEdge(this.findViewById(R.id.root_view))

        cameraView = findViewById(R.id.camera)
        resultView = findViewById(R.id.result)
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

        val barcodeScanner = ScanbotBarcodeScannerSDK(this).createBarcodeScanner()

        barcodeScannerFrameHandler = BarcodeScannerFrameHandler.attach(
            cameraView,
            barcodeScanner
        )

        barcodeScannerFrameHandler?.setScanningInterval(1000)
        barcodeScannerFrameHandler?.addResultHandler(this)

        barcodeScanner.setConfiguration(barcodeScanner.copyCurrentConfiguration().apply {
            setBarcodeFormats(BarcodeTypeRepository.selectedTypes.toList())
        })

        val barcodeAutoSnappingController =
            BarcodeAutoSnappingController.attach(cameraView, barcodeScannerFrameHandler!!)
        barcodeAutoSnappingController.setSensitivity(1f)
        cameraView.addPictureCallback(object : PictureCallback() {
            override fun onPictureTaken(image: ByteArray, captureInfo: CaptureInfo) {
                processPictureTaken(image, captureInfo.imageOrientation)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Use onActivityResult to handle permission rejection
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CODE)
        }
    }

    private fun handleSuccess(result: FrameHandlerResult.Success<BarcodeScannerResult?>) {
        result.value?.let {
            BarcodeResultRepository.barcodeResultBundle = BarcodeResultBundle(
                BarcodeScannerUiResult(items = it.barcodes.map { it.toV2(1) }),
                imagePath = null,
                previewPath = null,
            )
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

    override fun handle(result: FrameHandlerResult<BarcodeScannerResult?, SdkLicenseError>): Boolean {
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
