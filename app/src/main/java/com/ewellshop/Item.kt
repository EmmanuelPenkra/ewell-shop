package com.ewellshop

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.ewellshop.helpers.*
import com.ewellshop.helpers.Constants.DOMAIN
import com.ewellshop.helpers.Constants.TAG
import com.ewellshop.helpers.Item
import kotlinx.android.synthetic.main.activity_item.*
import kotlinx.android.synthetic.main.activity_pin.*
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat

class Item : AppCompatActivity() {

    val actions = Actions()

    var item: Item? = null
    var isUpdate: Boolean? = null

    var price = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        updateB.setOnClickListener {
            // TODO: UPDATE OR ADD ITEM
            updateOrAddItem()
        }

        conditionCV.setOnClickListener {
            selectCondition()
        }

        setupFields()
        getExtras()
    }

    private fun updateOrAddItem(){
        val item = item!!

        if (item.condition == null){
            Actions.showAlert(this, null, "Select Item's Condition")
            return
        }

        Actions.showAlert(this, null, "Confirm ${if (isUpdate!!) "Update" else "Addition"} " +
                "of ${item.name}", "Confirm", { _, _ ->
            val params = hashMapOf (
                "id" to item.id,
                "name" to nameET.text,
                "description" to descriptionET.text,
                "price" to price.replace(",", ""),
                "stock" to stockET.text,
                "condition" to item.condition!!.value,
                "businessID" to actions.getInfo("businessID", DataType.INTEGER)!!
//                "employeeID" to actions.getInfo("employeeID", DataType.INTEGER)!!
            )

            Internet(this, DOMAIN + "v1/" + (if (isUpdate!!) "update-item" else "add-item"), params,
                true){
                setResult(RESULT_OK)
                finish()
            }
        }, true)
    }

    private fun setupFields(){
        priceET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != price) {
                    val cleanString: String = s!!.replace("""[$,.]""".toRegex(), "")
                    val parsed = cleanString.toDoubleOrNull() ?: return

                    priceET.removeTextChangedListener(this)

                    val formatter = NumberFormat.getCurrencyInstance() as DecimalFormat
                    val symbols: DecimalFormatSymbols = formatter.decimalFormatSymbols
                    symbols.currencySymbol = "" // Don't use null.

                    formatter.decimalFormatSymbols = symbols
                    val formatted = formatter.format((parsed / 100))

                    price = formatted
                    priceET.setText(formatted)
                    priceET.setSelection(formatted.length)

                    priceET.addTextChangedListener(this)
                }
            }
        })
    }

    private fun selectCondition(){
        Actions.showAlert(this, "Choose Condition", null, ArrayList<String>().apply {
            ItemCondition.values().forEach {
                add(it.name.formatEnum().capitalizeWords())
            }
        }){_, which ->
            val condition = ItemCondition.values()[which]
            item?.condition = condition
            conditionTV.text = condition.name.formatEnum()
        }
    }

    fun moreOptions(v: View){
        Actions.showAlert(this, "More options", null, arrayListOf(
            "Delete Item"
        )){_, which ->
            when (which){
                0 -> {
                    // DELETE THIS ITEM
                }
            }
        }
    }

    private fun getExtras(){
        intent.extras?.let {
            item = it.getSerializable("item") as? Item
            isUpdate = it.getBoolean("isUpdate")

            item?.run {
                Actions.showImage(image, imageIV)
                nameET.setText(name)
                descriptionET.setText(description)
                stock?.let { stock ->
                    stockET.setText(stock)
                }
                priceET.setText(price.toString())
            }

            when (isUpdate){
                true -> {
                    updateB.text = "Update"
                    conditionTV.text = item!!.condition!!.name.formatEnum()
                }
                false -> {
                    updateB.text = "Add Item"
                    optionsTV.isVisible = false
                }
                else -> {
                    // PAGE SHOULDN'T GET HERE
                    finish()
                }
            }
        }
    }

    fun goBack(v: View){
        onBackPressed()
    }
}