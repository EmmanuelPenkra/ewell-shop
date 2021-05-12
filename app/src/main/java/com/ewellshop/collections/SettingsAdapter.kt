package com.ewellshop.collections

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.view.isVisible
import com.ewellshop.R
import com.ewellshop.helpers.Order
import com.ewellshop.helpers.Setting

class SettingsAdapter (context: Context, private val items: ArrayList<Setting>) : BaseAdapter() {

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
            convertView = inflater.inflate(R.layout.cell_setting, parent, false)
            holder.mainTV = convertView.findViewById(R.id.mainTV)
            holder.subTV = convertView.findViewById(R.id.subTV)
            holder.dividerV = convertView.findViewById(R.id.dividerV)
            convertView.tag = holder
        }else {
            holder = convertView.tag as Cell
        }

        val setting = items[position]
        holder.mainTV!!.text = setting.main
        holder.subTV!!.text = setting.sub

        holder.dividerV!!.isVisible = setting.divider

        return convertView!!
    }

    internal inner class Cell {
        var mainTV: TextView? = null
        var subTV: TextView? = null
        var dividerV: View? = null
    }
}