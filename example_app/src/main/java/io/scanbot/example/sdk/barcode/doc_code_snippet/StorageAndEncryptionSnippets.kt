package io.scanbot.example.sdk.barcode.doc_code_snippet

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDKInitializer
import io.scanbot.sdk.persistence.fileio.AesGcmEncryptedFileIoProcessor
import io.scanbot.sdk.persistence.fileio.AesGcmKeyProvider
import java.io.File

fun initSDKSnippet(application: Application) {
    // @Tag("Initialize SDK")
    // Example for using a sub-folder in the external(!) storage:
    val customStorageDir = File(application.getExternalFilesDir(null), "my-custom-storage-dir")
    customStorageDir.mkdirs()

    ScanbotBarcodeScannerSDKInitializer()
        .sdkFilesDirectory(customStorageDir)
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
    val ENCRYPTION_KEY = "DEMO_ONLY_REPLACE_WITH_32B_KEY!!" // WARNING: demo key only! Must be exactly 32 bytes for AES256 (16 bytes for AES128).
    val encryptionMode = AesGcmEncryptedFileIoProcessor.AESGCMEncrypterMode.AES256
    val stubKeyProvider = object : AesGcmKeyProvider {
        override fun getAesKeyForContext(file: String, keyMode: AesGcmEncryptedFileIoProcessor.AESGCMEncrypterMode): ByteArray =
            ENCRYPTION_KEY.toByteArray()
    }
    ScanbotBarcodeScannerSDKInitializer().useFileEncryption(
        enableFileEncryption = true,
        fileIOProcessor = AesGcmEncryptedFileIoProcessor(stubKeyProvider, encryptionMode)
    ).initialize(application)
    // @EndTag("Enable AES Encryption")
}

fun readFileWithFileIOProcessor(context: Context, source: File) {
    // @Tag("Read File")
    val scanbotBarcodeScannerSDK = ScanbotBarcodeScannerSDK(context)
    val decryptedImageBitmap: Bitmap? = scanbotBarcodeScannerSDK.fileIOProcessor().readImage(source, options = null)
    // @EndTag("Read File")
}
