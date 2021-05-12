package com.ewellshop.collections

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.ewellshop.R
import com.ewellshop.helpers.Actions
import com.ewellshop.helpers.Item

class InventoryAdapter (context: Context, private val items: ArrayList<Item>) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): Any {
        return items[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var convertView = convertView
        val holder: Cell
        if (convertView == null || convertView.tag == null){
            holder = Cell()
            convertView = inflater.inflate(R.layout.cell_item, parent, false)
            holder.nameTV = convertView.findViewById(R.id.nameTV)
            holder.imageIV = convertView.findViewById(R.id.imageIV)
            convertView.tag = holder
        }else {
            holder = convertView.tag as Cell
        }

        val item = items[position]
        holder.nameTV!!.text = item.name

        Actions.showImage(item.image, holder.imageIV!!)

        return convertView!!
    }

    internal inner class Cell {
        var nameTV: TextView? = null
        var imageIV: ImageView? = null
    }

//    override fun isEnabled(position: Int): Boolean = false
}