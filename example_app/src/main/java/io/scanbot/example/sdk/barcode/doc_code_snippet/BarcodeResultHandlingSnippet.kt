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
import io.scanbot.sdk.barcode.entity.AAMVA
import io.scanbot.sdk.barcode.entity.BoardingPass
import io.scanbot.sdk.barcode.entity.DEMedicalPlan
import io.scanbot.sdk.barcode.entity.GS1
import io.scanbot.sdk.barcode.entity.HIBC
import io.scanbot.sdk.barcode.entity.IDCardPDF417
import io.scanbot.sdk.barcode.entity.MedicalCertificate
import io.scanbot.sdk.barcode.entity.SEPA
import io.scanbot.sdk.barcode.entity.SwissQR
import io.scanbot.sdk.barcode.entity.VCard
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK

fun handlingResult(bitmap: Bitmap, context: Context) {
    // @Tag("Handling the Result")
    val barcodeScanner = ScanbotBarcodeScannerSDK(context).createBarcodeScanner()
    val result = barcodeScanner.scanFromBitmap(bitmap, 0)
    result?.barcodes?.forEach { barcodeItem ->
        // Handle the detected barcode(s) from result
        val barcodeText = barcodeItem.text
        val barcodeFormat = barcodeItem.format
        // The barcodeItem contains the scanned barcode data as ByteArray
        val barcodeRawData = barcodeItem.rawBytes
        // This is the image of the barcode that was scanned in the form of a ImageRef.(SDK's Internal representation of the image)
        // Call sourceImage?.toBitmap() to get the image as a Bitmap
        val barcodeImage = barcodeItem.sourceImage
    }
    // @EndTag("Handling the Result")
}

fun handlingParsedDocumentsResult(bitmap: Bitmap, context: Context) {
    // @Tag("Handling the parsed document result")
    val barcodeScanner = ScanbotBarcodeScannerSDK(context).createBarcodeScanner()
    val result = barcodeScanner.scanFromBitmap(bitmap, 0)
    result?.barcodes?.forEach { barcodeItem ->
        barcodeItem.extractedDocument?.let { document ->
            when (document.type.name) {
                AAMVA.DOCUMENT_TYPE -> {
                    // Handle AAMVA document
                    val aamva = AAMVA(document)
                    val titleData = aamva.titleData
                    val vehicleData = aamva.vehicleData
                    val registrationData = aamva.registrationData
                }
                BoardingPass.DOCUMENT_TYPE -> {
                    // Handle Boarding Pass document
                    val boardingPass = BoardingPass(document)
                    val passengerName = boardingPass.passengerName
                    val legs = boardingPass.legs
                }
                MedicalCertificate.DOCUMENT_TYPE -> {
                    // Handle Medical Certificate document
                    val medicalCertificate = MedicalCertificate(document)
                    val firstName = medicalCertificate.firstName
                }
                DEMedicalPlan.DOCUMENT_TYPE -> {
                    // Handle DeMedical Plan document
                    val deMedicalPlan = DEMedicalPlan(document)
                    val doctor = deMedicalPlan.doctor
                    val patient = deMedicalPlan.patient
                }
                IDCardPDF417.DOCUMENT_TYPE -> {
                    // Handle ID Card PDF 417 document
                    val idCardPdf417 =IDCardPDF417(document)
                    val firstName = idCardPdf417.firstName
                    val lastName = idCardPdf417.lastName
                }
                GS1.DOCUMENT_TYPE -> {
                    // Handle GS1 document
                    val gs1 = GS1(document)
                    val elements = gs1.elements
                }
                SEPA.DOCUMENT_TYPE -> {
                    // Handle SEPA document
                    val sepa = SEPA(document)
                    val receiverName = sepa.receiverName
                    val amount = sepa.amount
                }
                SwissQR.DOCUMENT_TYPE -> {
                    // Handle Swiss QR document
                    val swissQr = SwissQR(document)
                    val payeeName = swissQr.payeeName
                    val amount = swissQr.amount
                }
                VCard.DOCUMENT_TYPE -> {
                    // Handle vCard document
                    val vCard = VCard(document)
                    val name = vCard.name
                    val emails = vCard.emails
                }
                HIBC.DOCUMENT_TYPE -> {
                    // Handle HIBC document
                    val hibc = HIBC(document)
                    val lotNumber = hibc.lotNumber
                    val serialNumber = hibc.serialNumber
                }

                else -> {
                    // Handle other document types if needed
                }
            }
        }
    }
    // @EndTag("Handling the parsed document result")
}