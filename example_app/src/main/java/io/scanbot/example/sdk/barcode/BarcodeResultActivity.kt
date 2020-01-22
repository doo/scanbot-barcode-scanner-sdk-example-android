package io.scanbot.example.sdk.barcode

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.scanbot.barcodescanner.model.BarCodeScannerDocumentFormat
import io.scanbot.barcodescanner.model.DEMedicalPlan.DEMedicalPlanDocument
import io.scanbot.barcodescanner.model.DisabilityCertificate.DisabilityCertificateDocument
import io.scanbot.barcodescanner.model.SEPA.SEPADocument
import io.scanbot.barcodescanner.model.VCard.VCardDocument
import io.scanbot.barcodescanner.model.boardingPass.BoardingPassDocument
import io.scanbot.sdk.barcode.entity.BarcodeItem
import io.scanbot.sdk.barcode.entity.BarcodeScanningResult
import kotlinx.android.synthetic.main.activity_barcode_result.*
import kotlinx.android.synthetic.main.barcode_item.view.*

class BarcodeResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_result)
        setSupportActionBar(toolbar)
        showLatestBarcodeResult(BarcodeResultRepository.barcodeScanningResult)
    }


    private fun showLatestBarcodeResult(detectedBarcodes: BarcodeScanningResult?) {

        detectedBarcodes?.let {
            detectedBarcodes.barcodeItems.asSequence().map { item ->
                layoutInflater.inflate(R.layout.barcode_item, recognisedItems, false)?.also {
                    it.image.setImageBitmap(item.image)
                    it.barcodeFormat.text = item.barcodeFormat.name
                    it.docFormat.text = item.barcodeDocumentFormat?.documentFormat
                    it.recognisedData.text = printParsedFormat(item)
                }
            }.forEach { recognisedItems.addView(it) }

        }

    }

    private fun printParsedFormat(item: BarcodeItem): String {
        val barcodeDocumentFormat = item.barcodeDocumentFormat
            ?: return "" // for not supported by current barcode detector implementation

        val barcodesResult = StringBuilder()
        when (barcodeDocumentFormat) {
            is BoardingPassDocument -> {
                barcodesResult.append("\n")
                    .append("Boarding Pass Document").append("\n")
                    .append(barcodeDocumentFormat.name).append("\n")
                for (leg in barcodeDocumentFormat.legs) {
                    for (field in leg.fields) {
                        barcodesResult.append(field.type.name).append(": ").append(field.value)
                            .append("\n")
                    }
                }

            }
            is DEMedicalPlanDocument -> {
                barcodesResult.append("\n").append("DE Medical Plan Document").append("\n")

                barcodesResult.append("Doctor Fields:").append("\n")
                barcodeDocumentFormat.doctor.fields.forEach { field ->
                    barcodesResult.append(field.type.name).append(": ").append(field.value)
                        .append("\n")
                }

                barcodesResult.append("Patient Fields:").append("\n")
                barcodeDocumentFormat.patient.fields.forEach { field ->
                    barcodesResult.append(field.type.name).append(": ").append(field.value)
                        .append("\n")
                }

                barcodesResult.append("Medicine Fields:").append("\n")
                barcodeDocumentFormat.subheadings
                    .asSequence()
                    .flatMap { it.medicines.asSequence() }
                    .flatMap { it.fields.asSequence() }
                    .forEach {
                        barcodesResult.append(it.type.name).append(": ").append(it.value)
                            .append("\n")
                    }

            }
            is DisabilityCertificateDocument -> {
                barcodesResult.append("\n").append("Disability Certificate Document").append("\n")

                barcodeDocumentFormat.fields.forEach {
                    barcodesResult.append(it.type.name).append(": ").append(it.value).append("\n")
                }
            }
            is SEPADocument -> {
                barcodesResult.append("\n").append("Sepa Document").append("\n")

                barcodeDocumentFormat.fields.forEach {
                    barcodesResult.append(it.type.name).append(": ").append(it.value).append("\n")
                }
            }

            is VCardDocument -> {
                barcodesResult.append("\n").append("Vcard Document").append("\n")

                barcodeDocumentFormat.fields.forEach {
                    barcodesResult.append(it.type.name).append(": ").append(it.rawText).append("\n")
                }
            }

        }

        return barcodesResult.toString()
    }
}
