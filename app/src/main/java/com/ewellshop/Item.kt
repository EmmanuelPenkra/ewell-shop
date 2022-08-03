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
import com.ewellshop.popups.ImagePopup
import kotlinx.android.synthetic.main.activity_item.*
import kotlinx.android.synthetic.main.activity_pin.*
import java.lang.Math.round
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

        imageIV.setOnClickListener { showImage() }

        setupFields()
        getExtras()
    }

    private fun showImage(){
        ImagePopup(item!!.image).show(supportFragmentManager, "imagePopup")
    }

    private fun updateOrAddItem(){
        val item = item!!

        if (item.condition == null){
            Actions.showAlert(this, null, "Select Item's Condition")
            return
        }

        val params = hashMapOf (
            "id" to item.id,
            "name" to nameET.text,
            "description" to descriptionET.text,
            "price" to price.replace(",", ""),
            "condition" to item.condition!!.value,
            "businessID" to actions.getInfo("businessID", DataType.INTEGER)!!
//            "employeeID" to actions.getInfo("employeeID", DataType.INTEGER)!!
        )

        Internet(this, DOMAIN + "v1/" + (if (isUpdate!!) "update-item" else "add-item"), params,
            true){
            Actions.showAlert(this, "Successful", it.getString("message"), "Done", { _, _ ->
                setResult(RESULT_OK)
                finish()
            })
        }
    }

    private fun setupFields(){
        priceET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != price) {
                    priceET.removeTextChangedListener(this)

                    val cleanString: String = s!!.replace("""[$,.]""".toRegex(), "")
                    val parsed = cleanString.toDoubleOrNull() ?: return

                    val formatted = (parsed / 100).currency()

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
                    Actions.showAlert(this, "Are you sure?", "You won't be able to recover this " +
                            "item", "Yes, I am", { _, _ ->
                        Internet(this, DOMAIN + "v1/delete-item", hashMapOf(
                            "itemID" to item!!.id
                        ), true){
                            Actions.showAlert(this, "Item deleted successfully!", null, "Done",
                                { _, _ ->
                                    finish()
                                })
                        }
                    }, true)
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
                priceET.setText(price.currency())
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