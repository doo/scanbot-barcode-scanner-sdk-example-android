package io.scanbot.example.sdk.barcode.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import io.scanbot.example.sdk.barcode.*
import io.scanbot.example.sdk.barcode.databinding.ActivityMainBinding
import io.scanbot.example.sdk.barcode.model.BarcodeResultBundle
import io.scanbot.example.sdk.barcode.model.BarcodeResultRepository
import io.scanbot.example.sdk.barcode.model.BarcodeTypeRepository
import io.scanbot.example.sdk.barcode.model.BarcodeV2ResultBundle
import io.scanbot.example.sdk.barcode.model.BarcodeV2ResultRepository
import io.scanbot.example.sdk.barcode.ui.dialog.ErrorFragment
import io.scanbot.sap.Status
import io.scanbot.sdk.barcode.ScanbotBarcodeDetector
import io.scanbot.sdk.barcode.entity.BarcodeFormat
import io.scanbot.sdk.barcode.entity.BarcodeScanningResult
import io.scanbot.sdk.barcode.ui.BarcodeOverlayTextFormat
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.ui.barcode_scanner.view.barcode.batch.BatchBarcodeScannerActivity
import io.scanbot.sdk.ui.registerForActivityResultOk as registerForActivityResultOkV1
import io.scanbot.sdk.ui_v2.common.activity.registerForActivityResultOk
import io.scanbot.sdk.ui.view.barcode.SelectionOverlayConfiguration
import io.scanbot.sdk.ui.view.barcode.batch.configuration.BatchBarcodeScannerConfiguration
import io.scanbot.sdk.ui.view.barcode.configuration.BarcodeImageGenerationType
import io.scanbot.sdk.ui.view.barcode.configuration.BarcodeScannerAdditionalConfiguration
import io.scanbot.sdk.ui.view.base.configuration.CameraOrientationMode
import io.scanbot.sdk.ui_v2.barcode.BarcodeScannerActivity
import io.scanbot.sdk.ui_v2.barcode.common.mappers.toV2
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerConfiguration
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var barcodeDetector: ScanbotBarcodeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        barcodeDetector = ScanbotBarcodeScannerSDK(this).createBarcodeDetector()

        binding.warningView.isVisible =
            ScanbotBarcodeScannerSDK(this).licenseInfo.status == Status.StatusTrial

        binding.qrDemo.setOnClickListener {
            val intent = Intent(applicationContext, QRScanCameraViewActivity::class.java)
            startActivity(intent)
        }

        binding.classicalArOverlayDemo.setOnClickListener {
            val intent = Intent(applicationContext, BarcodeScannerViewActivity::class.java)
            startActivity(intent)
        }

        binding.classicalScanCountDemo.setOnClickListener {
            val intent = Intent(applicationContext, BarcodeScanAndCountViewActivity::class.java)
            startActivity(intent)
        }

        binding.classicalBatch.setOnClickListener {
            val intent = Intent(applicationContext, BatchQRScanActivity::class.java)
            startActivity(intent)
        }

        binding.rtuUi.setOnClickListener {
            val barcodeCameraConfiguration = io.scanbot.sdk.ui.view.barcode.configuration.BarcodeScannerConfiguration()
            barcodeCameraConfiguration.setBarcodeFormatsFilter(arrayListOf<BarcodeFormat>().also {
                it.addAll(
                    BarcodeTypeRepository.selectedTypes
                )
            })
            barcodeCameraConfiguration.setAdditionalDetectionParameters(
                BarcodeScannerAdditionalConfiguration(
                    gs1HandlingMode = io.scanbot.sdk.barcode.entity.Gs1Handling.DECODE
                )
            )
            barcodeCameraConfiguration.setBarcodeImageGenerationType(BarcodeImageGenerationType.NONE)
            barcodeResultLauncher.launch(barcodeCameraConfiguration)
        }

        binding.rtuUiV2.setOnClickListener {
            val barcodeCameraConfiguration = BarcodeScannerConfiguration().apply {
                recognizerConfiguration.apply {
                    this.barcodeFormats = BarcodeTypeRepository.selectedTypes.map { it.toV2() }
                    this.gs1Handling = io.scanbot.sdk.ui_v2.barcode.configuration.Gs1Handling.DECODE
                }
            }
            barcodeV2ResultLauncher.launch(barcodeCameraConfiguration)
        }

        binding.rtuUiImage.setOnClickListener {
            val barcodeCameraConfiguration = io.scanbot.sdk.ui.view.barcode.configuration.BarcodeScannerConfiguration()
            barcodeCameraConfiguration.setBarcodeFormatsFilter(arrayListOf<BarcodeFormat>().also {
                it.addAll(
                    BarcodeTypeRepository.selectedTypes
                )
            })
            barcodeCameraConfiguration.setBarcodeImageGenerationType(BarcodeImageGenerationType.VIDEO_FRAME)
            barcodeResultLauncher.launch(barcodeCameraConfiguration)
        }

        binding.rtuUiSelectionOverlay.setOnClickListener {
            val barcodeCameraConfiguration = io.scanbot.sdk.ui.view.barcode.configuration.BarcodeScannerConfiguration()
            barcodeCameraConfiguration.setSelectionOverlayConfiguration(
                SelectionOverlayConfiguration(
                    overlayEnabled = true,
                    textFormat = BarcodeOverlayTextFormat.CODE_AND_TYPE // Select NONE to hide the value
                )
            )
            barcodeResultLauncher.launch(barcodeCameraConfiguration)
        }

        binding.rtuUiImport.setOnClickListener {
            // select an image from photo library and run document detection on it:
            val imageIntent = Intent()
            imageIntent.type = "image/*"
            imageIntent.action = Intent.ACTION_GET_CONTENT
            imageIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, false)
            imageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            val wrappedIntent = Intent.createChooser(imageIntent, getString(R.string.share_title))
            importImageResultLauncher.launch(wrappedIntent)
        }

        binding.rtuUiImportPdf.setOnClickListener {
            // select an image from photo library and run document detection on it:
            val imageIntent = Intent()
            imageIntent.type = "application/pdf"
            imageIntent.action = Intent.ACTION_GET_CONTENT
            imageIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, false)
            imageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            val wrappedIntent = Intent.createChooser(imageIntent, getString(R.string.share_title))
            importPdfResultLauncher.launch(wrappedIntent)
        }

        binding.rtuUiBatchMode.setOnClickListener {
            val barcodeCameraConfiguration = BatchBarcodeScannerConfiguration()

            barcodeCameraConfiguration.setTopBarButtonsColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.white
                )
            )
            barcodeCameraConfiguration.setTopBarBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.colorPrimaryDark
                )
            )
            barcodeCameraConfiguration.setFinderTextHint("Please align the QR-/Barcode in the frame above to scan it.")

            barcodeCameraConfiguration.setDetailsBackgroundColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.white
                )
            )
            barcodeCameraConfiguration.setDetailsActionColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.white
                )
            )
            barcodeCameraConfiguration.setDetailsBackgroundColor(
                ContextCompat.getColor(
                    this,
                    R.color.sheetColor
                )
            )
            barcodeCameraConfiguration.setDetailsPrimaryColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.white
                )
            )
            barcodeCameraConfiguration.setBarcodesCountTextColor(
                ContextCompat.getColor(
                    this,
                    android.R.color.white
                )
            )
            barcodeCameraConfiguration.setOrientationLockMode(CameraOrientationMode.PORTRAIT)
            barcodeCameraConfiguration.setBarcodeFormatsFilter(arrayListOf<BarcodeFormat>().also {
                it.addAll(
                    BarcodeTypeRepository.selectedTypes
                )
            })

            batchBarcodeResultLauncher.launch(barcodeCameraConfiguration)
        }

        binding.settings.setOnClickListener {
            val intent = Intent(this@MainActivity, BarcodeTypesActivity::class.java)
            startActivity(intent)
        }

        binding.classicalBarcodeDocumentParser.setOnClickListener {
            val intent = Intent(this@MainActivity, BarcodeDocumentParserDemoActivity::class.java)
            startActivity(intent)
        }
    }

    private val barcodeResultLauncher: ActivityResultLauncher<io.scanbot.sdk.ui.view.barcode.configuration.BarcodeScannerConfiguration> =
        registerForActivityResultOkV1(io.scanbot.sdk.ui.barcode_scanner.view.barcode.BarcodeScannerActivity.ResultContract()) { resultEntity ->
            val imagePath = resultEntity.barcodeImagePath
            val previewPath = resultEntity.barcodePreviewFramePath

            BarcodeResultRepository.barcodeResultBundle =
                BarcodeResultBundle(resultEntity.result!!, imagePath, previewPath)

            val intent = Intent(this, BarcodeResultActivity::class.java)
            startActivity(intent)
        }

    private val barcodeV2ResultLauncher: ActivityResultLauncher<BarcodeScannerConfiguration> =
        registerForActivityResultOk(BarcodeScannerActivity.ResultContract()) { resultEntity ->
            val imagePath = resultEntity.barcodeImagePath
            val previewPath = resultEntity.barcodePreviewFramePath

            BarcodeV2ResultRepository.barcodeResultBundle =
                BarcodeV2ResultBundle(resultEntity.result!!, imagePath, previewPath)

            val intent = Intent(this, BarcodeV2ResultActivity::class.java)
            startActivity(intent)
        }

    private val batchBarcodeResultLauncher: ActivityResultLauncher<BatchBarcodeScannerConfiguration> =
        registerForActivityResultOkV1(BatchBarcodeScannerActivity.ResultContract()) { resultEntity ->
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
                    processImageGalleryResult(activityResult.data!!)?.let { bitmap ->
                        barcodeDetector.modifyConfig { setBarcodeFormats(BarcodeTypeRepository.selectedTypes.toList()) }
                        val result = barcodeDetector.detectFromBitmap(bitmap, 0)

                        BarcodeResultRepository.barcodeResultBundle =
                            result?.let { BarcodeResultBundle(it, null, null) }

                        startActivity(Intent(this, BarcodeResultActivity::class.java))
                    }
                }
            }
        }

    private val importPdfResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val sdk = ScanbotBarcodeScannerSDK(this)
                if (!sdk.licenseInfo.isValid) {
                    showLicenseDialog()
                } else {
                    processPdfGalleryResult(activityResult.data!!)?.let { file ->
                        val outputDir = File(getExternalFilesDir(null), "images/")
                        if (!outputDir.exists()) outputDir.mkdir()
                        outputDir.listFiles()?.forEach { it.delete() }

                        val pdfImagesExtractor = sdk.createPdfImagesExtractor()
                        val images =
                            pdfImagesExtractor.imageUrlsFromPdf(file, outputDir, prefix = "image")

                        barcodeDetector.modifyConfig { setBarcodeFormats(BarcodeTypeRepository.selectedTypes.toList()) }
                        images.map { uri ->
                            val bitmap = BitmapFactory.decodeFile(uri.path)
                            val result = barcodeDetector.detectFromBitmap(bitmap, 0)
                            // set the last detected result as the final result
                            result?.barcodeItems ?: emptyList()
                        }.let {
                            BarcodeResultRepository.barcodeResultBundle =
                                BarcodeResultBundle(BarcodeScanningResult(it.flatten()), null, null)
                        }


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

    private fun processImageGalleryResult(data: Intent): Bitmap? {
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

    private fun processPdfGalleryResult(data: Intent): File? {
        val uri = data.data
        if (uri != null) {
            try {
                return contentResolver.openInputStream(uri).use { inputStream ->
                    val file = File.createTempFile("temp", ".pdf")
                    file.outputStream().use { outputStream ->
                        inputStream?.copyTo(outputStream)
                    }
                    file
                }
            } catch (e: IOException) {
            }
        }
        return null
    }
}
