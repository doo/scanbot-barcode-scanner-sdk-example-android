package io.scanbot.sdk.sdk_integration_barcode_scanner_sdk

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import io.scanbot.example.sdk.barcode.*
import io.scanbot.sdk.barcode.entity.BarcodeFormat
import io.scanbot.sdk.barcode.entity.BarcodeScanningResult
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.ui.barcode_scanner.view.barcode.BarcodeScannerActivity
import io.scanbot.sdk.ui.view.barcode.BaseBarcodeScannerActivity
import io.scanbot.sdk.ui.view.barcode.configuration.BarcodeImageGenerationType
import io.scanbot.sdk.ui.view.barcode.configuration.BarcodeScannerConfiguration
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val BARCODE_DEFAULT_UI_REQUEST_CODE = 910
    private val IMPORT_IMAGE_REQUEST_CODE = 911

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.qr_demo).setOnClickListener {
            val intent = Intent(applicationContext, QRScanCameraViewActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.rtu_ui).setOnClickListener {
            val barcodeCameraConfiguration = BarcodeScannerConfiguration()
            barcodeCameraConfiguration.setBarcodeFormatsFilter(arrayListOf<BarcodeFormat>().also {
                it.addAll(
                    BarcodeTypeRepository.selectedTypes
                )
            })
            /*
            barcodeCameraConfiguration.setTopBarBackgroundColor(Color.parseColor("#00FFFF"))
            barcodeCameraConfiguration.setTopBarButtonsColor(Color.parseColor("#FF0000"))

            barcodeCameraConfiguration.setCancelButtonTitle("закончить")
            barcodeCameraConfiguration.setFinderTextHint("Помести код сюда")

            barcodeCameraConfiguration.setCameraOverlayColor(Color.parseColor("#80F0F000"))
            barcodeCameraConfiguration.setFinderLineColor(Color.parseColor("#00F0F0"))
            barcodeCameraConfiguration.setFinderHeight(800)
            barcodeCameraConfiguration.setFinderWidth(800)
            barcodeCameraConfiguration.setFinderLineWidth(10)
            barcodeCameraConfiguration.setFinderTextHintColor(Color.parseColor("#000000"))
            barcodeCameraConfiguration.setEnableCameraExplanationText("Дай пермишн!")
            barcodeCameraConfiguration.setEnableCameraButtonTitle("На пермишн")

            barcodeCameraConfiguration.setBarcodeFormatsFilter(arrayListOf(BarcodeFormat.ALL_FORMATS))
            barcodeCameraConfiguration.setFlashEnabled(false)
            */
            barcodeCameraConfiguration.setBarcodeImageGenerationType(BarcodeImageGenerationType.NONE)
            val intent =
                BarcodeScannerActivity.newIntent(this@MainActivity, barcodeCameraConfiguration)
            startActivityForResult(intent, BARCODE_DEFAULT_UI_REQUEST_CODE)
        }
        findViewById<View>(R.id.rtu_ui_image).setOnClickListener {
            val barcodeCameraConfiguration = BarcodeScannerConfiguration()
            barcodeCameraConfiguration.setBarcodeFormatsFilter(arrayListOf<BarcodeFormat>().also {
                it.addAll(
                    BarcodeTypeRepository.selectedTypes
                )
            })
            /*
                barcodeCameraConfiguration.setTopBarBackgroundColor(Color.parseColor("#00FFFF"))
                barcodeCameraConfiguration.setTopBarButtonsColor(Color.parseColor("#FF0000"))

                barcodeCameraConfiguration.setCancelButtonTitle("закончить")
                barcodeCameraConfiguration.setFinderTextHint("Помести код сюда")
                barcodeCameraConfiguration.setCameraOverlayColor(Color.parseColor("#80F0F000"))
                barcodeCameraConfiguration.setFinderLineColor(Color.parseColor("#00F0F0"))
                barcodeCameraConfiguration.setFinderHeight(800)
                barcodeCameraConfiguration.setFinderWidth(800)
                barcodeCameraConfiguration.setFinderLineWidth(10)
                barcodeCameraConfiguration.setFinderTextHintColor(Color.parseColor("#000000"))
                barcodeCameraConfiguration.setEnableCameraExplanationText("Дай пермишн!")
                barcodeCameraConfiguration.setEnableCameraButtonTitle("На пермишн")

                barcodeCameraConfiguration.setBarcodeFormatsFilter(arrayListOf(BarcodeFormat.ALL_FORMATS))
                barcodeCameraConfiguration.setFlashEnabled(false)
                */

            barcodeCameraConfiguration.setBarcodeImageGenerationType(BarcodeImageGenerationType.VIDEO_FRAME)
            val intent =
                BarcodeScannerActivity.newIntent(this@MainActivity, barcodeCameraConfiguration)
            startActivityForResult(intent, BARCODE_DEFAULT_UI_REQUEST_CODE)
        }

        findViewById<View>(R.id.rtu_ui_import).setOnClickListener {
            // select an image from photo library and run document detection on it:
            val imageIntent = Intent()
            imageIntent.type = "image/*"
            imageIntent.action = Intent.ACTION_GET_CONTENT
            imageIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                imageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            }
            startActivityForResult(Intent.createChooser(imageIntent, getString(R.string.share_title)), IMPORT_IMAGE_REQUEST_CODE)
        }
        findViewById<View>(R.id.settings).setOnClickListener {
            val intent = Intent(this@MainActivity, BarcodeTypesActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == BARCODE_DEFAULT_UI_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.getParcelableExtra<BarcodeScanningResult>(BaseBarcodeScannerActivity.SCANNED_BARCODE_EXTRA)
                ?.let {
                    val imagePath =
                        data.getStringExtra(BaseBarcodeScannerActivity.SCANNED_BARCODE_IMAGE_PATH_EXTRA)
                    val previewPath =
                        data.getStringExtra(BaseBarcodeScannerActivity.SCANNED_BARCODE_PREVIEW_FRAME_PATH_EXTRA)

                    BarcodeResultRepository.barcodeResultBundle =
                        BarcodeResultBundle(it, imagePath, previewPath)

                    val intent = Intent(this, BarcodeResultActivity::class.java)
                    startActivity(intent)
                }
        } else if (requestCode == IMPORT_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val sdk = ScanbotBarcodeScannerSDK(this)
            if (!sdk.licenseInfo.isValid) {
                showLicenseDialog()
            } else {
                processGalleryResult(data!!)?.let { bitmap ->
                    val result = sdk.barcodeDetector().detectFromBitmap(bitmap, 0)

                    BarcodeResultRepository.barcodeResultBundle = result?.let { BarcodeResultBundle(it, null, null) }

                    startActivity(Intent(this, BarcodeResultActivity::class.java))
                }
            }
        }
    }


    private fun showLicenseDialog() {
        if (supportFragmentManager.findFragmentByTag(ErrorFragment.NAME) == null) {
            val dialogFragment = ErrorFragment.newInstance()
            dialogFragment.show(supportFragmentManager, ErrorFragment.NAME)
        }
    }

    private fun processGalleryResult(data: Intent): Bitmap? {
        val imageUri = data.data
        var bitmap: Bitmap? = null
        if (imageUri != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            } catch (e: IOException) {
            }
        }
        return bitmap
    }

}
