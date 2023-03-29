package io.scanbot.example.sdk.barcode

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.scanbot.example.sdk.barcode.model.BarcodeTypeRepository
import io.scanbot.sdk.SdkLicenseError
import io.scanbot.sdk.barcode.BarcodeDetectorFrameHandler
import io.scanbot.sdk.barcode.entity.BarcodeItem
import io.scanbot.sdk.barcode.entity.BarcodeScanningResult
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.FrameHandlerResult
import io.scanbot.sdk.camera.ScanbotCameraView

class BatchQRScanActivity : AppCompatActivity(), BarcodeDetectorFrameHandler.ResultHandler {

    private lateinit var cameraView: ScanbotCameraView
    private lateinit var resultView: RecyclerView
    private lateinit var flash: View
    private var flashEnabled = false
    private var barcodeDetectorFrameHandler: BarcodeDetectorFrameHandler? = null
    private val resultAdapter by lazy { ResultAdapter(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_batch_qr_camera_view)

        cameraView = findViewById(R.id.camera)
        flash = findViewById(R.id.flash)
        flash.setOnClickListener {
            flashEnabled = !flashEnabled
            cameraView.useFlash(flashEnabled)
        }
        resultView = findViewById(R.id.resultsList)
        resultView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        resultView.adapter = resultAdapter

        cameraView.setCameraOpenCallback {
            cameraView.postDelayed({
                cameraView.useFlash(flashEnabled)
                cameraView.continuousFocus()
            }, 300)
        }

        val barcodeDetector = ScanbotBarcodeScannerSDK(this).createBarcodeDetector()

        barcodeDetectorFrameHandler = BarcodeDetectorFrameHandler.attach(
            cameraView,
            barcodeDetector
        )

        barcodeDetectorFrameHandler?.setDetectionInterval(0)
        barcodeDetectorFrameHandler?.addResultHandler(this)

        barcodeDetector.modifyConfig {
            setSaveCameraPreviewFrame(false)
            setBarcodeFormats(BarcodeTypeRepository.selectedTypes.toList())
        }
    }

    override fun onResume() {
        super.onResume()
        cameraView.onResume()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Use onActivityResult to handle permission rejection
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION_CODE
            )
        }
    }

    override fun onPause() {
        super.onPause()
        cameraView.onPause()
    }

    private fun handleSuccess(result: FrameHandlerResult.Success<BarcodeScanningResult?>) {
        result.value?.let {
            cameraView.post {
                resultAdapter.processBarcodeItemsBatch(it.barcodeItems)
            }
        }
    }

    override fun handle(result: FrameHandlerResult<BarcodeScanningResult?, SdkLicenseError>): Boolean {
        if (result is FrameHandlerResult.Success) {
            handleSuccess(result)
        } else {
            cameraView.post {
                Toast.makeText(
                    this,
                    "License has expired!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        return false
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 200
    }
}

class BarcodeViewHolder(item: View) : RecyclerView.ViewHolder(item) {
    val image: ImageView by lazy { item.findViewById(R.id.image) }
    val barcodeType: TextView by lazy { item.findViewById(R.id.barcodeFormat) }
    val text: TextView by lazy { item.findViewById(R.id.docText) }
}

class ResultAdapter(private val layoutInflater: LayoutInflater) :
    RecyclerView.Adapter<BarcodeViewHolder>() {
    // barcode item -> count
    private val items: MutableMap<BarcodeItem, Int> = linkedMapOf()
    // value -> time to live
    private var recentBarcodesFilter: MutableMap<String, Long> = linkedMapOf()

    fun processBarcodeItemsBatch(items: List<BarcodeItem>) {
        processFilteredBatch(items).forEach { item ->
            val currentItem = this.items.keys.firstOrNull() { existing -> existing.textWithExtension == item.textWithExtension }
            if (currentItem == null) {
                this.items[item] = 1
            } else {
                val currentItemCount = this.items[currentItem]
                currentItemCount?.let {
                    this.items[currentItem] = currentItemCount + 1
                }
            }
        }
        notifyDataSetChanged()
    }

    private fun processFilteredBatch(items: List<BarcodeItem>): List<BarcodeItem> {
        recentBarcodesFilter.filter {
            it.value < System.currentTimeMillis()
        }.forEach {
            recentBarcodesFilter.remove(it.key)
        }
        val result = items.filterNot {
            recentBarcodesFilter.containsKey(it.textWithExtension)
        }
        items.forEach {
            recentBarcodesFilter[it.textWithExtension] = System.currentTimeMillis() + 1000
        }
        return result
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeViewHolder {
        return BarcodeViewHolder(layoutInflater.inflate(R.layout.barcode_item, parent, false))
    }

    override fun onBindViewHolder(holder: BarcodeViewHolder, position: Int) {
        val itemsAsList = items.toList().reversed()
        val barcodeToCount = itemsAsList[position]
        val item = barcodeToCount.first
        holder.text.text = "(x${barcodeToCount.second}) ${item.textWithExtension}"
        holder.barcodeType.text = item.barcodeFormat.name
        holder.image.setImageBitmap(item.image)
    }

    override fun getItemCount(): Int = items.size

}
