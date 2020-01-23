package io.scanbot.example.sdk.barcode

import io.scanbot.sdk.barcode.entity.BarcodeScanningResult

data class BarcodeResultBundle(
    val barcodeScanningResult: BarcodeScanningResult,
    val imagePath: String? = null,
    val previewPath: String? = null
)