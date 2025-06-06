package io.scanbot.example.sdk.barcode.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.scanbot.example.sdk.barcode.model.BarcodeTypeRepository
import io.scanbot.example.sdk.barcode.R
import io.scanbot.example.sdk.barcode.ui.adapter.BarcodeTypesAdapter.BarcodeTypesViewHolder
import io.scanbot.sdk.barcode.BarcodeFormats

class BarcodeTypesAdapter : RecyclerView.Adapter<BarcodeTypesViewHolder>() {

    private val values = BarcodeFormats.all

    class BarcodeTypesViewHolder(barcodeTypeView: View) : RecyclerView.ViewHolder(barcodeTypeView) {
        var barcodeTypeName: TextView
        var barcodeTypeChecker: CheckBox

        init {
            this.barcodeTypeName = barcodeTypeView.findViewById(R.id.barcode_type_name)
            this.barcodeTypeChecker = barcodeTypeView.findViewById(R.id.barcode_type_checker)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BarcodeTypesViewHolder {
        val barcodeTypeView = LayoutInflater.from(parent.context)
            .inflate(R.layout.barcode_type_view, parent, false)

        return BarcodeTypesViewHolder(barcodeTypeView)
    }

    override fun onBindViewHolder(holder: BarcodeTypesViewHolder, position: Int) {
        val barcodeFormat = values[position]
        holder.barcodeTypeName.text = barcodeFormat.name
        holder.barcodeTypeChecker.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                BarcodeTypeRepository.selectType(barcodeFormat)
            } else {
                BarcodeTypeRepository.deselectType(barcodeFormat)
            }
        }

        holder.barcodeTypeChecker.isChecked = BarcodeTypeRepository.selectedTypes.contains(barcodeFormat)
    }

    override fun getItemCount(): Int {
        return values.size
    }
}
