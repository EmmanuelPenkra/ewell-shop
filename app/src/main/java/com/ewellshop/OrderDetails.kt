package com.ewellshop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.ewellshop.collections.ItemsAdapter
import com.ewellshop.helpers.OrderItem
import kotlinx.android.synthetic.main.activity_order_details.*

class OrderDetails : AppCompatActivity() {

    private var itemsAdapter: ItemsAdapter? = null
    var items = ArrayList<OrderItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)

        items.add(OrderItem(1))
        items.add(OrderItem(1))
        items.add(OrderItem(1))
        items.add(OrderItem(1))
        items.add(OrderItem(1))

        itemsAdapter = ItemsAdapter(this, items)
        itemsLV.adapter = itemsAdapter
    }

    fun finishOrder(v: View){

    }

    fun goBack(v: View){
        onBackPressed()
    }
}