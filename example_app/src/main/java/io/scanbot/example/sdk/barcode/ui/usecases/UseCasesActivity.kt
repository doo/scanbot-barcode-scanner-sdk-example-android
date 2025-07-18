package io.scanbot.example.sdk.barcode.ui.usecases

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.scanbot.example.sdk.barcode.R

class UseCasesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usecases)

        val items = listOf(
            ViewType.Header("Barcode Scanning Use Cases"),
            ViewType.Option(UseCase.SINGLE_BARCODE, "Scanning Single Barcodes"),
            ViewType.Option(UseCase.MULTIPLE_BARCODE, "Scanning Multiple Barcodes"),
            ViewType.Option(UseCase.BATCH_SCANNING, "Batch Scanning"),
            ViewType.Option(UseCase.TINY_BARCODE, "Scanning Tiny Barcodes"),
            ViewType.Option(UseCase.DISTANT_BARCODE, "Scanning Distant Barcodes"),
            ViewType.Option(UseCase.DETECTION_ON_THE_IMAGE, "Detecting Barcodes on Still Images"),
            ViewType.Header("Barcode AR Overlay Use Cases"),
            ViewType.Option(UseCase.AR_MULTI_SCAN, "AR-MultiScan"),
            ViewType.Option(UseCase.AR_SELECT_SCAN, "AR-SelectScan"),
            ViewType.Option(UseCase.AR_FIND_AND_PICK, "AR-FindAndPick"),
            ViewType.Option(UseCase.AR_SCAN_AND_COUNT, "AR-ScanAndCount"),
        )

        val adapter = OptionAdapter(items)
        val recyclerView = findViewById<RecyclerView>(R.id.main_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }
}
