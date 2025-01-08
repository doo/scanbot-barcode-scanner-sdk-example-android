package io.scanbot.example.sdk.barcode.model

import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerUiItem
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerUiResult


data class BarcodeResultBundle(
    val barcodeScanningResult: BarcodeScannerUiResult,
    val imagePath: String? = null,
    val previewPath: String? = null
)

object BarcodeResultRepository {
    var barcodeResultBundle: BarcodeResultBundle? = null

    var selectedBarcodeItem: BarcodeScannerUiItem? = null
}
