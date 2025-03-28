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

// @Tag("Configuring RTU UI v2 palette snippet")
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerScreenConfiguration
import io.scanbot.sdk.ui_v2.common.ScanbotColor

fun paletteConfigSnippet() {
    // Create the default configuration object.
    val config = BarcodeScannerScreenConfiguration().apply {
        // Configure parameters (use explicit `this.` receiver for better code completion):

        // Simply alter one color and keep the other default.
        this.palette.apply {
            this.sbColorPrimary = ScanbotColor("#c86e19")
        }

        // ... or set an entirely new palette.
        this.palette.apply {
            this.sbColorPrimary = ScanbotColor("#C8193C")
            this.sbColorPrimaryDisabled = ScanbotColor("#F5F5F5")
            this.sbColorNegative = ScanbotColor("#FF3737")
            this.sbColorPositive = ScanbotColor("#4EFFB4")
            this.sbColorWarning = ScanbotColor("#FFCE5C")
            this.sbColorSecondary = ScanbotColor("#FFEDEE")
            this.sbColorSecondaryDisabled = ScanbotColor("#F5F5F5")
            this.sbColorOnPrimary = ScanbotColor("#FFFFFF")
            this.sbColorOnSecondary = ScanbotColor("#C8193C")
            this.sbColorSurface = ScanbotColor("#FFFFFF")
            this.sbColorOutline = ScanbotColor("#EFEFEF")
            this.sbColorOnSurfaceVariant = ScanbotColor("#707070")
            this.sbColorOnSurface = ScanbotColor("#000000")
            this.sbColorSurfaceLow = ScanbotColor("#26000000")
            this.sbColorSurfaceHigh = ScanbotColor("#7A000000")
            this.sbColorModalOverlay = ScanbotColor("#A3000000")
        }
    }
}
// @EndTag("Configuring RTU UI v2 palette snippet")
