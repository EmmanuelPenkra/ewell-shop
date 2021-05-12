package com.ewellshop.collections

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.ewellshop.Orders
import com.ewellshop.R
import com.ewellshop.helpers.Order

class OrdersAdapter (context: Context, private val items: ArrayList<Order>) : BaseAdapter() {

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
            convertView = inflater.inflate(R.layout.cell_order, parent, false)
//            holder.nameTV = convertView.findViewById(R.id.nameTV)
            convertView.tag = holder
        }else {
            holder = convertView.tag as Cell
        }

        val order = items[position]
//        settingHolder.nameTV!!.text = setting.name

        return convertView!!
    }

    internal inner class Cell {
//        var nameTV: TextView? = null
    }
}