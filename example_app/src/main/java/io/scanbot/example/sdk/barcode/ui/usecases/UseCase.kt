package io.scanbot.example.sdk.barcode.ui.usecases

import android.app.Activity
import kotlin.reflect.KClass

enum class UseCase(val activityClass: KClass<out Activity>) {
    SINGLE_BARCODE(SingleBarcodeActivity::class),
    TINY_BARCODE(TinyBarcodeActivity::class),
    DISTANT_BARCODE(DistantBarcodeActivity::class),
    DETECTION_ON_THE_IMAGE(DetectionOnTheImageActivity::class),
    MULTIPLE_BARCODE(MultipleBarcodeActivity::class),
    BATCH_SCANNING(BatchScanningActivity::class),
    AR_MULTI_SCAN(AR_MultiScanActivity::class),
    AR_SELECT_SCAN(AR_SelectScanActivity::class),
    AR_FIND_AND_PICK(AR_FindAndPickActivity::class),
    AR_SCAN_AND_COUNT(AR_ScanAndCountActivity::class),
}


sealed class ViewType(val type: Int) {
    class Header(val title: String) : ViewType(0)
    class Option(val useCase: UseCase, val title: String) : ViewType(1)
}