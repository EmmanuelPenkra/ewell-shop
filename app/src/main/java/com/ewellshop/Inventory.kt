package com.ewellshop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ewellshop.collections.InventoryAdapter
import com.ewellshop.helpers.Item
import com.ewellshop.sheets.InventorySheet
import kotlinx.android.synthetic.main.fragment_inventory.view.*

class Inventory : Fragment() {

    var contextX: Context? = null

    private var inventoryAdapter: InventoryAdapter? = null
    var inventory = ArrayList<Item>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_inventory, container, false)

        contextX = requireActivity()

        val newB = v.newB
        val inventoryGV = v.inventoryGV

//        inventory.add(Item())
//        inventory.add(Item())
//        inventory.add(Item())
//        inventory.add(Item())
//        inventory.add(Item())

        inventoryAdapter = InventoryAdapter(contextX!!, inventory)
        inventoryGV.adapter = inventoryAdapter
        inventoryGV.emptyView = v.findViewById(R.id.emptyLL)

        inventoryGV.setOnItemClickListener { parent, view, position, id ->
            InventorySheet(contextX!!).show(fragmentManager!!, "inventorySheet")
        }

        newB.setOnClickListener {
            addNewItem()
        }

        return v
    }

    private fun addNewItem(){
        startActivity(Intent(contextX, ItemsLookup::class.java))
    }
}