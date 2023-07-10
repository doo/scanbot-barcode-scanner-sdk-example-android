package io.scanbot.example.sdk.barcode.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import io.scanbot.example.sdk.barcode.*
import io.scanbot.example.sdk.barcode.model.BarcodeResultBundle
import io.scanbot.example.sdk.barcode.model.BarcodeResultRepository
import io.scanbot.example.sdk.barcode.model.BarcodeTypeRepository
import io.scanbot.example.sdk.barcode.ui.dialog.ErrorFragment
import io.scanbot.sap.Status
import io.scanbot.sdk.barcode.ScanbotBarcodeDetector
import io.scanbot.sdk.barcode.entity.BarcodeFormat
import io.scanbot.sdk.barcode.ui.BarcodeOverlayTextFormat
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.ui.barcode_scanner.view.barcode.BarcodeScannerActivity
import io.scanbot.sdk.ui.barcode_scanner.view.barcode.batch.BatchBarcodeScannerActivity
import io.scanbot.sdk.ui.registerForActivityResultOk
import io.scanbot.sdk.ui.view.barcode.SelectionOverlayConfiguration
import io.scanbot.sdk.ui.view.barcode.batch.configuration.BatchBarcodeScannerConfiguration
import io.scanbot.sdk.ui.view.barcode.configuration.BarcodeImageGenerationType
import io.scanbot.sdk.ui.view.barcode.configuration.BarcodeScannerConfiguration
import io.scanbot.sdk.ui.view.base.configuration.CameraOrientationMode
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var barcodeDetector: ScanbotBarcodeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        barcodeDetector = ScanbotBarcodeScannerSDK(this).createBarcodeDetector()

        warning_view.isVisible = ScanbotBarcodeScannerSDK(this).licenseInfo.status == Status.StatusTrial

        findViewById<View>(R.id.qr_demo).setOnClickListener {
            val intent = Intent(applicationContext, QRScanCameraViewActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.classical_ar_overlay_demo).setOnClickListener {
            val intent = Intent(applicationContext, BarcodeScannerViewActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.classical_scan_count_demo).setOnClickListener {
            val intent = Intent(applicationContext, BarcodeScanAndCountViewActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.classical_batch).setOnClickListener {
            val intent = Intent(applicationContext, BatchQRScanActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.rtu_ui).setOnClickListener {
            val barcodeCameraConfiguration = BarcodeScannerConfiguration()
            barcodeCameraConfiguration.setBarcodeFormatsFilter(arrayListOf<BarcodeFormat>().also {
                it.addAll(
                    BarcodeTypeRepository.selectedTypes
                )
            })
            barcodeCameraConfiguration.setBarcodeImageGenerationType(BarcodeImageGenerationType.NONE)
            barcodeResultLauncher.launch(barcodeCameraConfiguration)
        }

        findViewById<View>(R.id.rtu_ui_image).setOnClickListener {
            val barcodeCameraConfiguration = BarcodeScannerConfiguration()
            barcodeCameraConfiguration.setBarcodeFormatsFilter(arrayListOf<BarcodeFormat>().also {
                it.addAll(
                    BarcodeTypeRepository.selectedTypes
                )
            })
            barcodeCameraConfiguration.setBarcodeImageGenerationType(BarcodeImageGenerationType.VIDEO_FRAME)
            barcodeResultLauncher.launch(barcodeCameraConfiguration)
        }

        findViewById<View>(R.id.rtu_ui_selection_overlay).setOnClickListener {
            val barcodeCameraConfiguration = BarcodeScannerConfiguration()
            barcodeCameraConfiguration.setSelectionOverlayConfiguration(
                SelectionOverlayConfiguration(
                    overlayEnabled = true,
                    textFormat = BarcodeOverlayTextFormat.CODE_AND_TYPE // Select NONE to hide the value
                )
            )
            barcodeResultLauncher.launch(barcodeCameraConfiguration)
        }

        findViewById<View>(R.id.rtu_ui_import).setOnClickListener {
            // select an image from photo library and run document detection on it:
            val imageIntent = Intent()
            imageIntent.type = "image/*"
            imageIntent.action = Intent.ACTION_GET_CONTENT
            imageIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, false)
            imageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            val wrappedIntent = Intent.createChooser(imageIntent, getString(R.string.share_title))
            importImageResultLauncher.launch(wrappedIntent)
        }

        findViewById<View>(R.id.rtu_ui_batch_mode).setOnClickListener {
            val barcodeCameraConfiguration = BatchBarcodeScannerConfiguration()

            barcodeCameraConfiguration.setTopBarButtonsColor(ContextCompat.getColor(this, android.R.color.white))
            barcodeCameraConfiguration.setTopBarBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
            barcodeCameraConfiguration.setFinderTextHint("Please align the QR-/Barcode in the frame above to scan it.")

            barcodeCameraConfiguration.setDetailsBackgroundColor(ContextCompat.getColor(this, android.R.color.white))
            barcodeCameraConfiguration.setDetailsActionColor(ContextCompat.getColor(this, android.R.color.white))
            barcodeCameraConfiguration.setDetailsBackgroundColor(ContextCompat.getColor(this, R.color.sheetColor))
            barcodeCameraConfiguration.setDetailsPrimaryColor(ContextCompat.getColor(this, android.R.color.white))
            barcodeCameraConfiguration.setBarcodesCountTextColor(ContextCompat.getColor(this, android.R.color.white))
            barcodeCameraConfiguration.setOrientationLockMode(CameraOrientationMode.PORTRAIT)
            barcodeCameraConfiguration.setBarcodeFormatsFilter(arrayListOf<BarcodeFormat>().also {
                it.addAll(
                    BarcodeTypeRepository.selectedTypes
                )
            })

            val rtuInput = BatchBarcodeScannerActivity.InputParams(
                barcodeCameraConfiguration
            )
            batchBarcodeResultLauncher.launch(rtuInput)
        }

        findViewById<View>(R.id.settings).setOnClickListener {
            val intent = Intent(this@MainActivity, BarcodeTypesActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.classical_barcode_document_parser).setOnClickListener {
            val intent = Intent(this@MainActivity, BarcodeDocumentParserDemoActivity::class.java)
            startActivity(intent)
        }
    }

    private val barcodeResultLauncher: ActivityResultLauncher<BarcodeScannerConfiguration> =
        registerForActivityResultOk(BarcodeScannerActivity.ResultContract()) { resultEntity ->
            val imagePath = resultEntity.barcodeImagePath
            val previewPath = resultEntity.barcodePreviewFramePath

            BarcodeResultRepository.barcodeResultBundle =
                BarcodeResultBundle(resultEntity.result!!, imagePath, previewPath)

            val intent = Intent(this, BarcodeResultActivity::class.java)
            startActivity(intent)
        }

    private val batchBarcodeResultLauncher: ActivityResultLauncher<BatchBarcodeScannerActivity.InputParams> =
        registerForActivityResultOk(BatchBarcodeScannerActivity.ResultContract()) { resultEntity ->
            BarcodeResultRepository.barcodeResultBundle =
                BarcodeResultBundle(resultEntity.result!!)

            val intent = Intent(this, BarcodeResultActivity::class.java)
            startActivity(intent)
        }

    private val importImageResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val sdk = ScanbotBarcodeScannerSDK(this)
                if (!sdk.licenseInfo.isValid) {
                    showLicenseDialog()
                } else {
                    processGalleryResult(activityResult.data!!)?.let { bitmap ->
                        barcodeDetector.modifyConfig { setBarcodeFormats(BarcodeTypeRepository.selectedTypes.toList()) }
                        val result = barcodeDetector.detectFromBitmap(bitmap, 0)

                        BarcodeResultRepository.barcodeResultBundle =
                            result?.let { BarcodeResultBundle(it, null, null) }

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
