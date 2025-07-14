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

import android.content.Context
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK

fun manualBarcodeDataParsing(context: Context, barcodeString: String) {
    val scanbotSDK = ScanbotBarcodeScannerSDK(context);
    scanbotSDK.createBarcodeDocumentParser().parse(barcodeString)
}

