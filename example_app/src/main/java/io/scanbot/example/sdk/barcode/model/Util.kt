package io.scanbot.example.sdk.barcode.model

import io.scanbot.sdk.barcode.BarcodeItem
import io.scanbot.sdk.ui_v2.barcode.common.mappers.toV2


fun Collection<BarcodeItem>.toV2Results(): List<io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerUiItem> {
    return this.map { item ->
        val count =
            this.count {
                it.format == item.format && it.text == item.text }
        item.toV2(count)
    }
}
