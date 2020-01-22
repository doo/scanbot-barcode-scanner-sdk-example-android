package io.scanbot.example.sdk.barcode

import android.content.Intent
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
                    it.setOnClickListener {
                        val intent = Intent(this, DetailedItemDataActivity::class.java)
                        intent.putExtra(DetailedItemDataActivity.BARCODE_ITEM, item)
                        startActivity(intent)
                    }
                }
            }.forEach {

                recognisedItems.addView(it)
            }

        }

    }

}
