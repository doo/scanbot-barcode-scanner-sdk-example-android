package io.scanbot.example.sdk.barcode.doc_code_snippet

/*
    NOTE: this snippet of code is to be used only as a part of the website documentation.
    This code is not intended for any use outside of the support of documentation by Scanbot SDK GmbH employees.
*/

// NOTE for maintainers: whenever changing this code,
// ensure that links using it are still pointing to valid lines!
// Pay attention to imports adding/removal/sorting!
// Page URLs using this code:
// TODO: add URLs here

// @Tag("Configuring RTU UI v2 Barcode AR overlay snippet")
import io.scanbot.sdk.barcode.BarcodeFormats
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerConfiguration
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerScreenConfiguration
import io.scanbot.sdk.ui_v2.barcode.configuration.CollapsedVisibleHeight
import io.scanbot.sdk.ui_v2.barcode.configuration.MultipleBarcodesScanningMode
import io.scanbot.sdk.ui_v2.barcode.configuration.MultipleScanningMode
import io.scanbot.sdk.ui_v2.barcode.configuration.SheetMode

fun arOverlayUseCaseSnippet() {
    // Create the default configuration object.
    val config = BarcodeScannerScreenConfiguration().apply {
        // Configure parameters (use explicit `this.` receiver for better code completion):


        // Configure the usecase.
        this.useCase = MultipleScanningMode().apply {
            this.mode = MultipleBarcodesScanningMode.UNIQUE
            this.sheet.mode = SheetMode.COLLAPSED_SHEET
            this.sheet.collapsedVisibleHeight = CollapsedVisibleHeight.SMALL
            // Configure AR Overlay.
            this.arOverlay.visible = true
            this.arOverlay.automaticSelectionEnabled = false

            // Configure other parameters, pertaining to use case as needed.
        }

        // Set an array of accepted barcode types.
        this.scannerConfiguration.barcodeFormats = BarcodeFormats.common

        // Configure other parameters as needed.
    }
}
// @EndTag("Configuring RTU UI v2 Barcode AR overlay snippet")
