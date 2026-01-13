package io.scanbot.example.sdk.barcode.doc_code_snippet.usecases

import android.app.Application
import android.content.Context
import io.scanbot.sap.IScanbotSDKLicenseErrorHandler
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDKInitializer
import io.scanbot.sdk.licensing.LicenseStatus
import io.scanbot.sdk.util.log.LoggerProvider

fun checkingLicenseStatusSnippet(context: Context) {
    // Check the license status:
    val licenseInfo = ScanbotBarcodeScannerSDK(context).licenseInfo
    LoggerProvider.logger.d("ExampleApplication", "License status: ${licenseInfo.status}")
    LoggerProvider.logger.d("ExampleApplication", "License isValid: ${licenseInfo.isValid}")
    LoggerProvider.logger.d(
        "ExampleApplication",
        "License message: ${licenseInfo.licenseStatusMessage}"
    )

    if (licenseInfo.isValid) {
        // Making your call into ScanbotSDK API is now safe.
        // e.g. start barcode scanner
    }
}

fun licenseErrorHandlingSnippet(application: Application) {
    val licenseInfo = ScanbotBarcodeScannerSDKInitializer()
        .license(application, "YOUR_SCANBOT_SDK_LICENSE_KEY")
        .licenseErrorHandler { status, feature, message ->
            LoggerProvider.logger.d(
                "ScanbotSDK",
                "license status:${status.name}, message: $message"
            )
            when (status) {
                LicenseStatus.OKAY -> {}
                LicenseStatus.TRIAL -> {}
                LicenseStatus.OKAY_EXPIRING_SOON -> {}
                LicenseStatus.FAILURE_NOT_SET,
                LicenseStatus.FAILURE_CORRUPTED,
                LicenseStatus.FAILURE_WRONG_OS,
                LicenseStatus.FAILURE_APP_ID_MISMATCH,
                LicenseStatus.FAILURE_EXPIRED,
                LicenseStatus.FAILURE_SERVER,
                LicenseStatus.FAILURE_VERSION,
                LicenseStatus.FAILURE_INACTIVE -> {
                    // license is completely invalid
                }
            }
        }
        .initialize(application)
}