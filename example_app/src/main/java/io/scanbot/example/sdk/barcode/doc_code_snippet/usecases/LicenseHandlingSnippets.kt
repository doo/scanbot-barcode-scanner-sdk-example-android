package io.scanbot.example.sdk.barcode.doc_code_snippet.usecases

import android.app.Application
import android.content.Context
import io.scanbot.sap.IScanbotSDKLicenseErrorHandler
import io.scanbot.sap.Status.StatusFailureAppIDMismatch
import io.scanbot.sap.Status.StatusFailureCorrupted
import io.scanbot.sap.Status.StatusFailureExpired
import io.scanbot.sap.Status.StatusFailureNotSet
import io.scanbot.sap.Status.StatusFailureWrongOS
import io.scanbot.sap.Status.StatusOkay
import io.scanbot.sap.Status.StatusTrial
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDKInitializer
import io.scanbot.sdk.util.log.LoggerProvider

fun checkingLicenseStatusSnippet(context: Context) {
    // Check the license status:
    val licenseInfo = ScanbotBarcodeScannerSDK(context).licenseInfo
    LoggerProvider.logger.d("ExampleApplication", "License status: ${licenseInfo.status}")
    LoggerProvider.logger.d("ExampleApplication", "License isValid: ${licenseInfo.isValid}")
    LoggerProvider.logger.d("ExampleApplication", "License message: ${licenseInfo.licenseStatusMessage}")

    if (licenseInfo.isValid) {
        // Making your call into ScanbotSDK API is now safe.
        // e.g. start barcode scanner
    }
}

fun licenseErrorHandlingSnippet(application: Application) {
    val licenseInfo = ScanbotBarcodeScannerSDKInitializer()
        .license(application, "YOUR_SCANBOT_SDK_LICENSE_KEY")
        .licenceErrorHandler(IScanbotSDKLicenseErrorHandler { status, feature, message ->
            LoggerProvider.logger.d("ScanbotSDK", "license status:${status.name}, message: $message")
            when (status) {
                StatusFailureNotSet,
                StatusFailureCorrupted,
                StatusFailureWrongOS,
                StatusFailureAppIDMismatch,
                StatusFailureExpired -> {
                    // license is completely invalid
                }
                StatusOkay,
                StatusTrial -> {

                }
            }
        })
        .initialize(application)
}