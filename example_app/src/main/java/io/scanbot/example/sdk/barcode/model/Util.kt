package io.scanbot.example.sdk.barcode.model

import io.scanbot.sdk.barcode.entity.BarcodeItem
import io.scanbot.sdk.ui_v2.barcode.common.mappers.toV2


fun Collection<BarcodeItem>.toV2Results(): List<io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeItem> {
    return this.map { item ->
        val count =
            this.count { it.barcodeFormat == item.barcodeFormat && it.text == item.text }
        item.toV2(count)
    }
}
