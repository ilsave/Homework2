package com.example.gardenwater

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class AdapterAreas(
    var listAreas: List<Area>
): RecyclerView.Adapter<AdapterAreas.ItemsHolder>() {


    inner class ItemsHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.area_item, parent, false)
        return ItemsHolder(view)
    }

    override fun onBindViewHolder(holder: ItemsHolder, position: Int) {
        var currentItem = listAreas[position]
        holder.itemView.apply {
            findViewById<TextView>(R.id.tvAreaTitle).text = currentItem.name
            if (currentItem.leftCheck){
                findViewById<CheckBox>(R.id.checkBoxLeftArea).isChecked = false
            }
            if(position % 2 == 0){
                holder.itemView.findViewById<CheckBox>(R.id.checkBoxRightArea).isChecked = true
                findViewById<TextView>(R.id.tvAreaTitle).setTextColor(
                    ContextCompat.getColor(context,R.color.blue))
            }
            if(position == listAreas.size-1){
                findViewById<TextView>(R.id.tvSeparator).visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int  = listAreas.size
}