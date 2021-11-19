package io.scanbot.example.sdk.barcode

import android.app.Application
import io.scanbot.sap.IScanbotSDKLicenseErrorHandler
import io.scanbot.sap.SdkFeature
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDKInitializer
import io.scanbot.sdk.util.log.LoggerProvider

class ExampleApplication : Application() {
    /*
     * TODO 1/2: Add the Scanbot Barcode SDK license key here.
     * Please note: The Scanbot Barcode SDK will run without a license key for one minute per session!
     * After the trial period is over all Scanbot SDK functions as well as the UI components will stop working.
     * You can get an unrestricted "no-strings-attached" 30 day trial license key for free.
     * Please submit the trial license form (https://scanbot.io/sdk/trial.html) on our website by using
     * the app identifier "io.scanbot.example.sdk.barcode.android" of this example app.
     */
    val licenseKey = ""

    override fun onCreate() {
        super.onCreate()

        ScanbotBarcodeScannerSDKInitializer()
            .withLogging(true)
            // TODO 2/2: Enable the Scanbot Barcode SDK license key
            //.license(this, licenseKey)
            .licenceErrorHandler(IScanbotSDKLicenseErrorHandler { status, feature, statusMessage ->
                LoggerProvider.logger.d("ExampleApplication", "+++> License status: ${status.name}. Status message: $statusMessage")
                if (feature != SdkFeature.NoSdkFeature) {
                    LoggerProvider.logger.d("ExampleApplication", "+++> Feature not available: ${feature.name}")
                }
            })
            // Uncomment to use new Camera X approach in Ready-To-Use UI screens (BETA)
             .useCameraXRtuUi(true)
            //.sdkFilesDirectory(this, getExternalFilesDir(null)!!)
            .initialize(this)

        LoggerProvider.logger.d("ExampleApplication", "Scanbot Barcode Scanner SDK was initialized")

        val licenseInfo = ScanbotBarcodeScannerSDK(this).licenseInfo
        LoggerProvider.logger.d("ExampleApplication", "License status: ${licenseInfo.status}")
        LoggerProvider.logger.d("ExampleApplication", "License isValid: ${licenseInfo.isValid}")
        LoggerProvider.logger.d("ExampleApplication", "License expirationDate: ${licenseInfo.expirationDate}")
    }
}

