package io.scanbot.example.sdk.barcode.ui.usecases

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.scanbot.example.sdk.barcode.R

class OptionAdapter(private val items: List<ViewType>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class TitleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title_text_view)
    }

    inner class OptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val option: TextView = view.findViewById(R.id.option_text_view)
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.main_item_title, parent, false)
                TitleViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.main_item_option, parent, false)
                OptionViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is ViewType.Header -> (holder as TitleViewHolder).title.text = item.title
            is ViewType.Option -> {
                (holder as OptionViewHolder).option.text = item.title
                holder.option.setOnClickListener {
                    val intent = Intent(it.context, item.useCase.activityClass.java)
                    it.context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
