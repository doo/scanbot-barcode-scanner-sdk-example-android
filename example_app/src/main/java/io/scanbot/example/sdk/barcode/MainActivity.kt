package io.scanbot.sdk.sdk_integration_barcode_scanner_sdk

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.scanbot.example.sdk.barcode.BarcodeTypesActivity
import io.scanbot.example.sdk.barcode.QRScanCameraViewActivity
import io.scanbot.example.sdk.barcode.R
import io.scanbot.sdk.barcode.entity.BarcodeScanningResult
import io.scanbot.sdk.ui.barcode_scanner.view.barcode.BarcodeScannerActivity
import io.scanbot.sdk.ui.view.barcode.BaseBarcodeScannerActivity
import io.scanbot.sdk.ui.view.barcode.configuration.BarcodeImageGenerationType
import io.scanbot.sdk.ui.view.barcode.configuration.BarcodeScannerConfiguration

class MainActivity : AppCompatActivity() {

    private val BARCODE_DEFAULT_UI_REQUEST_CODE = 910
    private val IMPORT_IMAGE_REQUEST_CODE = 911

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.qr_demo).setOnClickListener {
            val intent = Intent(applicationContext, QRScanCameraViewActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.rtu_ui).setOnClickListener {
            val barcodeCameraConfiguration = BarcodeScannerConfiguration()
            /*
            barcodeCameraConfiguration.setTopBarBackgroundColor(Color.parseColor("#00FFFF"))
            barcodeCameraConfiguration.setTopBarButtonsColor(Color.parseColor("#FF0000"))

            barcodeCameraConfiguration.setCancelButtonTitle("закончить")
            barcodeCameraConfiguration.setFinderTextHint("Помести код сюда")

            barcodeCameraConfiguration.setCameraOverlayColor(Color.parseColor("#80F0F000"))
            barcodeCameraConfiguration.setFinderLineColor(Color.parseColor("#00F0F0"))
            barcodeCameraConfiguration.setFinderHeight(800)
            barcodeCameraConfiguration.setFinderWidth(800)
            barcodeCameraConfiguration.setFinderLineWidth(10)
            barcodeCameraConfiguration.setFinderTextHintColor(Color.parseColor("#000000"))
            barcodeCameraConfiguration.setEnableCameraExplanationText("Дай пермишн!")
            barcodeCameraConfiguration.setEnableCameraButtonTitle("На пермишн")

            barcodeCameraConfiguration.setBarcodeFormatsFilter(arrayListOf(BarcodeFormat.ALL_FORMATS))
            barcodeCameraConfiguration.setFlashEnabled(false)
            */

            barcodeCameraConfiguration.setBarcodeImageGenerationType(BarcodeImageGenerationType.VIDEO_FRAME)
            val intent = BarcodeScannerActivity.newIntent(this@MainActivity, barcodeCameraConfiguration)
            startActivityForResult(intent, BARCODE_DEFAULT_UI_REQUEST_CODE)
        }

        findViewById<View>(R.id.settings).setOnClickListener {
            val intent =  Intent(this@MainActivity,BarcodeTypesActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == BARCODE_DEFAULT_UI_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val barcodeData = data!!.getParcelableExtra<BarcodeScanningResult>(BaseBarcodeScannerActivity.SCANNED_BARCODE_EXTRA)
            val imagePath = data!!.getStringExtra(BaseBarcodeScannerActivity.SCANNED_BARCODE_IMAGE_PATH_EXTRA)
            val previewPath = data!!.getStringExtra(BaseBarcodeScannerActivity.SCANNED_BARCODE_PREVIEW_FRAME_PATH_EXTRA)

            val barcodesTextResult = StringBuilder()
            barcodeData?.let {
                for (item in barcodeData.barcodeItems) {
                    barcodesTextResult.append(item.barcodeFormat.name + "\n" + item.text)
                            .append("\n")
                            .append("-------------------")
                            .append("\n")
                }
            }

            imagePath?.let {
                barcodesTextResult.append("\n")
                        .append("----CAPTURED IMAGE----")
                        .append("\n")
                        .append(it)
                        .append("\n")
                        .append("-------------------")
            }

            previewPath?.let {
                barcodesTextResult.append("\n")
                        .append("----VIDEO FRAME----")
                        .append("\n")
                        .append(it)
                        .append("\n")
                        .append("-------------------")
            }
            Toast.makeText(this@MainActivity,
                    barcodesTextResult.toString(), Toast.LENGTH_LONG).show()
        }
    }
}
