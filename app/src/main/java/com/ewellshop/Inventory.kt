package com.ewellshop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.ewellshop.collections.InventoryAdapter
import com.ewellshop.helpers.*
import com.ewellshop.helpers.Constants.DOMAIN
import com.ewellshop.helpers.Item
import com.ewellshop.popups.ImagePopup
import com.ewellshop.sheets.InventorySheet
import kotlinx.android.synthetic.main.fragment_inventory.*
import kotlinx.android.synthetic.main.fragment_inventory.view.*

class Inventory : Fragment() {

    val actions = Actions()
    var contextX: Context? = null

    private var inventoryAdapter: InventoryAdapter? = null
    var inventory = ArrayList<Item>()
    private var searchResults = ArrayList<Item>()

    var query = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_inventory, container, false)

        contextX = requireActivity()

        val newB = v.newB
        val inventoryGV = v.inventoryGV

        inventoryAdapter = InventoryAdapter(contextX!!, searchResults)
        inventoryGV.adapter = inventoryAdapter
        inventoryGV.emptyView = v.findViewById(R.id.emptyLL)

        inventoryGV.setOnItemClickListener { _, _, position, _ ->
            itemClicked(position)
        }

        newB.setOnClickListener {
            addNewItem()
        }

        v.searchET.addTextChangedListener {
            // SEARCH IN INVENTORY
            query = it.toString().trim()
            search()
        }

        return v
    }

    private fun search(){
        val tempResults = ArrayList<Item>()
        tempResults.addAll(searchResults)

        searchResults.clear()
        searchResults.addAll(inventory.filter { i -> i.name.contains(query, true) } as
                ArrayList<Item>)

        if (!tempResults.containsAll(searchResults) || !searchResults.containsAll(tempResults))
            inventoryAdapter?.notifyDataSetChanged()
    }

    private fun itemClicked(position: Int){
        val item = searchResults[position]
        InventorySheet(item, {
            ImagePopup(it).show(fragmentManager!!, "imagePopup")
        }) { sheet ->
            Internet(contextX!!, DOMAIN + "v1/change-item-state", hashMapOf(
                "itemID" to item.id,
                "state" to item.state.value
            ), true){
                // CHANGED
                Actions.showAlert(contextX!!, "Successful", it.getString("message"), "Done")

                // UPDATE INVENTORY
                inventory[position].state = XBusinessInventory.values()[it.getInt("newState")]
                search()
                sheet.dismiss()
            }
        }.show(fragmentManager!!, "inventorySheet")
    }

    private fun addNewItem(){
        startActivity(Intent(contextX, ItemsLookup::class.java))
    }

    override fun onResume() {
        super.onResume()

        fetchInventory()
    }

    private fun fetchInventory(){
        Internet(contextX!!, DOMAIN + "v1/inventory", hashMapOf(
            "businessID" to actions.getInfo("businessID", DataType.INTEGER)!!
        ), false, null, {
            // SHOW NO INTERNET ERROR MESSAGE
            emptyTV.text = "Check your internet connection"
        }){
            inventory.clear()

            for (item in it.getJSONArray("inventory")){
                inventory.add(Item(item.getInt("id"), item.getString("image"), item.getString
                    ("name"), item.getString("description"), item.getDouble("price"), ItemCondition.values()[item.getInt
                    ("condition")], XBusinessInventory.values()[item.getInt("state")]))
            }

            search()

            inventoryAdapter?.notifyDataSetChanged()
        }
    }
}