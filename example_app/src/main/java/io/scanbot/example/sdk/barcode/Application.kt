package io.scanbot.example.sdk.barcode

import android.app.Application
import io.scanbot.sap.IScanbotSDKLicenseErrorHandler
import io.scanbot.sap.SdkFeature
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDKInitializer
import io.scanbot.sdk.util.log.LoggerProvider

class ExampleApplication : Application() {

    // Trial License Key!
    val licenseKey =
        "ipObkyAD/rYq6twCQRi+jbREIywvHa" +
        "g6LsojqVCCnjTh9dtR6JfQx3F98o9I" +
        "lrrwerf/kshndC7GVTgJSycghCqLPV" +
        "SwWVV+qeWcQViIcemHhhhZpfMCz2/9" +
        "lqYBkShjb31qeGV3gB3JvKn/HRWOKT" +
        "zOVFqLEqa6l1ixEj+UVOiUgdl101Gm" +
        "pFLdo+WIPD2LtQOFw+GtGtBQJkiK3D" +
        "+axKCN66uCJu3BpNJVLdBkjRmfOGni" +
        "XQauOF5b372W+ADta2o0wCUbh6Q8KA" +
        "+eDBAJO2pBWv1TdC/zzBqLjNo/0nRG" +
        "IJXlGc9mfQdeBvR4zGkTl3b37DjVnR" +
        "B44uwB82Dcvw==\nU2NhbmJvdFNESw" +
        "ppby5zY2FuYm90LmV4YW1wbGUuc2Rr" +
        "LmJhcmNvZGUuYW5kcm9pZAoxNjUwMD" +
        "Y3MTk5CjUxMgoy\n"

    override fun onCreate() {
        super.onCreate()

        ScanbotBarcodeScannerSDKInitializer()
            .withLogging(true)
            .license(this, licenseKey)
            .licenceErrorHandler(IScanbotSDKLicenseErrorHandler { status, feature, statusMessage ->
                LoggerProvider.logger.d("ExampleApplication", "+++> License status: ${status.name}. Status message: $statusMessage")
                if (feature != SdkFeature.NoSdkFeature) {
                    LoggerProvider.logger.d("ExampleApplication", "+++> Feature not available: ${feature.name}")
                }
            })
            // Uncomment to use new Camera X approach in Ready-To-Use UI screens (BETA)
            // .useCameraXRtuUi(true)
            //.sdkFilesDirectory(this, getExternalFilesDir(null)!!)
            .initialize(this)

        LoggerProvider.logger.d("ExampleApplication", "Scanbot Barcode Scanner SDK was initialized")

        val licenseInfo = ScanbotBarcodeScannerSDK(this).licenseInfo
        LoggerProvider.logger.d("ExampleApplication", "License status: ${licenseInfo.status}")
        LoggerProvider.logger.d("ExampleApplication", "License isValid: ${licenseInfo.isValid}")
        LoggerProvider.logger.d("ExampleApplication", "License expirationDate: ${licenseInfo.expirationDate}")
    }
}

