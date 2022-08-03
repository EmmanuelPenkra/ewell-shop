package com.ewellshop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import com.ewellshop.collections.InventoryAdapter
import com.ewellshop.helpers.*
import com.ewellshop.helpers.Constants.DOMAIN
import com.ewellshop.helpers.Item
import kotlinx.android.synthetic.main.activity_items_lookup.*

class ItemsLookup : AppCompatActivity() {

    private val ITEM_ADDED = 100

    private var inventoryAdapter: InventoryAdapter? = null
    private var inventory = ArrayList<Item>()
    private var searchResults = ArrayList<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_items_lookup)

        inventoryAdapter = InventoryAdapter(this, searchResults)
        inventoryGV.adapter = inventoryAdapter

        inventoryGV.setOnItemClickListener { _, _, position, _ ->
            startActivityForResult(Intent(this, com.ewellshop.Item::class.java).apply {
                putExtra("item", searchResults[position])
                putExtra("isUpdate", false)
            }, ITEM_ADDED)
        }

        searchET.addTextChangedListener {
            // SEARCH IN INVENTORY
            val tempResults = ArrayList<Item>()
            tempResults.addAll(searchResults)

            val query = it.toString().trim()
            searchResults.clear()
            searchResults.addAll(inventory.filter { i -> i.name.contains(query, true) } as
                    ArrayList<Item>)

            if (!tempResults.containsAll(searchResults) || !searchResults.containsAll(tempResults))
                inventoryAdapter?.notifyDataSetChanged()
        }

        getEwellStock()
    }

    private fun getEwellStock(){
        Internet(this, DOMAIN + "v1/es-stock", null, true){
            for (item in it.getJSONArray("stock"))
                inventory.add(
                    Item(item.getInt("id"), item.getString("image"), item.getString
                        ("name"), item.getString("description"), 0.0, null, XBusinessInventory
                        .AVAILABLE)
                )

            searchResults.addAll(inventory)
            inventoryAdapter?.notifyDataSetChanged()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) data?.let {
            when (requestCode){
                ITEM_ADDED -> finish()
            }
        }
    }

    fun goBack(v: View){
        onBackPressed()
    }
}