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

// @Tag("Configuring RTU UI v2 Barcode find and pick use case snippet")
import io.scanbot.sdk.barcode.BarcodeFormats
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerScreenConfiguration
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeUseCase
import io.scanbot.sdk.ui_v2.barcode.configuration.CollapsedVisibleHeight
import io.scanbot.sdk.ui_v2.barcode.configuration.ExpectedBarcode
import io.scanbot.sdk.ui_v2.barcode.configuration.SheetMode
import io.scanbot.sdk.ui_v2.common.ScanbotColor

fun findAndPickModeUseCaseSnippet() {
    // Create the default configuration object.
    val config = BarcodeScannerScreenConfiguration().apply {
        // Configure parameters (use explicit `this.` receiver for better code completion):

        // Initialize the use case for multiple scanning.
        this.useCase = BarcodeUseCase.findAndPickScanningMode().apply {

            // Set the sheet mode for the barcodes preview.
            this.sheet.mode = SheetMode.COLLAPSED_SHEET

            // Enable/Disable the automatic selection.
            this.arOverlay.automaticSelectionEnabled = false

            // Set the height for the collapsed sheet.
            this.sheet.collapsedVisibleHeight = CollapsedVisibleHeight.LARGE

            // Enable manual count change.
            this.sheetContent.manualCountChangeEnabled = true

            // Set the delay before same barcode counting repeat.
            this.countingRepeatDelay = 1000

            // Configure the submit button.
            this.sheetContent.submitButton.text = "Submit"
            this.sheetContent.submitButton.foreground.color = ScanbotColor("#000000")

            // Configure other parameters, pertaining to findAndPick-scanning mode as needed.
            // Set the expected barcodes.
            expectedBarcodes = listOf(
                ExpectedBarcode(
                    barcodeValue = "123456",
                    title = " numeric barcode",
                    image = "https://avatars.githubusercontent.com/u/1454920",
                    count = 4
                ),
                ExpectedBarcode(
                    barcodeValue = "SCANBOT",
                    title = "value barcode",
                    image = "https://avatars.githubusercontent.com/u/1454920",
                    count = 3
                )
            )
        }

        // Set an array of accepted barcode types.
        this.scannerConfiguration.barcodeFormats = BarcodeFormats.common

        // Configure other parameters as needed.
    }
}
// @EndTag("Configuring RTU UI v2 Barcode find and pick use case snippet")
