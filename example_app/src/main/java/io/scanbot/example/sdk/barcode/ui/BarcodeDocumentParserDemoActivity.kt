package io.scanbot.example.sdk.barcode.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.scanbot.example.sdk.barcode.R
import io.scanbot.example.sdk.barcode.ui.util.applyEdgeToEdge
import io.scanbot.sdk.barcode.document.BarcodeDocumentParser
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK

class BarcodeDocumentParserDemoActivity : AppCompatActivity() {

    private lateinit var barcodeDocumentParser: BarcodeDocumentParser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_barcode_document_parser)
        applyEdgeToEdge(this.findViewById(R.id.root_view))

        barcodeDocumentParser = ScanbotBarcodeScannerSDK(this).createBarcodeDocumentParser()
        val inputView = findViewById<TextView>(R.id.barcode_document_input)
        val outputView = findViewById<TextView>(R.id.barcode_document_output)
        findViewById<Button>(R.id.barcode_document_button).setOnClickListener {
            val input = inputView.text.toString()
            val parseDocument = barcodeDocumentParser.parseDocument(input)
            outputView.text = parseDocument.toString()
        }
    }
}
