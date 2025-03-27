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

// @Tag("Configuring RTU UI v2 Barcode screen localization snippet")
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerScreenConfiguration

fun configurationWithLocalizationSnippet() {
    // Create the default configuration object.
    val config = BarcodeScannerScreenConfiguration().apply {
        // Configure parameters (use explicit `this.` receiver for better code completion):

        // Configure localization parameters.
        this.localization.apply {
            barcodeInfoMappingErrorStateCancelButton = "Custom Cancel title"
            cameraPermissionCloseButton = "Custom Close title"


            // Configure other strings as needed.
        }

        // Configure other parameters as needed.
    }
}
// @EndTag("Configuring RTU UI v2 Barcode screen localization snippet")
