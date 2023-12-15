package io.scanbot.example.sdk.barcode.model

import io.scanbot.sdk.barcode.entity.BarcodeItem
import io.scanbot.sdk.barcode.entity.BarcodeScanningResult

data class BarcodeResultBundle(
    val barcodeScanningResult: BarcodeScanningResult,
    val imagePath: String? = null,
    val previewPath: String? = null
)

object BarcodeResultRepository {
    var barcodeResultBundle: BarcodeResultBundle? = null

    var selectedBarcodeItem: BarcodeItem? = null
}
