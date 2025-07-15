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
import io.scanbot.sdk.barcode.AustraliaPostCustomerFormat
import io.scanbot.sdk.barcode.BarcodeDocumentFormat
import io.scanbot.sdk.barcode.BarcodeFormat
import io.scanbot.sdk.barcode.BarcodeFormatAustraliaPostConfiguration
import io.scanbot.sdk.barcode.BarcodeFormatCode11Configuration
import io.scanbot.sdk.barcode.BarcodeFormatCode2Of5Configuration
import io.scanbot.sdk.barcode.BarcodeFormatCommonConfiguration
import io.scanbot.sdk.barcode.BarcodeFormatConfigurationBase
import io.scanbot.sdk.barcode.BarcodeFormatMsiPlesseyConfiguration
import io.scanbot.sdk.barcode.BarcodeFormats
import io.scanbot.sdk.barcode.BarcodeScannerConfiguration
import io.scanbot.sdk.barcode.BarcodeScannerEngineMode
import io.scanbot.sdk.barcode.Gs1Handling
import io.scanbot.sdk.barcode.MsiPlesseyChecksumAlgorithm
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import java.util.EnumSet

fun barcodeScannerWithAdvancedConfigSnippet(context: Context) {
    // @Tag("Advanced configuring Barcode Scanner")
    val barcodeScanner = ScanbotBarcodeScannerSDK(context).createBarcodeScanner()

    var configs = mutableListOf<BarcodeFormatConfigurationBase>()

    val baseConfig = BarcodeFormatCommonConfiguration.default().copy(
        regexFilter = "",
        minimum1DQuietZoneSize = 10,
        stripCheckDigits = false,
        minimumTextLength = 0,
        maximumTextLength = 0,
        gs1Handling = Gs1Handling.PARSE,
        strictMode = true,
        formats = BarcodeFormats.common,
        addAdditionalQuietZone = false
    )
    configs.add(baseConfig)
    // Add individual configurations for specific barcode formats
    val australiaPostConfig = BarcodeFormatAustraliaPostConfiguration(
        regexFilter = "",
        australiaPostCustomerFormat = AustraliaPostCustomerFormat.ALPHA_NUMERIC
    )
    configs.add(australiaPostConfig)

    val msiPlesseyConfig = BarcodeFormatMsiPlesseyConfiguration(
        regexFilter = "",
        minimum1DQuietZoneSize = 10,
        stripCheckDigits = false,
        minimumTextLength = 0,
        maximumTextLength = 0,
        checksumAlgorithms = listOf(MsiPlesseyChecksumAlgorithm.MOD_10)
    )
    configs.add(msiPlesseyConfig)

    val code11Config = BarcodeFormatCode11Configuration(
        regexFilter = "",
        minimum1DQuietZoneSize = 10,
        stripCheckDigits = false,
        minimumTextLength = 0,
        maximumTextLength = 0,
        checksum = true
    )
    configs.add(code11Config)

    val code2Of5Config = BarcodeFormatCode2Of5Configuration(
        regexFilter = "",
        minimum1DQuietZoneSize = 10,
        stripCheckDigits = false,
        minimumTextLength = 0,
        maximumTextLength = 0,
        iata2of5 = true,
        code25 = false,
        industrial2of5 = false,
        useIATA2OF5Checksum = true
    )
    configs.add(code2Of5Config)

    // Set the configurations to the barcode scanner
    barcodeScanner.setConfiguration(barcodeScanner.copyCurrentConfiguration().apply {
        barcodeFormatConfigurations = configs
        extractedDocumentFormats = listOf( BarcodeDocumentFormat.AAMVA, BarcodeDocumentFormat.BOARDING_PASS, BarcodeDocumentFormat.DE_MEDICAL_PLAN, BarcodeDocumentFormat.MEDICAL_CERTIFICATE, BarcodeDocumentFormat.ID_CARD_PDF_417, BarcodeDocumentFormat.SEPA, BarcodeDocumentFormat.SWISS_QR, BarcodeDocumentFormat.VCARD, BarcodeDocumentFormat.GS1, BarcodeDocumentFormat.HIBC )
        onlyAcceptDocuments = false
        engineMode = BarcodeScannerEngineMode.NEXT_GEN
        returnBarcodeImage = true
    })
    // @EndTag("Advanced configuring Barcode Scanner")
}

fun barcodeFormatCommonConfigurationSnippet(context: Context) {
    // @Tag("Configuring BarcodeFormatCommonConfiguration in Barcode Scanner")
    val baseConfig = BarcodeFormatCommonConfiguration.default().copy(
        regexFilter = "",
        minimum1DQuietZoneSize = 10,
        stripCheckDigits = false,
        minimumTextLength = 0,
        maximumTextLength = 0,
        gs1Handling = Gs1Handling.PARSE,
        strictMode = true,
        formats = BarcodeFormats.common,
        addAdditionalQuietZone = false
    )
    // @EndTag("Configuring BarcodeFormatCommonConfiguration in Barcode Scanner")
}

fun barcodeFormatIndividualSimplifiedConfigurationSnippet(context: Context) {
    // @Tag("Configuring individual symbologies in Barcode Scanner")
    val baseConfig = BarcodeFormatCommonConfiguration.default().copy(
        regexFilter = "",
        minimum1DQuietZoneSize = 10,
        stripCheckDigits = false,
        minimumTextLength = 0,
        maximumTextLength = 0,
        gs1Handling = Gs1Handling.PARSE,
        strictMode = true,
        formats = listOf(BarcodeFormat.QR_CODE, BarcodeFormat.AZTEC, BarcodeFormat.CODE_128),
        addAdditionalQuietZone = false
    )
    // @EndTag("Configuring individual symbologies in Barcode Scanner")
}

fun barcodeParsersConfigurationSnippet(context: Context) {
    // @Tag("Configuring parsers in Barcode Scanner")
    val barcodeScanner = ScanbotBarcodeScannerSDK(context).createBarcodeScanner()

    var configs = mutableListOf<BarcodeFormatConfigurationBase>()

    barcodeScanner.setConfiguration(barcodeScanner.copyCurrentConfiguration().apply {
        barcodeFormatConfigurations = configs
        // Example of adding a specific configuration for parsed documents
        extractedDocumentFormats = listOf( BarcodeDocumentFormat.AAMVA, BarcodeDocumentFormat.BOARDING_PASS, BarcodeDocumentFormat.DE_MEDICAL_PLAN, BarcodeDocumentFormat.MEDICAL_CERTIFICATE, BarcodeDocumentFormat.ID_CARD_PDF_417, BarcodeDocumentFormat.SEPA, BarcodeDocumentFormat.SWISS_QR, BarcodeDocumentFormat.VCARD, BarcodeDocumentFormat.GS1, BarcodeDocumentFormat.HIBC )
        onlyAcceptDocuments = false
        engineMode = BarcodeScannerEngineMode.NEXT_GEN
        returnBarcodeImage = true
    })
    // @EndTag("Configuring parsers in Barcode Scanner")
}

