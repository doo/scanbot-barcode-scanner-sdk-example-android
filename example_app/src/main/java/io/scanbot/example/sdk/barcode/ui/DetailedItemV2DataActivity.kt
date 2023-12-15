package io.scanbot.example.sdk.barcode.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.scanbot.example.sdk.barcode.databinding.ActivityDetailedItemDataBinding
import io.scanbot.example.sdk.barcode.model.BarcodeV2ResultRepository
import io.scanbot.sdk.ui_v2.barcode.configuration.AAMVADocumentFormat
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeItem
import io.scanbot.sdk.ui_v2.barcode.configuration.BoardingPassDocumentFormat
import io.scanbot.sdk.ui_v2.barcode.configuration.GS1DocumentFormat
import io.scanbot.sdk.ui_v2.barcode.configuration.IDCardPDF417DocumentFormat
import io.scanbot.sdk.ui_v2.barcode.configuration.MedicalCertificateDocumentFormat
import io.scanbot.sdk.ui_v2.barcode.configuration.MedicalPlanDocumentFormat
import io.scanbot.sdk.ui_v2.barcode.configuration.SEPADocumentFormat
import io.scanbot.sdk.ui_v2.barcode.configuration.SwissQRCodeDocumentFormat
import io.scanbot.sdk.ui_v2.barcode.configuration.VCardDocumentFormat

class DetailedItemV2DataActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDetailedItemDataBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        BarcodeV2ResultRepository.selectedBarcodeItem?.let { item ->
            binding.docFormat.text = item.formattedResult?.let {
                it::class.java.simpleName
            } ?: "Unknown document"
            binding.description.text = printParsedFormat(item)
        }
    }

    private fun printParsedFormat(item: BarcodeItem): String {
        val formattedResult = item.formattedResult
                ?: return "${item.textWithExtension}\n\nBinary data:\n${item.rawBytes.toByteArray().toHexString()}" // for not supported by current barcode detector implementation

        val barcodesResult = StringBuilder()
        when (formattedResult) {
            is AAMVADocumentFormat -> {
                barcodesResult.append("\n")
                        .append("AAMVA Document\n")
                        .append(formattedResult.aamvaVersionNumber).append("\n")
                        .append(formattedResult.issuerIdentificationNumber).append("\n")
                        .append(formattedResult.jurisdictionVersionNumber).append("\n")
                formattedResult.subfiles.forEach { subfile ->
                    for (field in subfile.fields) {
                        barcodesResult.append(field.type.name).append(": ").append(field.value)
                                .append("\n")
                    }
                }
            }
            is BoardingPassDocumentFormat -> {
                barcodesResult.append("\n")
                        .append("Boarding Pass Document\n")
                        .append("${formattedResult.name}\n")
                formattedResult.legs?.forEach { leg ->
                    leg.fields.forEach {
                        barcodesResult.append("${it.type.name}:${it.value}\n")
                    }
                }
            }
            is MedicalPlanDocumentFormat -> {
                barcodesResult.append("\nDE Medical Plan Document\n")

                barcodesResult.append("Doctor Fields:\n")
                formattedResult.doctor.fields.forEach {
                    barcodesResult.append("${it.type.name}: ${it.value}\n")
                }
                barcodesResult.append("\n")

                barcodesResult.append("Patient Fields:\n")
                formattedResult.patient.fields.forEach {
                    barcodesResult.append("${it.type.name}: ${it.value}\n")
                }
                barcodesResult.append("\n")

                barcodesResult.append("Medicine Fields:").append("\n")
                formattedResult.subheadings
                        .asSequence()
                        .flatMap {
                            barcodesResult.append("\n")
                            it.medicines.asSequence()
                        }
                        .flatMap { it.fields.asSequence() }
                        .forEach {
                            barcodesResult.append("${it.type.name}: ${it.value}\n")
                        }
            }
            is MedicalCertificateDocumentFormat -> {
                barcodesResult.append("\nMedical Certificate Document\n")

                formattedResult.fields.forEach {
                    barcodesResult.append("${it.type?.name}: ${it.value}\n")
                }
            }
            is IDCardPDF417DocumentFormat -> {
                barcodesResult.append("\nId Card PDF417\n")

                formattedResult.fields.forEach {
                    barcodesResult.append("${it.type?.name}: ${it.value}\n")
                }
            }
            is SEPADocumentFormat -> {
                barcodesResult.append("\nSEPA Document\n")

                formattedResult.fields.forEach {
                    barcodesResult.append("${it.type.name}: ${it.value}\n")
                }
            }
            is SwissQRCodeDocumentFormat -> {
                barcodesResult.append("\nSwiss QR Document\n")
                barcodesResult.append("Version: ${formattedResult.version.name}\n")

                formattedResult.fields.forEach {
                    barcodesResult.append("${it.type.name}: ${it.value}\n")
                }
            }
            is VCardDocumentFormat -> {
                barcodesResult.append("\nVcard Document\n")

                formattedResult.fields.forEach {
                    barcodesResult.append("${it.type.name}: ${it.rawText}\n")
                }
            }
            is GS1DocumentFormat -> {
                barcodesResult.append("\nGs1 Document\n")

                formattedResult.fields.forEach {
                    barcodesResult.append("${it.fieldDescription}: ${it.rawValue}\n")
                }
            }
        }
        return barcodesResult.toString()
    }

    private fun ByteArray.toHexString() = this.joinToString("") { String.format("%02X", it.toInt() and 0xFF) }
}
