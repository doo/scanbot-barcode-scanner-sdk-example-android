package io.scanbot.example.sdk.barcode.doc_code_snippet

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDKInitializer
import io.scanbot.sdk.persistence.fileio.AESEncryptedFileIOProcessor
import java.io.File

fun initSDKSnippet(application: Application) {
    // @Tag("Initialize SDK")
    // Example for using a sub-folder in the external(!) storage:
    val customStorageDir = File(application.getExternalFilesDir(null), "my-custom-storage-dir")
    customStorageDir.mkdirs()

    ScanbotBarcodeScannerSDKInitializer()
        .sdkFilesDirectory(application, customStorageDir)
        // ...
        .initialize(application)
    // @EndTag("Initialize SDK")
}

fun enableEncryptionInSDKInitializerSnippet(application: Application) {
    // @Tag("Enable Encryption")
    ScanbotBarcodeScannerSDKInitializer()
        .useFileEncryption(true)
        .initialize(application)
    // @EndTag("Enable Encryption")
}

fun enableAesEncryptionSnippet(application: Application) {
    // @Tag("Enable AES Encryption")
    ScanbotBarcodeScannerSDKInitializer()
        .useFileEncryption(true, AESEncryptedFileIOProcessor("any_user_password", AESEncryptedFileIOProcessor.AESEncrypterMode.AES256))
        .initialize(application)
    // @EndTag("Enable AES Encryption")
}

fun readFileWithFileIOProcessor(context: Context, source: File) {
    // @Tag("Read File")
    val scanbotBarcodeScannerSDK = ScanbotBarcodeScannerSDK(context)
    val decryptedImageBitmap: Bitmap? = scanbotBarcodeScannerSDK.fileIOProcessor().readImage(source, options = null)
    // @EndTag("Read File")
}