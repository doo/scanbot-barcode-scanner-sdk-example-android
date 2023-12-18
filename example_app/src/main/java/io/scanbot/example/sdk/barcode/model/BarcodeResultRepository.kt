package io.scanbot.example.sdk.barcode.model

import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeItem
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerResult

data class BarcodeResultBundle(
    val barcodeScanningResult: BarcodeScannerResult,
    val imagePath: String? = null,
    val previewPath: String? = null
)

object BarcodeResultRepository {
    var barcodeResultBundle: BarcodeResultBundle? = null

    var selectedBarcodeItem: BarcodeItem? = null
}
