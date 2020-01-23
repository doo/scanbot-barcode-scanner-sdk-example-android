package io.scanbot.example.sdk.barcode

import android.app.Application
import io.scanbot.sap.IScanbotSDKLicenseErrorHandler
import io.scanbot.sap.SdkFeature
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDKInitializer
import net.doo.snap.util.log.LoggerProvider

class SnapApplication : Application() {
    val licenseKey =
        "BSMZQgVt1mQTbh8hCdeADm4BrX1Exm" +
                "4OiTLy9Itr7vCvosA9HQ+LBCwlvJdB" +
                "gH8NvR7jXrop4+o1jD2hYX1UYdxcrF" +
                "owZuqzbYz7a19nKZKrKZ9M4KRKoWnk" +
                "hSrrG/xqa6n1YhndjmmArgHrhKahYR" +
                "QL1wVeP4xNttYQaDqRsbn5nvlkDKbo" +
                "BlEuQiab5l/7hqcyCCZIk3HnYPWCtT" +
                "/+iB/Lw5fxBJ9YxxHAlz598myK99t4" +
                "RFF4IPU09WTeFgoyUr6AbyHqSMIPDR" +
                "OdlJxWMvtO4xV3T5M4hX5XMFLf+FYP" +
                "CEiVwbOnwctTr5zKEVAaH85fhYqBdb" +
                "MSs/BVCE1Dug==\nU2NhbmJvdFNESw" +
                "ppby5zY2FuYm90LmV4YW1wbGUuc2Rr" +
                "LmJhcmNvZGUuYW5kcm9pZAoxNTgyMz" +
                "I5NTk5CjUxMgoy\n";

    override fun onCreate() {
        super.onCreate()

        ScanbotBarcodeScannerSDKInitializer()
            .withLogging(true)
            .license(this, licenseKey)
            .licenceErrorHandler(IScanbotSDKLicenseErrorHandler { status, feature ->
                LoggerProvider.getLogger()
                    .d("ScanbotBarcodeScannerSDK", "license status:${status.name}")
                if (feature != SdkFeature.NoSdkFeature) {
                    LoggerProvider.getLogger()
                        .d("ScanbotBarcodeScannerSDK", "feature not available:${feature.name}")
                }
            })
            .sdkFilesDirectory(this, getExternalFilesDir(null)!!)
            .prepareBarcodeScannerBlobs(true)
            .initialize(this)

    }
}

