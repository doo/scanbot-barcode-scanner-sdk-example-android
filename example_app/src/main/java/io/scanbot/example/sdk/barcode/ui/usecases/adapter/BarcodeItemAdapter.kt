package io.scanbot.example.sdk.barcode.ui.usecases.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.scanbot.example.sdk.barcode.R
import io.scanbot.sdk.barcode.BarcodeItem
import io.scanbot.sdk.barcode.textWithExtension

class BarcodeItemAdapter : RecyclerView.Adapter<BarcodeItemAdapter.BarcodeViewHolder>() {
    private val items: MutableList<BarcodeItem> = mutableListOf()

    inner class BarcodeViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val image: ImageView by lazy { item.findViewById(R.id.image) }
        val barcodeType: TextView by lazy { item.findViewById(R.id.barcodeFormat) }
        val text: TextView by lazy { item.findViewById(R.id.docText) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeViewHolder {
        return BarcodeViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.barcode_item, parent, false))
    }

    override fun onBindViewHolder(holder: BarcodeViewHolder, position: Int) {
        val item = items.get(position)
        holder.text.text = item.textWithExtension
        holder.barcodeType.text = item.format.name
        holder.image.setImageBitmap(item.sourceImage?.toBitmap())
    }

    fun addBarcodeItems(items: List<BarcodeItem>) {
        // lets check duplicates
        items.forEach { item ->
            var insertedCount = 0
            if (this.items.none { it.textWithExtension == item.textWithExtension
                        && it.format == item.format }
            ) {
                this.items.add(0, item)
                insertedCount += 1
            }
            notifyItemRangeInserted(0, insertedCount)
        }
    }

    override fun getItemCount(): Int = items.size

    fun getItems(): List<BarcodeItem> = items
    fun setBarcodeItems(barcodeItems: List<BarcodeItem>) {
        this.items.clear()
        this.items.addAll(barcodeItems)
        notifyDataSetChanged()
    }
}
