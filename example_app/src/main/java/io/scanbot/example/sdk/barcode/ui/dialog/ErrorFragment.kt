package io.scanbot.example.sdk.barcode.ui.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import io.scanbot.example.sdk.barcode.R


class ErrorFragment : androidx.fragment.app.DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(this.activity!!)

        val inflater = LayoutInflater.from(activity)

        val contentContainer = inflater.inflate(R.layout.fragment_expired_license_dialog, null, false)

        builder.setView(contentContainer)

        builder.setNegativeButton(getString(R.string.cancel_dialog_button)) { _, _ ->
        }

        builder.setPositiveButton(getString(R.string.get_license)) { _, _ ->
            run {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(SDK_LINK))
                activity?.startActivity(Intent.createChooser(intent, getString(R.string.choose_browser)))
                dismiss()
            }
        }

        val dialog = builder.create()

        dialog.setCanceledOnTouchOutside(true)

        return dialog
    }

    companion object {
        const val NAME = "ErrorFragment"
        const val SDK_LINK = "https://scanbot.io/en/sdk.html"

        @JvmStatic
        fun newInstance(): ErrorFragment {
            return ErrorFragment()
        }
    }
}