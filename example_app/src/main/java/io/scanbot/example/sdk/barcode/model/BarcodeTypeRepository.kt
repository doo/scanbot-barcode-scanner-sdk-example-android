package io.scanbot.example.sdk.barcode.model

import io.scanbot.sdk.barcode.entity.BarcodeFormat

object BarcodeTypeRepository {

    val selectedTypes = mutableSetOf<BarcodeFormat>().also {
        it.addAll(BarcodeFormat.ALL_CODES)
    }

    fun selectType(type: BarcodeFormat) {
        selectedTypes.add(type)
    }

    fun deselectType(type: BarcodeFormat) {
        selectedTypes.remove(type)
    }
}
