package io.scanbot.example.sdk.barcode.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.scanbot.barcodescanner.entity.AAMVA
import io.scanbot.barcodescanner.entity.BarcodeDocumentLibrary.wrap
import io.scanbot.barcodescanner.entity.BoardingPass
import io.scanbot.barcodescanner.entity.DEMedicalPlan
import io.scanbot.barcodescanner.entity.GS1
import io.scanbot.barcodescanner.entity.IDCardPDF417
import io.scanbot.barcodescanner.entity.MedicalCertificate
import io.scanbot.barcodescanner.entity.SEPA
import io.scanbot.barcodescanner.entity.SwissQR
import io.scanbot.barcodescanner.entity.VCard
import io.scanbot.example.sdk.barcode.databinding.ActivityDetailedItemDataBinding
import io.scanbot.example.sdk.barcode.model.BarcodeResultRepository
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeItem

class DetailedItemDataActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDetailedItemDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        BarcodeResultRepository.selectedBarcodeItem?.let { item ->
            binding.docFormat.text = item.parsedDocument?.type?.fullName ?: ""
            binding.description.text = printParsedFormat(item)
        }
    }

    private fun printParsedFormat(item: BarcodeItem): String {
        val formattedResult = item.parsedDocument?.wrap()
                ?: return "${item.textWithExtension}\n\nBinary data:\n${item.rawBytes.toHexString()}" // for not supported by current barcode detector implementation

        val barcodesResult = StringBuilder()
        when (formattedResult) {
            is BoardingPass -> {
                barcodesResult.append("\n")
                    .append("Boarding Pass Document\n")
                formattedResult.document.fields.forEach { field ->
                    barcodesResult.append("${field.type.name}:${field.value?.text}\n")
                }
                formattedResult.document.children.forEach { leg ->
                    leg.fields.forEach {
                        barcodesResult.append("${it.type.name}:${it.value?.text}\n")
                    }
                }
            }
            is SwissQR -> {
                barcodesResult.append("\nSwiss QR Document\n")

                formattedResult.document.fields.forEach {
                    barcodesResult.append("${it.type.name}: ${it.value?.text}\n")
                }
            }
            is DEMedicalPlan -> {
                barcodesResult.append("\nDE Medical Plan Document\n")

                formattedResult.document.fields.forEach { field ->
                    barcodesResult.append(field.type.name).append(": ").append(field.value?.text)
                        .append("\n")
                }
                formattedResult.document.children.forEach { subfile ->
                    for (field in subfile.fields) {
                        barcodesResult.append(field.type.name).append(": ").append(field.value?.text)
                            .append("\n")
                    }
                }
            }
            is IDCardPDF417 -> {
                barcodesResult.append("\nId Card PDF417\n")

                formattedResult.document.fields.forEach {
                    barcodesResult.append("${it.type?.name}: ${it.value?.text}\n")
                }
            }
            is GS1 -> {
                barcodesResult.append("\nGs1 Document\n")

                formattedResult.document.fields.forEach {
                    barcodesResult.append("${it.type.name}: ${it.value?.text}\n")
                }
            }
            is SEPA -> {
                barcodesResult.append("\nSEPA Document\n")

                formattedResult.document.fields.forEach {
                    barcodesResult.append("${it.type.name}: ${it.value?.text}\n")
                }
            }
            is MedicalCertificate -> {
                barcodesResult.append("\nMedical Certificate Document\n")

                formattedResult.document.fields.forEach {
                    barcodesResult.append("${it.type?.name}: ${it.value?.text}\n")
                }
            }
            is VCard -> {
                barcodesResult.append("\nVcard Document\n")

                formattedResult.document.fields.forEach {
                    barcodesResult.append("${it.type.name}: ${it.value?.text}\n")
                }
            }
            is AAMVA -> {
                barcodesResult.append("\n")
                    .append("AAMVA Document\n")
                formattedResult.document.fields.forEach { field ->
                    barcodesResult.append(field.type.name).append(": ").append(field.value?.text)
                        .append("\n")
                }
                formattedResult.document.children.forEach { subfile ->
                    for (field in subfile.fields) {
                        barcodesResult.append(field.type.name).append(": ").append(field.value?.text)
                            .append("\n")
                    }
                }
            }
            else -> throw AssertionError("Unsupported root document type")

        }
        return barcodesResult.toString()
    }

    private fun ByteArray.toHexString() = this.joinToString("") { String.format("%02X", it.toInt() and 0xFF) }
}
