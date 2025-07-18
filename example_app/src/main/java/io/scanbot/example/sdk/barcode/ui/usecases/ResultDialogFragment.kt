package io.scanbot.example.sdk.barcode.ui.usecases

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.scanbot.example.sdk.barcode.R
import io.scanbot.sdk.barcode.BarcodeItem

class ResultDialogFragment : BottomSheetDialogFragment() {
    // In production app it is not recommended to use this way of handling a callback from a dialog.
    // This approach is not good as it may lead to memory leaks.
    var onDismissListener: (() -> Unit)? = null

    private lateinit var imageView: ImageView
    private lateinit var textView: TextView

    companion object {
        private const val ARG_BARCODE_ITEM = "arg_barcode_item"

        fun newInstance(barcodeItem: BarcodeItem): ResultDialogFragment {
            val fragment = ResultDialogFragment()
            val args = Bundle()
            args.putParcelable(ARG_BARCODE_ITEM, barcodeItem)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_result_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.image_view)
        textView = view.findViewById(R.id.text_view)

        view.findViewById<Button>(R.id.button_dismiss).setOnClickListener {
            dismiss()
        }

        arguments?.let { args ->
            args.getParcelable<BarcodeItem>(ARG_BARCODE_ITEM)?.let { barcodeItem ->
                imageView.setImageBitmap(barcodeItem.sourceImage?.toBitmap())
                textView.text = barcodeItem.text
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()
    }
}