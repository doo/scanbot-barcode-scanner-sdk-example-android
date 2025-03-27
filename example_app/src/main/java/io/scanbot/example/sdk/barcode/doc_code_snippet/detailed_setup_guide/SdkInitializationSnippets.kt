package io.scanbot.example.sdk.barcode.doc_code_snippet.detailed_setup_guide

/*
    NOTE: this snippet of code is to be used only as a part of the website documentation.
    This code is not intended for any use outside of the support of documentation by Scanbot SDK GmbH employees.
*/

// NOTE for maintainers: whenever changing this code,
// ensure that links using it are still pointing to valid lines!
// Pay attention to imports adding/removal/sorting!
// Page URLs using this code:
// TODO: add URLs here

import android.app.Application
import io.scanbot.sdk.*
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDKInitializer

fun xnnpackAccelerationSnippet(application: Application) {
    // @Tag("XNNPACK Acceleration")
    ScanbotBarcodeScannerSDKInitializer()
            .allowXnnpackAcceleration(false)
            // ...
            .initialize(application)
    // @EndTag("XNNPACK Acceleration")
}

fun gpuAccelerationSnippet(application: Application) {
    // @Tag("GPU Acceleration")
    ScanbotBarcodeScannerSDKInitializer()
            .allowGpuAcceleration(false)
            // ...
            .initialize(application)
    // @EndTag("GPU Acceleration")
}

fun precompileGpuMlModelsSnippet(application: Application) {
    // @Tag("Precompile GPU ML Models")
    ScanbotBarcodeScannerSDKInitializer()
            .precompileGpuMlModels(
                    precompilingCallback = {},
            )
            // ...
            .initialize(application)
    // @EndTag("Precompile GPU ML Models")
}