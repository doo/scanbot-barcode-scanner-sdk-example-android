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
import androidx.core.view.isVisible
import io.scanbot.example.sdk.barcode.R
import io.scanbot.example.sdk.barcode.databinding.ActivityMainBinding
import io.scanbot.example.sdk.barcode.model.BarcodeTypeRepository
import io.scanbot.example.sdk.barcode.model.BarcodeV2ResultBundle
import io.scanbot.example.sdk.barcode.model.BarcodeV2ResultRepository
import io.scanbot.example.sdk.barcode.model.toV2Results
import io.scanbot.example.sdk.barcode.ui.dialog.ErrorFragment
import io.scanbot.sap.Status
import io.scanbot.sdk.barcode.ScanbotBarcodeDetector
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.ui_v2.barcode.BarcodeScannerActivity
import io.scanbot.sdk.ui_v2.barcode.common.mappers.toV2
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerConfiguration
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerResult
import io.scanbot.sdk.ui_v2.barcode.configuration.MultipleBarcodesScanningMode
import io.scanbot.sdk.ui_v2.barcode.configuration.MultipleScanningMode
import io.scanbot.sdk.ui_v2.barcode.configuration.SingleScanningMode
import io.scanbot.sdk.ui_v2.common.activity.registerForActivityResultOk
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
            val barcodeCameraConfiguration = BarcodeScannerConfiguration().apply {
                this.recognizerConfiguration.apply {
                    this.barcodeFormats = BarcodeTypeRepository.selectedTypes.map { it.toV2() }
                    this.gs1Handling = io.scanbot.sdk.ui_v2.barcode.configuration.Gs1Handling.DECODE
                }
                this.useCase = SingleScanningMode().apply {
//                    this.confirmationSheetEnabled = false
                    // tweak other behaviour as needed
                }
            }
            barcodeV2ResultLauncher.launch(barcodeCameraConfiguration)
        }

        binding.rtuUiSelectionOverlay.setOnClickListener {
            val barcodeCameraConfiguration = BarcodeScannerConfiguration().apply {
                this.arOverlay.visible = true
            }
            // tweak other behaviour as needed
            barcodeV2ResultLauncher.launch(barcodeCameraConfiguration)
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
            val barcodeCameraConfiguration = BarcodeScannerConfiguration().apply {
                this.recognizerConfiguration.apply {
                    this.barcodeFormats = BarcodeTypeRepository.selectedTypes.map { it.toV2() }
                }
                this.useCase = MultipleScanningMode().apply {
                    this.mode = MultipleBarcodesScanningMode.COUNTING
                }
                // tweak other behaviour as needed
            }

            barcodeV2ResultLauncher.launch(barcodeCameraConfiguration)
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

    private val barcodeV2ResultLauncher: ActivityResultLauncher<BarcodeScannerConfiguration> =
        registerForActivityResultOk(BarcodeScannerActivity.ResultContract()) { resultEntity ->
            val imagePath = resultEntity.barcodeImagePath
            val previewPath = resultEntity.barcodePreviewFramePath

            BarcodeV2ResultRepository.barcodeResultBundle =
                BarcodeV2ResultBundle(resultEntity.result!!, imagePath, previewPath)

            val intent = Intent(this, BarcodeV2ResultActivity::class.java)
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

                        BarcodeV2ResultRepository.barcodeResultBundle =
                            result?.let { v1Result ->
                                val result = BarcodeScannerResult(
                                    v1Result.barcodeItems.toV2Results()
                                )
                                BarcodeV2ResultBundle(result, null, null) }

                        startActivity(Intent(this, BarcodeV2ResultActivity::class.java))
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
                            BarcodeV2ResultRepository.barcodeResultBundle =
                                BarcodeV2ResultBundle(BarcodeScannerResult(it.flatten().toV2Results()), null, null)
                        }

                        startActivity(Intent(this, BarcodeV2ResultActivity::class.java))
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
