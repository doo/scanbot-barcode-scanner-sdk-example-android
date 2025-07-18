package io.scanbot.example.sdk.barcode.ui.usecases

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.scanbot.sdk.barcode.BarcodeScannerResult

object ExampleUtils {
    fun showLicenseExpiredToastAndExit(activity: Activity) {
        activity.runOnUiThread {
            Toast.makeText(
                activity, "License has expired!", Toast.LENGTH_LONG
            ).show()
            activity.finish()
        }
    }

    fun showBarcodeResult(activity: AppCompatActivity, barcodeScanningResult: BarcodeScannerResult, onDismissListener: () -> Unit) {
        val resultDialogFragment = ResultDialogFragment.newInstance(barcodeScanningResult.barcodes.first())
        resultDialogFragment.show(activity.supportFragmentManager, "result")
        resultDialogFragment.onDismissListener = {
            onDismissListener()
        }
    }

    fun openBrowser(context: Context, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }
}