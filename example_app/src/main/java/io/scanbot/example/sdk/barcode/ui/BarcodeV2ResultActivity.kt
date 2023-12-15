package io.scanbot.example.sdk.barcode.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import io.scanbot.example.sdk.barcode.databinding.ActivityBarcodeResultBinding
import io.scanbot.example.sdk.barcode.databinding.BarcodeItemV2Binding
import io.scanbot.example.sdk.barcode.databinding.SnapImageItemBinding
import io.scanbot.example.sdk.barcode.model.BarcodeV2ResultRepository
import io.scanbot.sdk.ui_v2.barcode.configuration.BarcodeScannerResult
import java.io.File

class BarcodeV2ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBarcodeResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        showSnapImageIfExists(
            BarcodeV2ResultRepository.barcodeResultBundle?.previewPath
                ?: BarcodeV2ResultRepository.barcodeResultBundle?.imagePath
        )

        showLatestBarcodeResult(BarcodeV2ResultRepository.barcodeResultBundle?.barcodeScanningResult)
    }

    private fun showSnapImageIfExists(imagePath: String?) {
        imagePath?.let { imagePath ->
            binding.recognisedItems.addView(
                SnapImageItemBinding.inflate(layoutInflater, binding.recognisedItems, false).also {
                    Picasso.get().load(File(imagePath)).into(it.snapImage)
                }.root
            )
        }
    }

    private fun showLatestBarcodeResult(detectedBarcodes: BarcodeScannerResult?) {

        detectedBarcodes?.let {
            detectedBarcodes.items.asSequence().map { item ->
                val itemViewBinding = BarcodeItemV2Binding.inflate(layoutInflater, binding.recognisedItems, false)
                itemViewBinding.barcodeFormat.text = item.type.name
                itemViewBinding.docFormat.text = item.formattedResult?.let { it::class.java.simpleName } ?: "Unknown document"
                itemViewBinding.docText.text = item.textWithExtension
                itemViewBinding.root.setOnClickListener {
                    val intent = Intent(this, DetailedItemV2DataActivity::class.java)
                    BarcodeV2ResultRepository.selectedBarcodeItem = item
                    startActivity(intent)
                }
                itemViewBinding.root
            }.forEach {
                binding.recognisedItems.addView(it)
            }
        }
    }
}
