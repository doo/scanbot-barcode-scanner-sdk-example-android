package io.scanbot.example.sdk.barcode.model

import io.scanbot.sdk.barcode.BarcodeFormat
import io.scanbot.sdk.barcode.BarcodeFormats

object BarcodeTypeRepository {

    val selectedTypes = mutableSetOf<BarcodeFormat>().also {
        it.addAll(BarcodeFormats.all)
    }

    fun selectType(type: BarcodeFormat) {
        selectedTypes.add(type)
    }

    fun deselectType(type: BarcodeFormat) {
        selectedTypes.remove(type)
    }
}
