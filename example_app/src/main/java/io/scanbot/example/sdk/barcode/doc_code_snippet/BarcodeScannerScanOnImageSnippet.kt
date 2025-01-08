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
import android.graphics.Bitmap
import android.widget.Toast
import io.scanbot.sdk.barcode.BarcodeScanner
import io.scanbot.sdk.barcode.BarcodeScannerResult
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK

fun scanOnImageSnippet(context: Context) {
    val sdk = ScanbotBarcodeScannerSDK(context)
    val barcodeScanner = sdk.createBarcodeScanner()
}

fun processImageSnippet(
    sdk: ScanbotBarcodeScannerSDK,
    barcodeDetector: BarcodeScanner,
    bitmap: Bitmap
) {
    if (!sdk.licenseInfo.isValid) { return; }

    val result = barcodeDetector.scanFromBitmap(bitmap, 0)
    // handle the detected barcode(s) from result
}

fun handleResultSnippet(context: Context, result: BarcodeScannerResult) {
    val barcodeData = result!!
    val barcodesTextResult = StringBuilder()
    for (item in barcodeData.barcodes) {
        barcodesTextResult.append(item.format.name + "\n" + item.text)
            .append("\n")
            .append("-------------------")
            .append("\n")
    }
    Toast.makeText(context, barcodesTextResult.toString(), Toast.LENGTH_LONG).show()
}


