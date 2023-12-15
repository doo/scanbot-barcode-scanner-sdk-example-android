package io.scanbot.example.sdk.barcode.model

import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeItem
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerResult

data class BarcodeV2ResultBundle(
    val barcodeScanningResult: BarcodeScannerResult,
    val imagePath: String? = null,
    val previewPath: String? = null
)

object BarcodeV2ResultRepository {
    var barcodeResultBundle: BarcodeV2ResultBundle? = null

    var selectedBarcodeItem: BarcodeItem? = null
}
