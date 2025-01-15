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

// @Tag("Configuring RTU UI v2 Barcode item mapping snippet")
import io.scanbot.sdk.barcode.BarcodeItem
import io.scanbot.sdk.barcode.entity.textWithExtension
import io.scanbot.sdk.ui_v2.barcode.common.mappers.getName
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeItemMapper
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeMappedData
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeMappingErrorCallback
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeMappingResultCallback
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerScreenConfiguration
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeUseCase

fun itemMappingConfigSnippet() {
    // Create the default configuration object.
    val config = BarcodeScannerScreenConfiguration().apply {
        // Configure parameters (use explicit `this.` receiver for better code completion):

        this.useCase = BarcodeUseCase.singleScanningMode().apply {
            class CustomMapper() : BarcodeItemMapper {

                override fun mapBarcodeItem(
                    barcodeItem: BarcodeItem,
                    onResult: BarcodeMappingResultCallback,
                    onError: BarcodeMappingErrorCallback
                ) {
                    /** TODO: process scan result as needed to get your mapped data,
                     * e.g. query your server to get product image, title and subtitle.
                     * See example below.
                     */
                    val title = "Some product ${barcodeItem.textWithExtension}"
                    val subtitle = barcodeItem.format?.getName() ?: "Unknown"
                    val image = "https://avatars.githubusercontent.com/u/1454920" //WARNING: all web links won't work without internet permission

                    /** TODO: call [BarcodeMappingResult.onError()] in case of error during obtaining mapped data. */
                    if (barcodeItem.textWithExtension == "Error occurred!") {
                        onError.onError()
                    } else {
                        onResult.onResult(
                            BarcodeMappedData(
                                title = title,
                                subtitle = subtitle,
                                barcodeImage = image,
                            )
                        )
                    }
                }
            }
            this.barcodeInfoMapping.barcodeItemMapper = CustomMapper()
        }

        // Configure other parameters as needed.
    }
}
// @EndTag("Configuring RTU UI v2 Barcode item mapping snippet")
