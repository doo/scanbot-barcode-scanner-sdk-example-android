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
import io.scanbot.example.sdk.barcode.model.BarcodeResultBundle
import io.scanbot.example.sdk.barcode.model.BarcodeResultRepository
import io.scanbot.example.sdk.barcode.model.BarcodeTypeRepository
import io.scanbot.example.sdk.barcode.ui.dialog.ErrorFragment
import io.scanbot.example.sdk.barcode.ui.util.applyEdgeToEdge
import io.scanbot.sap.Status
import io.scanbot.sdk.barcode.BarcodeFormats
import io.scanbot.sdk.barcode.BarcodeItem
import io.scanbot.sdk.barcode.BarcodeScanner
import io.scanbot.sdk.barcode.Gs1Handling
import io.scanbot.sdk.barcode.setBarcodeFormats
import io.scanbot.sdk.barcode.textWithExtension
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.ui_v2.barcode.BarcodeScannerActivity
import io.scanbot.sdk.ui_v2.barcode.common.mappers.BarcodeMappedDataExtension
import io.scanbot.sdk.ui_v2.barcode.common.mappers.getName
import io.scanbot.sdk.ui_v2.barcode.common.mappers.toV2
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeItemMapper
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeMappedData
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeMappingErrorCallback
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeMappingResultCallback
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerScreenConfiguration
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerUiResult
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeUseCase
import io.scanbot.sdk.ui_v2.barcode.configuration.CollapsedVisibleHeight
import io.scanbot.sdk.ui_v2.barcode.configuration.ExpectedBarcode
import io.scanbot.sdk.ui_v2.barcode.configuration.MultipleBarcodesScanningMode
import io.scanbot.sdk.ui_v2.barcode.configuration.MultipleScanningMode
import io.scanbot.sdk.ui_v2.barcode.configuration.SheetMode
import io.scanbot.sdk.ui_v2.barcode.configuration.SingleScanningMode
import io.scanbot.sdk.ui_v2.common.ScanbotColor
import io.scanbot.sdk.ui_v2.common.activity.registerForActivityResultOk
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var barcodeScanner: BarcodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyEdgeToEdge(this.findViewById(R.id.root_view))

        barcodeScanner = ScanbotBarcodeScannerSDK(this).createBarcodeScanner()

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
            val barcodeCameraConfiguration = BarcodeScannerScreenConfiguration().apply {
                this.scannerConfiguration.apply {
                    this.barcodeFormats = BarcodeTypeRepository.selectedTypes.toList()
                    this.gs1Handling = Gs1Handling.DECODE_FULL
                }
                this.useCase = SingleScanningMode().apply {
//                    this.confirmationSheetEnabled = false
                    // tweak other behaviour as needed
                }
            }
            barcodeResultLauncher.launch(barcodeCameraConfiguration)
        }

        binding.rtuUiSelectionOverlay.setOnClickListener {
            val barcodeCameraConfiguration = BarcodeScannerScreenConfiguration().apply {
               this.useCase = BarcodeUseCase.singleScanningMode().apply {
                   this.arOverlay.visible = true
               }
            }
            // tweak other behaviour as needed
            barcodeResultLauncher.launch(barcodeCameraConfiguration)
        }

        binding.rtuUiBatchMode.setOnClickListener {
            val barcodeCameraConfiguration = BarcodeScannerScreenConfiguration().apply {
                this.scannerConfiguration.apply {
                    this.barcodeFormats = BarcodeTypeRepository.selectedTypes.toList()
                }
                this.useCase = MultipleScanningMode().apply {
                    this.mode = MultipleBarcodesScanningMode.COUNTING
                }
                // tweak other behaviour as needed
            }

            barcodeResultLauncher.launch(barcodeCameraConfiguration)
        }

        binding.rtuUiMultipleUnique.setOnClickListener {
            val barcodeCameraConfiguration = BarcodeScannerScreenConfiguration().apply {
                class CustomBarcodeItemMapper : BarcodeItemMapper {

                    // NOTE: callback implementation class must be static (in case of Java)
                    // or non-inner (in case of Kotlin), have default (empty) constructor
                    // and must not touch fields or methods of enclosing class/method
                    override fun mapBarcodeItem(
                        barcodeItem: BarcodeItem,
                        onResult: BarcodeMappingResultCallback,
                        onError: BarcodeMappingErrorCallback
                    ) {
                        // TODO: use barcodeItem appropriately here as needed
                        onResult.onResult(
                            BarcodeMappedData(
                                title = barcodeItem.textWithExtension,
                                subtitle = barcodeItem.format?.getName() ?: "Unknown",
                                barcodeImage = BarcodeMappedDataExtension.barcodeFormatKey
                            )
                        )
                    }
                }

                this.useCase = MultipleScanningMode().apply {
                    this.mode = MultipleBarcodesScanningMode.UNIQUE
                    this.sheetContent.manualCountChangeEnabled = false
                    this.sheet.mode = SheetMode.COLLAPSED_SHEET
                    this.arOverlay.visible = true
                    this.arOverlay.automaticSelectionEnabled = false
                    this.barcodeInfoMapping.barcodeItemMapper = CustomBarcodeItemMapper()
                }

                this.userGuidance.title.text =
                    "Please align the QR-/Barcode in the frame above to scan it."


            }

            barcodeResultLauncher.launch(barcodeCameraConfiguration)
        }

        binding.rtuUiFindAndPick.setOnClickListener {
            val barcodeCameraConfiguration = BarcodeScannerScreenConfiguration().apply {

                this.useCase = BarcodeUseCase.findAndPickScanningMode().apply {

                    this.sheet.mode = SheetMode.COLLAPSED_SHEET
                    this.sheet.collapsedVisibleHeight = CollapsedVisibleHeight.LARGE
                    this.arOverlay.automaticSelectionEnabled = false

                    this.allowPartialScan = false

                    this.countingRepeatDelay = 1000

                    this.sheetContent.manualCountChangeEnabled = true
                    this.sheetContent.submitButton.text = "Submit"
                    this.sheetContent.submitButton.foreground.color = ScanbotColor("#000000")

                    // Configure other parameters, pertaining to findAndPick-scanning mode as needed.
                    // Set the expected barcodes.
                    expectedBarcodes = listOf(
                        ExpectedBarcode(
                            barcodeValue = "123456",
                            title = "numeric barcode",
                            image = "",
                            count = 4
                        ),
                        ExpectedBarcode(
                            barcodeValue = "SCANBOT",
                            title = "value barcode",
                            image = "",
                            count = 3
                        )
                    )
                }

                // Set an array of accepted barcode types.
                this.scannerConfiguration.barcodeFormats = BarcodeFormats.common

                this.userGuidance.title.text =
                    "Please align the QR-/Barcode in the frame above to scan it."

            }

            barcodeResultLauncher.launch(barcodeCameraConfiguration)
        }

        binding.rtuUiImport.setOnClickListener {
            // select an image from photo library and run barcode scanning on it:
            val imageIntent = Intent()
            imageIntent.type = "image/*"
            imageIntent.action = Intent.ACTION_GET_CONTENT
            imageIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, false)
            imageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            val wrappedIntent = Intent.createChooser(imageIntent, getString(R.string.share_title))
            importImageResultLauncher.launch(wrappedIntent)
        }

        binding.rtuUiImportPdf.setOnClickListener {
            // select an image from photo library and run barcode scanning on it:
            val imageIntent = Intent()
            imageIntent.type = "application/pdf"
            imageIntent.action = Intent.ACTION_GET_CONTENT
            imageIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, false)
            imageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            val wrappedIntent = Intent.createChooser(imageIntent, getString(R.string.share_title))
            importPdfResultLauncher.launch(wrappedIntent)
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

    private val barcodeResultLauncher: ActivityResultLauncher<BarcodeScannerScreenConfiguration> =
        registerForActivityResultOk(BarcodeScannerActivity.ResultContract()) { resultEntity ->

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
                        barcodeScanner.setConfiguration(barcodeScanner.copyCurrentConfiguration().apply {
                            setBarcodeFormats(BarcodeTypeRepository.selectedTypes.toList())
                        })
                        val result = barcodeScanner.scanFromBitmap(bitmap, 0)

                        BarcodeResultRepository.barcodeResultBundle =
                            result?.let { v1Result ->
                                val result = BarcodeScannerUiResult(
                                    v1Result.barcodes.map { it.toV2(1) }
                                )
                                BarcodeResultBundle(result, null, null) }

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

                        barcodeScanner.setConfiguration(barcodeScanner.copyCurrentConfiguration().apply {
                            setBarcodeFormats(BarcodeTypeRepository.selectedTypes.toList())
                        })
                        images.map { uri ->
                            val bitmap = BitmapFactory.decodeFile(uri.path)
                            val result = barcodeScanner.scanFromBitmap(bitmap, 0)
                            // set the last scanned result as the final result
                            result?.barcodes ?: emptyList()
                        }.let {
                            BarcodeResultRepository.barcodeResultBundle =
                                BarcodeResultBundle(BarcodeScannerUiResult(it.flatten().map { it.toV2(1) }), null, null)
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
