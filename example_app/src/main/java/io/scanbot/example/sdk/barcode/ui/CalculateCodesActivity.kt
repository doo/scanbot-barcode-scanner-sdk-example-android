package io.scanbot.example.sdk.barcode.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.scanbot.example.sdk.barcode.R
import io.scanbot.example.sdk.barcode.model.BarcodeTypeRepository
import io.scanbot.sdk.barcode.ScanbotBarcodeDetector
import io.scanbot.sdk.barcode.entity.BarcodeItem
import io.scanbot.sdk.barcode.entity.BarcodeMetadataKey
import io.scanbot.sdk.barcode.entity.BarcodeScanningResult
import io.scanbot.sdk.barcode.ui.BarcodePolygonsView
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK
import io.scanbot.sdk.camera.*
import io.scanbot.sdk.ui.camera.ScanbotCameraXView

class CalculateCodesActivity : AppCompatActivity() {

    private lateinit var cameraView: ScanbotCameraXView
    private lateinit var resultView: RecyclerView
    private lateinit var polygonsView: BarcodePolygonsView
    private lateinit var barcodeDetector: ScanbotBarcodeDetector
    private val resultAdapter by lazy { ResultAdapter(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_codes_camera_view)

        cameraView = findViewById(R.id.camera)

        cameraView.setPreviewMode(CameraPreviewMode.FIT_IN)
        cameraView.setPhysicalZoom(1.2f)
        cameraView.setForceMaxSnappingSize(true)

        cameraView.addPictureCallback(object : PictureCallback() {
            override fun onPictureTaken(image: ByteArray, captureInfo: CaptureInfo) {
                processPictureTaken(image, captureInfo.imageOrientation)
            }
        })
        findViewById<View>(R.id.count_button).setOnClickListener {
            cameraView.takePicture(true)
        }
        resultView = findViewById(R.id.resultsList)
        resultView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        resultView.adapter = resultAdapter

        polygonsView = findViewById(R.id.polygons_view)

        // As we are not interested in a view under the polygon, we need clean the factories
        polygonsView.barcodeItemViewFactory = null
        polygonsView.barcodeItemViewBinder = null
        polygonsView.setCornerRadius(10)
        polygonsView.setStrokeColor(Color.parseColor("#FF02CEA6"))

        cameraView.setCameraOpenCallback {
            cameraView.postDelayed({
                cameraView.setPhysicalZoom(1.2f)
            }, 300)
        }

        barcodeDetector = ScanbotBarcodeScannerSDK(this).createBarcodeDetector()
        barcodeDetector.modifyConfig {
            setSaveCameraPreviewFrame(false)
            setBarcodeFormats(BarcodeTypeRepository.selectedTypes.toList())
        }
    }

    private fun processPictureTaken(image: ByteArray, imageOrientation: Int) {
        cameraView.post {
            cameraView.stopPreview()
        }
        val detectFromJpeg = barcodeDetector.detectFromJpeg(image, imageOrientation)
        val options = BitmapFactory.Options()
        BitmapFactory.decodeByteArray(image, 0, image.size, options)
        options.inJustDecodeBounds = true
        cameraView.post {
            // this part is required to let the Polygon view the size of the image
            polygonsView.frameHandler.handleFrame(
                FrameHandler.Frame(
                    ByteArray(0),
                    options.outWidth,
                    options.outHeight,
                    imageOrientation,
                    null,
                    null,
                    0,
                    0
                )
            )
            val items = detectFromJpeg?.barcodeItems ?: listOf()
            resultAdapter.processBarcodeItemsMultiple(items)
            val metadata = hashMapOf<BarcodeMetadataKey, String>()

            // this way we pass the barcodes list into the Polygon view
            polygonsView.barcodesResultHandler.handle(
                FrameHandlerResult.Success(
                    BarcodeScanningResult(
                        items.mapIndexed { index, barcode ->
                            BarcodeItem(
                                // This "hack" is required as the Polygon view can't work with duplicates by default
                                barcode.textWithExtension + "$index",
                                barcode.rawBytes,
                                barcode.numBits,
                                barcode.resultPoints,
                                barcode.barcodeFormat,
                                null,
                                null,
                                null,
                                metadata
                            )
                        }, System.currentTimeMillis()
                    )
                )
            )

            // We start the preview once again in 2 seconds and clear the Polygon view
            cameraView.postDelayed({
                cameraView.startPreview()
                polygonsView.barcodesResultHandler.handle(
                    FrameHandlerResult.Success(
                        BarcodeScanningResult(
                            listOf(),
                            System.currentTimeMillis()
                        )
                    )
                )
            }, 2000)
        }
    }

    override fun onResume() {
        super.onResume()
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

    fun processBarcodeItemsMultiple(items: List<BarcodeItem>) {
        this.items.clear()
        items.forEach { item ->
            val currentItem = this.items.keys.firstOrNull { existing -> existing.textWithExtension == item.textWithExtension }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeViewHolder {
        return BarcodeViewHolder(layoutInflater.inflate(R.layout.barcode_item_small, parent, false))
    }

    override fun onBindViewHolder(holder: BarcodeViewHolder, position: Int) {
        val itemsAsList = items.toList().reversed()
        val barcodeToCount = itemsAsList[position]
        val item = barcodeToCount.first
        // Displaying the count of the barcodes
        holder.text.text = "(x${barcodeToCount.second}) ${item.textWithExtension}"
        holder.barcodeType.text = item.barcodeFormat.name
        holder.image.setImageBitmap(item.image)
    }

    override fun getItemCount(): Int = items.size

}
