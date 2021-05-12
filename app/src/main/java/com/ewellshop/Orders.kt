package com.ewellshop

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import com.ewellshop.collections.OrdersAdapter
import com.ewellshop.helpers.Actions
import com.ewellshop.helpers.Order
import kotlinx.android.synthetic.main.fragment_orders.view.*

class Orders : Fragment() {

    var contextX: Context? = null

    private var ordersAdapter: OrdersAdapter? = null
    private var orders = ArrayList<Order>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_orders, container, false)

        contextX = requireActivity()

        val ordersLV = v.ordersLV
        val fulfilB = v.fulfilB

//        orders.add(Order(1))
//        orders.add(Order(1))
//        orders.add(Order(1))
//        orders.add(Order(1))
//        orders.add(Order(1))

        ordersAdapter = OrdersAdapter(contextX!!, orders)
        ordersLV.adapter = ordersAdapter

        ordersLV.setOnItemClickListener { parent, view, position, id ->
            startActivity(Intent(contextX, OrderDetails::class.java).apply {

            })
        }

        fulfilB.setOnClickListener {
            fulfilOrder()
        }

        return v
    }

    fun fulfilOrder() {
        val editText = EditText(contextX).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            hint = "6-digit Order No."
            filters = arrayOf<InputFilter>(InputFilter.LengthFilter(6))
            setBackgroundColor(resources.getColor(R.color.background))
            typeface = ResourcesCompat.getFont(contextX!!, R.font.pt_sans)
        }
        AlertDialog.Builder(contextX).apply {
            setTitle("Fulfil an Order")
            setMessage("Ask customer for a 6-digit Order Number and provide below\n")
            setView(editText)
            setPositiveButton("Continue") { _, _ ->
                //START THE ORDER PROCESS
                checkOrders(editText.text.toString())
            }
            setNegativeButton("Cancel", null)
        }.create().run {
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    fun checkOrders(orderNo: String){
        Actions.showAlert(contextX!!, "Verify with ID", "Ask Customer to provide " +
                "an ID and verify that their name is\n\nEmmanuel Gyekye Atta-Penkra","Verified", {_, _ ->
            startActivity(Intent(contextX, OrderDetails::class.java))
        }, true, cancellable = false)
    }

    override fun onResume() {
        super.onResume()

        orders.add(Order(1))
        orders.add(Order(1))
        orders.add(Order(1))
        orders.add(Order(1))
        orders.add(Order(1))
        ordersAdapter?.notifyDataSetChanged()
    }
}