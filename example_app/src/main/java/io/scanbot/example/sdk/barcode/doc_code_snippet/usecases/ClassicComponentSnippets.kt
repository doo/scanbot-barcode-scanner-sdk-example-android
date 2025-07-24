package io.scanbot.example.sdk.barcode.doc_code_snippet.usecases

import io.scanbot.sdk.barcode.BarcodeItem
import io.scanbot.sdk.barcode.ui.BarcodeOverlayTextFormat
import io.scanbot.sdk.barcode.ui.BarcodePolygonsView
import io.scanbot.sdk.barcode.ui.BarcodeScannerView

fun enableBarcodeSelectionOverlaySnippet(barcodeScannerView: BarcodeScannerView) {
    // @Tag("Enable Barcode Selection Overlay")
    barcodeScannerView.selectionOverlayController.setEnabled(true)
    barcodeScannerView.viewController.apply {
        // This is important for Selection Overlay to work properly
        barcodeScanningInterval = 0
    }
    // @EndTag("Enable Barcode Selection Overlay")
}

fun selectionOverlayAppearanceConfigSnippet(
    barcodeScannerView: BarcodeScannerView,
    overlayStrokeWidth: Float,
    overlayPolygonColor: Int,
    overlayHighlightedPolygonColor: Int,
    overlayTextColor: Int,
    overlayTextHighlightedColor: Int,
    overlayTextContainerColor: Int,
    overlayTextContainerHighlightedColor: Int,
    overlayTextFormat: BarcodeOverlayTextFormat
) {
    // @Tag("Set appearance configuration")
    barcodeScannerView.selectionOverlayController.setBarcodeAppearanceDelegate(object :
        BarcodePolygonsView.BarcodeAppearanceDelegate {
        override fun getPolygonStyle(
            defaultStyle: BarcodePolygonsView.BarcodePolygonStyle,
            barcodeItem: BarcodeItem
        ): BarcodePolygonsView.BarcodePolygonStyle {
            return defaultStyle.copy(
                strokeWidth = overlayStrokeWidth,
                fillColor = overlayPolygonColor,
                fillHighlightedColor = overlayHighlightedPolygonColor,
                strokeColor = overlayPolygonColor,
                strokeHighlightedColor = overlayHighlightedPolygonColor,
            )
        }

        override fun getTextViewStyle(
            defaultStyle: BarcodePolygonsView.BarcodeTextViewStyle,
            barcodeItem: BarcodeItem
        ): BarcodePolygonsView.BarcodeTextViewStyle {
            return defaultStyle.copy(
                textColor = overlayTextColor,
                textHighlightedColor = overlayTextHighlightedColor,
                textContainerColor = overlayTextContainerColor,
                textContainerHighlightedColor = overlayTextContainerHighlightedColor,
                textFormat = overlayTextFormat
            )
        }
    })
    // @EndTag("Set appearance configuration")
}