package io.scanbot.example.sdk.barcode.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import io.scanbot.example.sdk.barcode.databinding.ActivityBarcodeResultBinding
import io.scanbot.example.sdk.barcode.databinding.BarcodeItemBinding
import io.scanbot.example.sdk.barcode.databinding.SnapImageItemBinding
import io.scanbot.example.sdk.barcode.model.BarcodeResultRepository
import io.scanbot.sdk.barcode.entity.BarcodeScanningResult
import java.io.File

class BarcodeResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBarcodeResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBarcodeResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        showSnapImageIfExists(
            BarcodeResultRepository.barcodeResultBundle?.previewPath
                ?: BarcodeResultRepository.barcodeResultBundle?.imagePath
        )

        showLatestBarcodeResult(BarcodeResultRepository.barcodeResultBundle?.barcodeScanningResult)
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

    private fun showLatestBarcodeResult(detectedBarcodes: BarcodeScanningResult?) {

        detectedBarcodes?.let {
            detectedBarcodes.barcodeItems.asSequence().map { item ->
                val itemViewBinding = BarcodeItemBinding.inflate(layoutInflater, binding.recognisedItems, false)
                item.image?.let { bitmap ->
                    itemViewBinding.image.setImageBitmap(bitmap)
                }
                itemViewBinding.barcodeFormat.text = item.barcodeFormat.name
                itemViewBinding.docFormat.text = item.formattedResult?.let {
                    it::class.java.simpleName
                } ?: "Unknown document"
                itemViewBinding.docText.text = item.textWithExtension
                itemViewBinding.root.setOnClickListener {
                    val intent = Intent(this, DetailedItemDataActivity::class.java)
                    BarcodeResultRepository.selectedBarcodeItem = item
                    startActivity(intent)
                }
                itemViewBinding.root
            }.forEach {
                binding.recognisedItems.addView(it)
            }
        }
    }
}
