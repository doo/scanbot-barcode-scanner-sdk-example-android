package io.scanbot.example.sdk.barcode.ui

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
import io.scanbot.example.sdk.barcode.R
import io.scanbot.example.sdk.barcode.model.BarcodeTypeRepository
import io.scanbot.sdk.SdkLicenseError
import io.scanbot.sdk.barcode.BarcodeItem
import io.scanbot.sdk.barcode.BarcodeScannerFrameHandler
import io.scanbot.sdk.barcode.BarcodeScannerResult
import io.scanbot.sdk.barcode.textWithExtension
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.FrameHandlerResult
import io.scanbot.sdk.camera.ScanbotCameraView

class BatchQRScanActivity : AppCompatActivity(), BarcodeScannerFrameHandler.ResultHandler {

    private lateinit var cameraView: ScanbotCameraView
    private lateinit var resultView: RecyclerView
    private lateinit var flash: View
    private var flashEnabled = false
    private var barcodeScannerFrameHandler: BarcodeScannerFrameHandler? = null
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

        val barcodeScanner = ScanbotBarcodeScannerSDK(this).createBarcodeScanner()

        barcodeScannerFrameHandler = BarcodeScannerFrameHandler.attach(
            cameraView,
            barcodeScanner
        )

        barcodeScannerFrameHandler?.setScanningInterval(1000)
        barcodeScannerFrameHandler?.addResultHandler(this)

        barcodeScanner.setConfigurations(
            barcodeFormats = BarcodeTypeRepository.selectedTypes.toList()
        )
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

    private fun handleSuccess(result: FrameHandlerResult.Success<BarcodeScannerResult?>) {
        result.value?.let {
            cameraView.post {
                resultAdapter.addBarcodeItems(it.barcodes)
            }
        }
    }

    override fun handle(result: FrameHandlerResult<BarcodeScannerResult?, SdkLicenseError>): Boolean {
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

class ResultAdapter(val layoutInflater: LayoutInflater) :
    RecyclerView.Adapter<BarcodeViewHolder>() {
    private val items: MutableList<BarcodeItem> = mutableListOf()

    fun addBarcodeItems(items: List<BarcodeItem>) {
        // lets check duplicates
        items.forEach { item ->
            var insertedCount = 0
            if (!this.items.any { it.textWithExtension == item.textWithExtension }) {
                this.items.add(0, item)
                insertedCount += 1
            }
            notifyItemRangeInserted(0, insertedCount)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeViewHolder {
        return BarcodeViewHolder(layoutInflater.inflate(R.layout.barcode_item, parent, false))
    }

    override fun onBindViewHolder(holder: BarcodeViewHolder, position: Int) {
        val item = items[position]
        holder.text.text = item.textWithExtension
        holder.barcodeType.text = item.format.name
        if (item.sourceImage == null) {
            holder.image.visibility = View.GONE
        } else {
            holder.image.visibility = View.VISIBLE
        }
        holder.image.setImageBitmap(item.sourceImage?.toBitmap())
    }

    override fun getItemCount(): Int = items.size
}
