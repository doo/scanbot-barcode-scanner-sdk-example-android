package io.scanbot.example.sdk.barcode.model

import io.scanbot.sdk.barcode.entity.BarcodeItem
import io.scanbot.sdk.ui_v2.barcode.common.mappers.toV2


fun Collection<BarcodeItem>.toV2Results(): List<io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerUiItem> {
    return this.map { item ->
        val count =
            this.count { it.barcodeItem.format == item.barcodeItem.format && it.barcodeItem.text == item.barcodeItem.text }
        item.barcodeItem.toV2(count)
    }
}
