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
import io.scanbot.sdk.barcode.BarcodeDocumentFormat
import io.scanbot.sdk.barcode.BarcodeFormats
import io.scanbot.sdk.barcode.setBarcodeFormats
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK

fun barcodeScannerWithSimpleConfigSnippet(context: Context) {
    // @Tag("Simple configuring Barcode Scanner")
    val barcodeScanner = ScanbotBarcodeScannerSDK(context).createBarcodeScanner()

    barcodeScanner.setConfiguration(
        barcodeScanner.copyCurrentConfiguration().apply {
            onlyAcceptDocuments = false
            this.extractedDocumentFormats = listOf(
                BarcodeDocumentFormat.AAMVA,
                BarcodeDocumentFormat.BOARDING_PASS,
                BarcodeDocumentFormat.DE_MEDICAL_PLAN,
                BarcodeDocumentFormat.MEDICAL_CERTIFICATE,
                BarcodeDocumentFormat.ID_CARD_PDF_417,
                BarcodeDocumentFormat.SEPA,
                BarcodeDocumentFormat.SWISS_QR,
                BarcodeDocumentFormat.VCARD,
                BarcodeDocumentFormat.GS1,
                BarcodeDocumentFormat.HIBC
            )
            setBarcodeFormats(
                // set the supported barcode formats here
                barcodeFormats = BarcodeFormats.common,
            )
        }
    )
    // @EndTag("Simple configuring Barcode Scanner")
}
