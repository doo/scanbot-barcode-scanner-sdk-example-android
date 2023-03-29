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
    val licenseKey = "i/RLY9t/K2JLKqH/CBYtvczh9aLURu" +
            "i/lfUikmrQms5Td1iCgRhVx6K2ebCB" +
            "E95DehPnWlfgryCNg/PMGuq6iOFdjY" +
            "EeNz8HDIgdHYb9L+Qi+tPpyylbx/Et" +
            "RnU4VVF2AyVecqeNfUPt6rPXDMae/f" +
            "OrST4OSW3Ss8yUUIWRO9MopoPSNdQ+" +
            "MCdJxox+gxFcAshBWM32J5w+cymu3J" +
            "Y+/C9HfAZ/W3YS0QioRxA0wLdLLSMu" +
            "zRzrIA9HdestZA7Qz8g8av+jczzibO" +
            "9rbltuao7/wK3oXFNRYOGfno3cOt2C" +
            "S0xhh6qRSXZz+s4G4QKvljDBJrH/6h" +
            "ioPLyLk2Z58g==\nU2NhbmJvdFNESw" +
            "ppby5zY2FuYm90LmV4YW1wbGUuc2Rr" +
            "LmJhcmNvZGUuYW5kcm9pZAoxNjgxMD" +
            "g0Nzk5CjUxMgoy\n";

    override fun onCreate() {
        super.onCreate()

        ScanbotBarcodeScannerSDKInitializer()
            .withLogging(true)
            // TODO 2/2: Enable the Scanbot Barcode SDK license key
            .license(this, licenseKey)
            .licenceErrorHandler(IScanbotSDKLicenseErrorHandler { status, feature, statusMessage ->
                LoggerProvider.logger.d("ExampleApplication", "+++> License status: ${status.name}. Status message: $statusMessage")
                if (feature != SdkFeature.NoSdkFeature) {
                    LoggerProvider.logger.d("ExampleApplication", "+++> Feature not available: ${feature.name}")
                }
            })
            // Uncomment to switch back to the legacy camera approach in Ready-To-Use UI screens
            // .useCameraXRtuUi(false)
            //.sdkFilesDirectory(this, getExternalFilesDir(null)!!)
            .initialize(this)

        LoggerProvider.logger.d("ExampleApplication", "Scanbot Barcode Scanner SDK was initialized")

        val licenseInfo = ScanbotBarcodeScannerSDK(this).licenseInfo
        LoggerProvider.logger.d("ExampleApplication", "License status: ${licenseInfo.status}")
        LoggerProvider.logger.d("ExampleApplication", "License isValid: ${licenseInfo.isValid}")
        LoggerProvider.logger.d("ExampleApplication", "License expirationDate: ${licenseInfo.expirationDate}")
    }
}

